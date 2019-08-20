package com.examples.spcloud.kubernetes;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * Create by $(xiliangMa) on 2019-08-20
 */

// 添加服务发现客户端注解
@SpringBootApplication
@EnableDiscoveryClient
public class CloudKubernetesDiscoveryApp {
    public static void main(String[] args) {
        SpringApplication.run(CloudKubernetesDiscoveryApp.class, args);
    }
}
