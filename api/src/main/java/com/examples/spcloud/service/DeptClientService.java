package com.examples.spcloud.service;


import com.examples.spcloud.entity.Dept;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;

/**
 * Create by $(xiliangMa) on 2019-07-11
 */

@FeignClient(value = "MSCLOUD.PROVIDER-DEPT.COM")
public interface DeptClientService {

    @RequestMapping(value = "/consumer/dept/get/{id}", method = RequestMethod.GET)
    public Dept get(@PathVariable("id") Long id);


    @RequestMapping(value = "/consumer/dept/list", method = RequestMethod.GET)
    public List<Dept> list();

    @RequestMapping(value = "/consumer/dept/add", method = RequestMethod.POST)
    public boolean add(Dept dept);
}
