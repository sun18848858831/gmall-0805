package com.atguigu.gmall.pms.vo;

import com.atguigu.gmall.pms.entity.SpuInfoEntity;
import lombok.Data;


import java.util.List;

/**
 * @author shkstart
 * @create 2020-01-05 19:15
 */
@Data
public class SpuInfoVO extends SpuInfoEntity {

    //private List<Spring> spuImages;    //spu描述信息（图片）
    private List<String> spuImages;

    private List<BaseAttrValueVO> baseAttrs;     //通用的规格属性

    private List<SkuInfoVO> skus;
}
