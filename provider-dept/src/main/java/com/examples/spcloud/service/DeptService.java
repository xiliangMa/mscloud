package com.examples.spcloud.service;

import com.examples.spcloud.entity.Dept;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Create by $(xiliangMa) on 2019-07-10
 */
public interface DeptService {
    public boolean add(Dept dept);
    public Dept get(long id);
    public List<Dept> list();
}
