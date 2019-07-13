package com.examples.spcloud.consumer.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

/**
 * Create by $(xiliangMa) on 2019-07-13
 */

@Service
public class ConsumerService {

    private final static String URL = "http://PROVIER-SERVICE/provider/port";

    @Autowired
    private RestTemplate restTemplate;


    public String port() {
        return restTemplate.getForObject(URL, String.class);
    }
}
