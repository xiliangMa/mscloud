package com.examples.spcloud.consumer.service;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Create by $(xiliangMa) on 2019-07-13
 */

@FeignClient(value = "PROVIER-SERVICE")
public interface ConsumerFeignService {

    // 这里的url为调用服务端的restapi地址
    @RequestMapping(value = "/provider/port", method = RequestMethod.GET)
    public String port();
}
