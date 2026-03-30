package com.sky.controller.admin;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sky.dto.CategoryPageQueryDTO;
import com.sky.entity.Category;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.CategoryService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/admin/category")
@Api(tags = "分类相关接口")
@Slf4j
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @PostMapping
    @ApiOperation("新增分类")
    /**
     * 作用: 执行save相关逻辑。
     * 输入: @RequestBody com.sky.dto.CategoryDTO categoryDTO。
     * 输出: Result<String>。
     */
    public Result<String> save(@RequestBody com.sky.dto.CategoryDTO categoryDTO) {
        log.info("新增分类：{}", categoryDTO);
        categoryService.save(categoryDTO);
        return Result.success();
    }

    @GetMapping("/page")
    @ApiOperation("分类分页查询")
    /**
     * 作用: 执行page相关逻辑。
     * 输入: CategoryPageQueryDTO categoryPageQueryDTO。
     * 输出: Result<PageResult>。
     */
    public Result<PageResult> page(CategoryPageQueryDTO categoryPageQueryDTO) {
        log.info("分页查询：{}", categoryPageQueryDTO);
        Page<Category> p = categoryService
                .queryCategoryByPage(categoryPageQueryDTO);
        return Result.success(new PageResult(p.getTotal(), p.getRecords()));
    }

    @DeleteMapping
    @ApiOperation("删除分类")
    /**
     * 作用: 执行deleteById相关逻辑。
     * 输入: @RequestParam("id") Long id。
     * 输出: Result<String>。
     */
    public Result<String> deleteById(@RequestParam("id") Long id) {
        log.info("删除分类：{}", id);
        categoryService.deleteById(id);
        return Result.success();
    }

    @PutMapping
    @ApiOperation("修改分类")
    /**
     * 作用: 执行update相关逻辑。
     * 输入: @RequestBody com.sky.dto.CategoryDTO categoryDTO。
     * 输出: Result<String>。
     */
    public Result<String> update(@RequestBody com.sky.dto.CategoryDTO categoryDTO) {
        categoryService.update(categoryDTO);
        return Result.success();
    }

    @PostMapping("/status/{status}")
    @ApiOperation("启用/禁用分类")
    /**
     * 作用: 执行startOrStop相关逻辑。
     * 输入: @PathVariable("status") Integer status, Long id。
     * 输出: Result<String>。
     */
    public Result<String> startOrStop(@PathVariable("status") Integer status, Long id) {
        categoryService.startOrStop(status, id);
        return Result.success();
    }

    @GetMapping("/list")
    @ApiOperation("根据类型查询分类")
    /**
     * 作用: 执行list相关逻辑。
     * 输入: Integer type。
     * 输出: Result<List<Category>>。
     */
    public Result<List<Category>> list(Integer type) {
        List<Category> list = categoryService.list(type);
        return Result.success(list);
    }
}
