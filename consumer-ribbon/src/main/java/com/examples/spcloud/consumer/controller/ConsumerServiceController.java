package com.examples.spcloud.consumer.controller;

import com.examples.spcloud.consumer.service.ConsumerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

/**
 * Create by $(xiliangMa) on 2019-07-13
 */

@RestController
public class ConsumerServiceController {

    @Autowired
    private ConsumerService service;


    @RequestMapping(value = "/consumer/port", method = RequestMethod.GET)
    public String port() {
        return service.port();
    }
}
