package com.atguigu.gmall.index.controller;

import com.atguigu.core.bean.Resp;
import com.atguigu.gmall.index.service.IndexService;
import com.atguigu.gmall.index.service.impl.IndexServiceImpl;
import com.atguigu.gmall.pms.entity.CategoryEntity;
import com.atguigu.gmall.pms.vo.CategoryVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author shkstart
 * @create 2020-01-13 19:16
 */

@RestController
@RequestMapping("index")
public class IndexController {

    @Autowired
    private IndexService indexService;

    @GetMapping("cates")
    public Resp<List<CategoryEntity>> queryLvl1Categories(){
        List<CategoryEntity> categoryEntities = this.indexService.queryLvl1Categories();
        return Resp.ok(categoryEntities);
    }

    @GetMapping("cates/{pid}")
    public Resp<List<CategoryVO>> queryCategoresWithSub(@PathVariable("pid")Long pid){
        List<CategoryVO> categoryVOS = this.indexService.queryCategoresWithSub(pid);
        return Resp.ok(categoryVOS);
    }

    @GetMapping("test/lock")
    public Resp<Object> testLock(){
        this.indexService.testLock();
        return Resp.ok(null);
    }
}
