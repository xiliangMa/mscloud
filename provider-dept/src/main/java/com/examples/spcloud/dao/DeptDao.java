package com.examples.spcloud.dao;

import com.examples.spcloud.entity.Dept;

import java.util.List;

/**
 * Create by $(xiliangMa) on 2019-07-10
 */

public interface DeptDao {
    public boolean addDept(Dept dept);
    public Dept findById(long id);
    public List<Dept> finfAll();
}
