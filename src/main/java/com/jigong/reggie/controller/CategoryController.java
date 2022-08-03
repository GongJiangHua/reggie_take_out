package com.jigong.reggie.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jigong.reggie.commom.Result;
import com.jigong.reggie.entity.Category;
import com.jigong.reggie.service.impl.CategoryServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
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
    public Result<String> save(@RequestBody Category category){
        categoryServiceImpl.saveCate(category);
        return Result.success("新增分类成功");
    }

    @GetMapping("/page")
    public Result<Page> page(int page,int pageSize){
        Page<Category> pageRes =  categoryServiceImpl.page(page,pageSize);
        return Result.success(pageRes);
    }

    @PutMapping
    public Result<String> update(@RequestBody Category category){
        categoryServiceImpl.updateCate(category);
        return Result.success("菜品更新失败");
    }


    @DeleteMapping
    public Result<String> remove(Long id){
        log.info("删除分类，id为:{}",id);
        categoryServiceImpl.remove(id);
        return Result.success("分类删除成功");
    }

    @GetMapping("/list")
    public Result<List<Category>> list(Category category){
        List<Category> list = categoryServiceImpl.list(category);
        return Result.success(list);
    }
}
