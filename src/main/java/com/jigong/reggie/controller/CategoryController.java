package com.jigong.reggie.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jigong.reggie.commom.Result;
import com.jigong.reggie.entity.Category;
import com.jigong.reggie.service.impl.CategoryServiceImpl;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@Api(tags = "分类相关的接口")
@RequestMapping("/category")
public class CategoryController {
    @Autowired
    public CategoryServiceImpl categoryServiceImpl;

    /**
     * 新增分类
     * @param category
     * @return
     */
    @PostMapping
    @ApiOperation("新增分类接口")
    public Result<String> save(@RequestBody Category category){
        categoryServiceImpl.saveCate(category);
        return Result.success("新增分类成功");
    }

    @GetMapping("/page")
    @ApiOperation("分类分页接口")
    public Result<Page> page(int page,int pageSize){
        Page<Category> pageRes =  categoryServiceImpl.page(page,pageSize);
        return Result.success(pageRes);
    }

    @PutMapping
    @ApiOperation("分类更新接口")
    public Result<String> update(@RequestBody Category category){
        categoryServiceImpl.updateCate(category);
        return Result.success("菜品更新失败");
    }


    @DeleteMapping
    @ApiOperation("删除分类接口")
    public Result<String> remove(Long id){
        log.info("删除分类，id为:{}",id);
        categoryServiceImpl.remove(id);
        return Result.success("分类删除成功");
    }

    @GetMapping("/list")
    @ApiOperation("分类列表显示")
    public Result<List<Category>> list(Category category){
        List<Category> list = categoryServiceImpl.list(category);
        return Result.success(list);
    }
}
