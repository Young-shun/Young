package com.sky.dto;

import lombok.Data;

import java.io.Serializable;

import com.sky.query.PageQuery;

@Data
public class EmployeePageQueryDTO extends PageQuery implements Serializable {

    // 员工姓名
    private String name;

}
