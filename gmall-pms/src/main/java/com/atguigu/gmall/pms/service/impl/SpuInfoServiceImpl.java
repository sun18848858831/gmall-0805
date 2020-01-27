package com.atguigu.gmall.pms.service.impl;

import com.atguigu.gmall.pms.dao.*;
import com.atguigu.gmall.pms.entity.*;
import com.atguigu.gmall.pms.feign.GmallSmsClient;
import com.atguigu.gmall.pms.service.*;
import com.atguigu.gmall.pms.vo.BaseAttrValueVO;
import com.atguigu.gmall.pms.vo.SkuInfoVO;
import com.atguigu.gmall.pms.vo.SpuInfoVO;
import com.atguigu.gmall.sms.vo.SaleVO;
import io.seata.spring.annotation.GlobalTransactional;
import org.apache.commons.lang.StringUtils;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.core.bean.PageVo;
import com.atguigu.core.bean.Query;
import com.atguigu.core.bean.QueryCondition;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;


@Service("spuInfoService")
public class SpuInfoServiceImpl extends ServiceImpl<SpuInfoDao, SpuInfoEntity> implements SpuInfoService {

    @Autowired
    private SpuInfoDescDao descDao;

    @Autowired
    //private ProductAttrValueDao attrValueDao; 不用它，好像它没有存集合的方法
    private ProductAttrValueService attrValueService;

    @Autowired
    private SkuInfoDao skuInfoDao;

    @Autowired
    //private SkuImagesDao imagesDao;   没有批量保存图片的方法好像
    private SkuImagesService imagesService;

    @Autowired
    private SkuSaleAttrValueService saleAttrValueService;

    @Autowired
    private GmallSmsClient smsClient;

    @Autowired
    private SpuInfoDescService descService;

    @Autowired
    private AmqpTemplate amqpTemplate;


    @Override
    public PageVo queryPage(QueryCondition params) {
        IPage<SpuInfoEntity> page = this.page(
                new Query<SpuInfoEntity>().getPage(params),
                new QueryWrapper<SpuInfoEntity>()
        );

        return new PageVo(page);
    }

    @Override
    public PageVo querySpuByCidOrKey(QueryCondition condition, Long catId) {

        QueryWrapper<SpuInfoEntity> wrapper = new QueryWrapper<>();

        //判断差全站还是差本类
        if(catId !=0l){
            wrapper.eq("catalog_id",catId);
        }

        //关键字
        String key = condition.getKey();
        if(StringUtils.isNotBlank(key)){
            wrapper.and(w->w.eq("id",key).or().like("spu_name",key));
        }

        IPage<SpuInfoEntity> page = this.page(new Query<SpuInfoEntity>().getPage(condition), wrapper);
        return new PageVo(page);
    }

    //受检异常和非受检异常
    //@Transactional(rollbackFor = FileNotFoundException.class,noRollbackFor = ArithmeticException.class)
    //测试超时
    //@Transactional(timeout = 3)
    //测试只读
    //@Transactional(readOnly = true)
    @GlobalTransactional
    @Override
    public void bigSave(SpuInfoVO spuInfoVO) throws FileNotFoundException {
        //1.保存spu相关信息
        //1.1.spuInfo
        Long spuId = this.saveSpuInfo(spuInfoVO);

        //1.2.spuInfoDesc  spu描述信息
        this.descService.saveSpuDesc(spuInfoVO, spuId);

        //测试超时使用
//        try {
//            TimeUnit.SECONDS.sleep(4);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }


        //1.3.基础属性相关信息
        this.saveBaseAttrValue(spuInfoVO, spuId);


        //2.sku相关信息
        //List<SkuInfoVO> skus = new SpuInfoVO().getSkus();
        this.saveSkuAndSales(spuInfoVO, spuId);

        //测试受检异常和非受检异常使用
        //FileInputStream inputStream = new FileInputStream("xxx");
        //int i = 1/0;

        sendMsg(spuId,"insert");


    }

