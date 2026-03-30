package com.sky.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sky.constant.StatusConstant;
import com.sky.dto.CategoryDTO;
import com.sky.api.client.DishClient;
import com.sky.api.client.SetmealClient;
import com.sky.dto.CategoryPageQueryDTO;
import com.sky.entity.Category;
import com.sky.mapper.CategoryMapper;
import com.sky.service.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {

    @Autowired
    private CategoryMapper categoryMapper;
    @Autowired
    private DishClient dishClient;
    @Autowired
    private SetmealClient setmealClient;

    @Override
    @Transactional
    /**
     * 作用: 执行save相关逻辑。
     * 输入: CategoryDTO categoryDTO。
     * 输出: 无。
     */
    public void save(CategoryDTO categoryDTO) {
        Category category = new Category();
        BeanUtils.copyProperties(categoryDTO, category);
        category.setStatus(StatusConstant.DISABLE);
        categoryMapper.insert(category);
        dishClient.syncCategory(category);
        setmealClient.syncCategory(category);
    }

    @Override
    @Transactional
    /**
     * 作用: 执行deleteById相关逻辑。
     * 输入: Long id。
     * 输出: 无。
     */
    public void deleteById(Long id) {
        categoryMapper.deleteById(id);
        dishClient.deleteCategory(id);
        setmealClient.deleteCategory(id);
    }

    @Override
    @Transactional
    /**
     * 作用: 执行update相关逻辑。
     * 输入: CategoryDTO categoryDTO。
     * 输出: 无。
     */
    public void update(CategoryDTO categoryDTO) {
        Category category = new Category();
        BeanUtils.copyProperties(categoryDTO, category);
        categoryMapper.updateById(category);
        Category categoryDB = categoryMapper.selectById(category.getId());
        if (categoryDB != null) {
            dishClient.syncCategory(categoryDB);
            setmealClient.syncCategory(categoryDB);
        }
    }

    @Override
    @Transactional
    /**
     * 作用: 执行startOrStop相关逻辑。
     * 输入: Integer status, Long id。
     * 输出: 无。
     */
    public void startOrStop(Integer status, Long id) {
        Category category = Category.builder()
                .id(id)
                .status(status)
                .build();
        categoryMapper.updateById(category);
        Category categoryDB = categoryMapper.selectById(id);
        if (categoryDB != null) {
            dishClient.syncCategory(categoryDB);
            setmealClient.syncCategory(categoryDB);
        }
    }

    @Override
    /**
     * 作用: 执行list相关逻辑。
     * 输入: Integer type。
     * 输出: List<Category>。
     */
    public List<Category> list(Integer type) {
        return categoryMapper.selectList(new LambdaQueryWrapper<Category>()
                .eq(Category::getStatus, StatusConstant.ENABLE)
                .eq(type != null, Category::getType, type)
                .orderByAsc(Category::getSort)
                .orderByDesc(Category::getCreateTime));
    }

    @Override
    /**
     * 作用: 执行queryCategoryByPage相关逻辑。
     * 输入: CategoryPageQueryDTO categoryPageQueryDTO。
     * 输出: Page<Category>。
     */
    public Page<Category> queryCategoryByPage(CategoryPageQueryDTO categoryPageQueryDTO) {
        Page<Category> page = new Page<>(categoryPageQueryDTO.getPage(), categoryPageQueryDTO.getPageSize());
        categoryMapper.selectPage(page, new LambdaQueryWrapper<Category>()
                .eq(categoryPageQueryDTO.getType() != null, Category::getType, categoryPageQueryDTO.getType())
                .like(categoryPageQueryDTO.getName() != null && !categoryPageQueryDTO.getName().isEmpty(),
                        Category::getName, categoryPageQueryDTO.getName())
                .orderByAsc(Category::getSort)
                .orderByDesc(Category::getCreateTime));
        return page;
    }
}
