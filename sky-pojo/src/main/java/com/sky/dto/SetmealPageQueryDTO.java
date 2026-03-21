package com.sky.dto;

import lombok.Data;

import java.io.Serializable;

import com.sky.query.PageQuery;

@Data
public class SetmealPageQueryDTO extends PageQuery implements Serializable {

    private String name;

    // 分类id
    private Integer categoryId;

    // 状态 0表示禁用 1表示启用
    private Integer status;

}
