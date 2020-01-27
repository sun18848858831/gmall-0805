package com.atguigu.gmall.index.feign;

import com.atguigu.gmall.pms.api.GmallPmsApi;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * @author shkstart
 * @create 2020-01-13 19:28
 */

@FeignClient("pms-service")
public interface GmallPmsClient extends GmallPmsApi {
}
