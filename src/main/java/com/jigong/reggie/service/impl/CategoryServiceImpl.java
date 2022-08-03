package com.jigong.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jigong.reggie.commom.MyCustomException;
import com.jigong.reggie.entity.Category;
import com.jigong.reggie.entity.Dish;
import com.jigong.reggie.entity.Setmeal;
import com.jigong.reggie.mapper.CategoryMapper;
import com.jigong.reggie.service.CategoryService;
import com.jigong.reggie.service.DishService;
import com.jigong.reggie.service.SetmealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {
    @Autowired
    public DishService dishService;
    @Autowired
    public SetmealService setmealService;

    public boolean saveCate(Category category) {
        return this.save(category);
    }

    public Page<Category> page(int page, int pageSize) {
        //分页构造器
        Page<Category> pageInfo = new Page<>(page, pageSize);
        //条件构造器
        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper();
        //添加查询条件，sort排序
        queryWrapper.orderByAsc(Category::getSort);

        return this.page(pageInfo, queryWrapper);
    }

    public void updateCate(Category category) {
        this.updateById(category);
    }


    public void remove(Long id) {
        //查询当前分类是否有关联的菜品，如果已关联，抛出业务异常
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper();
        queryWrapper.eq(Dish::getCategoryId, id);
        int dishCount = dishService.count(queryWrapper);
        if (dishCount > 0) {
            //已经关联菜品，抛出异常
            throw new MyCustomException("当前分类下关联了菜品，不能删除");
        }
        //查询当前分类是否有关联的套餐，如果已关联，抛出业务异常
        LambdaQueryWrapper<Setmeal> queryWrapper1 = new LambdaQueryWrapper<>();
        queryWrapper1.eq(Setmeal::getCategoryId, id);
        int setmealCount = setmealService.count(queryWrapper1);
        if (setmealCount > 0) {
            //已经关联套餐，抛出异常
            throw new MyCustomException("当前分类下关联了套餐，不能删除");

        }
        //没有关联的菜品或者套餐，直接删除
        this.removeById(id);
    }

    public List<Category> list(Category category){
        //添加条件构造器
        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();
        //添加条件
        queryWrapper.eq(category.getType() != null,Category::getType,category.getType());
        //添加排序条件
        queryWrapper.orderByAsc(Category::getSort).orderByDesc(Category::getUpdateTime);
        List<Category> list = this.list(queryWrapper);
        return list;
    }
}
