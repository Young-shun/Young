package com.sky.controller.inner;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sky.entity.Category;
import com.sky.mapper.CategorySyncMapper;
import com.sky.result.Result;

@RestController
@RequestMapping("/inner/setmeal/category")
public class InnerSetmealCategoryController {

  @Autowired
  private CategorySyncMapper categorySyncMapper;

  @PostMapping("/sync")
  public Result<String> sync(@RequestBody Category category) {
    if (category.getId() == null) {
      return Result.error("category id is required");
    }
    Category existing = categorySyncMapper.selectById(category.getId());
    if (existing == null) {
      categorySyncMapper.insert(category);
    } else {
      categorySyncMapper.updateById(category);
    }
    return Result.success();
  }

  @DeleteMapping("/{id}")
  public Result<String> delete(@PathVariable("id") Long id) {
    categorySyncMapper.deleteById(id);
    return Result.success();
  }
}
