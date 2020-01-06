package com.atguigu.gmall.sms.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author shkstart
 * @create 2020-01-06 16:57
 */
@Data
public class SaleVO {
    private Long skuId;

    private BigDecimal growBounds;

    private BigDecimal buyBounds;

    private List<String> work;


    private BigDecimal fullPrice;

    private BigDecimal reducePrice;

    private Integer fullAddOther;


    private Integer fullCount;

    private BigDecimal discount;

    private Integer ladderAddOther;
}
