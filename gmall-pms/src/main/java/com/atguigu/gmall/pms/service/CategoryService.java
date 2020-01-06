package com.atguigu.gmall.pms.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.atguigu.gmall.pms.entity.CategoryEntity;
import com.atguigu.core.bean.PageVo;
import com.atguigu.core.bean.QueryCondition;
import io.swagger.models.properties.PropertyBuilder;

import java.util.List;
import java.util.logging.Level;


/**
 * 商品三级分类
 *
 * @author syh
 * @email lxf@atguigu.com
 * @date 2020-01-02 14:48:39
 */
public interface CategoryService extends IService<CategoryEntity> {

    PageVo queryPage(QueryCondition params);

    List<CategoryEntity> queryCategoriesByLevelOrPid(Integer level, Long pid);
}

