package com.examples.spcloud.provider.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * Create by $(xiliangMa) on 2019-07-13
 */

@RestController
public class ProviderServiceController {

    @Value("${server.port}")
    private String port;

    @RequestMapping(value = "/provider/port", method = RequestMethod.GET)
    public String getPort() {
        return String.format("Your provider Service port is: %s ", port);
    }
}
