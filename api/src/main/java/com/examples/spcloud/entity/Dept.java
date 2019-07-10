package com.examples.spcloud.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;

@Data
@SuppressWarnings("serial")
@NoArgsConstructor
@Accessors(chain=true)
public class Dept implements Serializable {
    private Long deptNo;
    private String dName;
    private String dbSource;

    public Dept(String dName) {
        super();
        this.dName = dName;
    }

    public static void main(String[] args) {
        Dept dept = new Dept();
        dept.setDName("虚拟化").setDeptNo(11l).setDbSource("DB1");
    }
}
