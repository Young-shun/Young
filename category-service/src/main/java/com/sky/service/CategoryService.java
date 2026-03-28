package com.sky.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.sky.entity.Category;
import java.util.List;

public interface CategoryService extends IService<Category> {
    void save(com.sky.dto.CategoryDTO categoryDTO);
    void deleteById(Long id);
    void update(com.sky.dto.CategoryDTO categoryDTO);
    void startOrStop(Integer status, Long id);
    List<Category> list(Integer type);
    Page<Category> queryCategoryByPage(com.sky.dto.CategoryPageQueryDTO categoryPageQueryDTO);
}
