package com.examples.spcloud.controller;

import com.examples.spcloud.entity.Dept;
import com.examples.spcloud.service.DeptService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Create by $(xiliangMa) on 2019-07-10
 */

@RestController
public class DeptController {

    @Autowired
    private DeptService deptService;

    @RequestMapping(value = "/dept/add", method = RequestMethod.POST, produces = "application/json")
    public boolean add(@RequestBody Dept dept) {
        return deptService.add(dept);
    }


    @RequestMapping(value = "/dept/get/{id}", method = RequestMethod.GET, produces = "application/json")
    public Dept get(@PathVariable("id") Long id) {
        return deptService.get(id);
    }

    @RequestMapping(value = "/dept/list", method = RequestMethod.GET, produces = "application/json")
    public List<Dept> list() {
        return deptService.list();
    }


}
