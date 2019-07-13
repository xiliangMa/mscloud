package com.examples.spcloud.consumer.hystrix;

import com.examples.spcloud.consumer.service.ConsumerFeignService;
import org.springframework.stereotype.Component;

/**
 * Create by $(xiliangMa) on 2019-07-13
 */

@Component
public class ConsumerFeignServiceHystrix implements ConsumerFeignService {
    public String port() {
        return "熔断测试返回结果";
    }
}
