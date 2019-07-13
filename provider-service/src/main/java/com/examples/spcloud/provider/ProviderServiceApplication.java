package com.examples.spcloud.provider;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

/**
 * Create by $(xiliangMa) on 2019-07-13
 */

@SpringBootApplication
@EnableEurekaClient
public class ProviderServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(ProviderServiceApplication.class, args);
    }
}
