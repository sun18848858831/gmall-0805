package com.atguigu.gmall.search.pojo;

import lombok.Data;

import java.util.List;

/**
 * @author shkstart
 * @create 2020-01-10 21:10
 */

@Data
public class SearchParam {

    private String key;

    private Long[] catelog3;

    private Long[] brand;

    private Double priceFrom;

    private Double priceTo;

    private List<String> props;

    private String order;

    private Integer pageNum = 1;

    private Integer pageSize = 64;

    private Boolean store;


}
