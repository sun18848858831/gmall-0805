package com.atguigu.gmall.pms.feign;

import com.atguigu.gmall.sms.api.GmallSmsApi;
import org.springframework.cloud.openfeign.FeignClient;
/**
 * @author shkstart
 * @create 2020-01-06 18:34
 */

@FeignClient("sms-service")
public interface GmallSmsClient extends GmallSmsApi {


}
