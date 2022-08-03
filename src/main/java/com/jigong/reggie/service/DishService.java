package com.jigong.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jigong.reggie.dto.DishDto;
import com.jigong.reggie.entity.Dish;

public interface DishService extends IService<Dish> {
    //新增菜品，同时插入菜品对应的口味数据，需要操作两张表dish，dish_falvor
    public void saveWithFlavor(DishDto dishDto);
    //根据id查询菜品信息和对应的口味信息
    public DishDto getWithFlavor(Long id);
    //更新菜品信息和口味信息
    public void updateWithFlavor(DishDto dishDto);
}
