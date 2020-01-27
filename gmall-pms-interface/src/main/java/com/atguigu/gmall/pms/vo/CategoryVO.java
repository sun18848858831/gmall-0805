package com.atguigu.gmall.pms.vo;

import com.atguigu.gmall.pms.entity.CategoryEntity;
import lombok.Data;

import java.util.List;

/**
 * @author shkstart
 * @create 2020-01-13 20:01
 */

@Data
public class CategoryVO extends CategoryEntity {

    private List<CategoryEntity> subs;
}
