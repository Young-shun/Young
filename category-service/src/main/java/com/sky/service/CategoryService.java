package com.sky.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.sky.entity.Category;
import java.util.List;

public interface CategoryService extends IService<Category> {
    /**
     * 作用: 执行save相关逻辑。
     * 输入: com.sky.dto.CategoryDTO categoryDTO。
     * 输出: 无。
     */
    void save(com.sky.dto.CategoryDTO categoryDTO);
    /**
     * 作用: 执行deleteById相关逻辑。
     * 输入: Long id。
     * 输出: 无。
     */
    void deleteById(Long id);
    /**
     * 作用: 执行update相关逻辑。
     * 输入: com.sky.dto.CategoryDTO categoryDTO。
     * 输出: 无。
     */
    void update(com.sky.dto.CategoryDTO categoryDTO);
    /**
     * 作用: 执行startOrStop相关逻辑。
     * 输入: Integer status, Long id。
     * 输出: 无。
     */
    void startOrStop(Integer status, Long id);
    List<Category> list(Integer type);
    Page<Category> queryCategoryByPage(com.sky.dto.CategoryPageQueryDTO categoryPageQueryDTO);
}