    private void sendMsg(Long spuId,String type) {
        this.amqpTemplate.convertAndSend("GMALL-PMS-EXCHANGE","item."+type,spuId);
    }

    private void saveSkuAndSales(SpuInfoVO spuInfoVO, Long spuId) {
        List<SkuInfoVO> skus = spuInfoVO.getSkus();
        if(CollectionUtils.isEmpty(skus)){
            return;
        }

        skus.forEach(sku->{
            //2.1.skuInfo
            SkuInfoEntity skuInfoEntity = new SkuInfoEntity();
            BeanUtils.copyProperties(sku,skuInfoEntity);
            skuInfoEntity.setSpuId(spuId);
            List<String> images = sku.getImages();
            if(!CollectionUtils.isEmpty(images)){
                skuInfoEntity.setSkuDefaultImg(skuInfoEntity.getSkuDefaultImg()==null?images.get(0):skuInfoEntity.getSkuDefaultImg());
            }
            skuInfoEntity.setSkuCode(UUID.randomUUID().toString());
            skuInfoEntity.setCatalogId(spuInfoVO.getCatalogId());
            skuInfoEntity.setBrandId(spuInfoVO.getBrandId());
            this.skuInfoDao.insert(skuInfoEntity);
            Long skuId = skuInfoEntity.getSkuId();
            //2.2.skuInfoImages
            if(!CollectionUtils.isEmpty(images)){
                List<SkuImagesEntity> imagesEntities = images.stream().map(image -> {
                    SkuImagesEntity imagesEntity = new SkuImagesEntity();
                    imagesEntity.setSkuId(skuId);
                    imagesEntity.setImgUrl(image);
                    imagesEntity.setImgSort(0);
                    imagesEntity.setDefaultImg(StringUtils.equals(image, skuInfoEntity.getSkuDefaultImg()) ? 1 : 0);
                    return imagesEntity;
                }).collect(Collectors.toList());
                imagesService.saveBatch(imagesEntities);
            }

            //2.3.skuSaleAttrValue
            List<SkuSaleAttrValueEntity> saleAttrs = sku.getSaleAttrs();
            if(!CollectionUtils.isEmpty(saleAttrs)){
                saleAttrs.forEach(skuSaleAttrValueEntity->{
                    skuSaleAttrValueEntity.setSkuId(skuId);
                    skuSaleAttrValueEntity.setAttrSort(0);
                });
                saleAttrValueService.saveBatch(saleAttrs);
            }

            //3.营销相关信息
            //SaleVO saleVO = new SaleVO();
            SaleVO saleVO = new SaleVO();
            BeanUtils.copyProperties(sku,saleVO);
            saleVO.setSkuId(skuId);
            this.smsClient.saveSales(saleVO);

        });
    }

    private void saveBaseAttrValue(SpuInfoVO spuInfoVO, Long spuId) {
        List<BaseAttrValueVO> baseAttrs = spuInfoVO.getBaseAttrs();
        if(!CollectionUtils.isEmpty(baseAttrs)){
            List<ProductAttrValueEntity> attrValues = baseAttrs.stream().map(baseAttrValueVO -> {
                ProductAttrValueEntity attrValueEntity = new ProductAttrValueEntity();
                BeanUtils.copyProperties(baseAttrValueVO, attrValueEntity);
                attrValueEntity.setSpuId(spuId);
                attrValueEntity.setAttrSort(0);
                attrValueEntity.setQuickShow(0);
                return attrValueEntity;
            }).collect(Collectors.toList());
            this.attrValueService.saveBatch(attrValues);
        }
    }


    private Long saveSpuInfo(SpuInfoVO spuInfoVO) {
        spuInfoVO.setCreateTime(new Date());
        spuInfoVO.setUodateTime(spuInfoVO.getCreateTime());
        this.save(spuInfoVO);
        return spuInfoVO.getId();
    }

}