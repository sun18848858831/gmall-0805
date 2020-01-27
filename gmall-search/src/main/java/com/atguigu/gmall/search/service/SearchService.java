package com.atguigu.gmall.search.service;

import com.atguigu.gmall.search.pojo.SearchParam;
import com.atguigu.gmall.search.pojo.SearchResponseVO;

import java.io.IOException;

/**
 * @author shkstart
 * @create 2020-01-11 11:32
 */
public interface SearchService {

    public SearchResponseVO search(SearchParam searchParam) throws IOException;


}
