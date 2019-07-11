package com.examples.eureka3;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

/**
 * Create by $(xiliangMa) on 2019-07-11
 */

@SpringBootApplication
@EnableEurekaServer
public class EurekaApplication3 {
    public static void main(String[] args) {
        SpringApplication.run(EurekaApplication3.class, args);
    }
}
