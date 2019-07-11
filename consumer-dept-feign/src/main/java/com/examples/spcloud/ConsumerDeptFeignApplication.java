package com.examples.spcloud;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * Create by $(xiliangMa) on 2019-07-11
 */

@SpringBootApplication
@EnableEurekaClient
@EnableFeignClients(basePackages = {"com.examples.spcloud"})
@EnableDiscoveryClient
public class ConsumerDeptFeignApplication {
    public static void main(String[] args) {
        SpringApplication.run(ConsumerDeptFeignApplication.class, args);
    }
}
