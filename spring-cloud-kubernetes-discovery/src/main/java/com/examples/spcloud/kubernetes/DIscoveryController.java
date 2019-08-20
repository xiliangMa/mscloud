package com.examples.spcloud.kubernetes;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Create by $(xiliangMa) on 2019-08-20
 */

@RestController
public class DIscoveryController {

    private static final Log log = LogFactory.getLog(DIscoveryController.class);

    // 定义服务发现客户端
    @Autowired
    private DiscoveryClient discoveryClient;


    // 获取kubernetes 集群中的service， 是不是很简单，以后可以使用kubernetes 服务发现了，不在使用eureka了
    @RequestMapping("services")
    public List<String> Services() {
        return this.discoveryClient.getServices();
    }



}
