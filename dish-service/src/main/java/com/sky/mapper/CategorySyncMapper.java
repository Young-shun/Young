package com.sky.mapper;

import org.apache.ibatis.annotations.Mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sky.entity.Category;

@Mapper
public interface CategorySyncMapper extends BaseMapper<Category> {
}
