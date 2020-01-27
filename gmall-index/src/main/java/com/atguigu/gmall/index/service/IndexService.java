package com.atguigu.gmall.index.service;

import com.atguigu.gmall.pms.entity.CategoryEntity;
import com.atguigu.gmall.pms.vo.CategoryVO;

import java.util.List;

/**
 * @author shkstart
 * @create 2020-01-13 19:21
 */
public interface IndexService {
    List<CategoryEntity> queryLvl1Categories();

    List<CategoryVO> queryCategoresWithSub(Long pid);

    void testLock();
}
