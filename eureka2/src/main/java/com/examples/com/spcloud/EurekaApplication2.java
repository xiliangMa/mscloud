package com.examples.com.spcloud;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

/**
 * Create by $(xiliangMa) on 2019-07-11
 */

@SpringBootApplication
@EnableEurekaServer
public class EurekaApplication2 {
    public static void main(String[] args) {
        SpringApplication.run(EurekaApplication2.class, args);
    }
}
