package com.examples.spcloud.consumer.controller;

import com.examples.spcloud.consumer.service.ConsumerRibbonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * Create by $(xiliangMa) on 2019-07-13
 */

@RestController
public class ConsumerRibbonServiceController {

    @Autowired
    private ConsumerRibbonService service;


    @RequestMapping(value = "/consumer/port", method = RequestMethod.GET)
    public String port() {
        return service.port();
    }
}
