package com.atguigu.gmall.sms.api;

import com.atguigu.core.bean.Resp;
import com.atguigu.gmall.sms.vo.SaleVO;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @author shkstart
 * @create 2020-01-06 19:15
 */
public interface GmallSmsApi {

    @PostMapping("sms/skubounds/sales")
    public Resp<Object> saveSales(@RequestBody SaleVO saleVO);
}
