package com.examples.spcloud;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

/**
 * Create by $(xiliangMa) on 2019-07-10
 */

@SpringBootApplication
@EnableEurekaClient
@MapperScan(value = {"com.examples.spcloud.dao"})
public class DeptProviderApplication {
    public static void main(String[] args) {
        SpringApplication.run(DeptProviderApplication.class, args);
    }
}
