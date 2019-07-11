package com.examples.spcloud.service.impl;

import com.examples.spcloud.dao.DeptDao;
import com.examples.spcloud.entity.Dept;
import com.examples.spcloud.service.DeptService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Create by $(xiliangMa) on 2019-07-10
 */
@Service
public class DeptServiceImpl implements DeptService {
    @Autowired
    private DeptDao deptDao;

    public boolean add(Dept dept) {
        return deptDao.addDept(dept);
    }

    public Dept get(long id) {
        return deptDao.findById(id);
    }

    public List<Dept> list() {
        return deptDao.finfAll();
    }
}
