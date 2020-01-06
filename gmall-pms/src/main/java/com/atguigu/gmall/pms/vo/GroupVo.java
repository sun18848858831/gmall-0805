package com.atguigu.gmall.pms.vo;

import com.atguigu.gmall.pms.entity.AttrAttrgroupRelationEntity;
import com.atguigu.gmall.pms.entity.AttrEntity;
import com.atguigu.gmall.pms.entity.AttrGroupEntity;
import lombok.Data;

import java.util.List;

/**
 * @author shkstart
 * @create 2020-01-04 16:12
 */

@Data
public class GroupVo extends AttrGroupEntity {

    private List<AttrEntity> attrEntities;
    private List<AttrAttrgroupRelationEntity> relations;
}
