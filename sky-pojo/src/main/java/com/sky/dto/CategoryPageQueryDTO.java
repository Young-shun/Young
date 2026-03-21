package com.sky.dto;

import lombok.Data;

import java.io.Serializable;

import com.sky.query.PageQuery;

@Data
public class CategoryPageQueryDTO extends PageQuery implements Serializable {

    // 分类名称
    private String name;

    // 分类类型 1菜品分类 2套餐分类
    private Integer type;

}
