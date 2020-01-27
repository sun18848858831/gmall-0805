package com.atguigu.gmall.search.feign;

import com.atguigu.gmall.pms.api.GmallPmsApi;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * @author shkstart
 * @create 2020-01-09 17:13
 */
@FeignClient("pms-service")
public interface GmallPmsCliet extends GmallPmsApi {
}
