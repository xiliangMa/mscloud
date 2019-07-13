package com.examples.spcloud.consumer.service;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

/**
 * Create by $(xiliangMa) on 2019-07-13
 */

@Service
public class ConsumerRibbonService {

    private final static String URL = "http://PROVIDER-SERVICE/provider/port";

    @Autowired
    private RestTemplate restTemplate;


    @HystrixCommand(fallbackMethod = "hystrixError")
    public String port() {
        return restTemplate.getForObject(URL, String.class);
    }

    public String hystrixError() {
        return "熔断测试返回结果";
    }
}
