package com.jigong.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jigong.reggie.commom.Result;
import com.jigong.reggie.dto.DishDto;
import com.jigong.reggie.entity.Category;
import com.jigong.reggie.entity.Dish;
import com.jigong.reggie.entity.DishFlavor;
import com.jigong.reggie.mapper.DishMapper;
import com.jigong.reggie.service.CategoryService;
import com.jigong.reggie.service.DishFlavorService;
import com.jigong.reggie.service.DishService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements DishService {
    @Autowired
    public DishFlavorService dishFlavorService;

    @Autowired
    public CategoryService categoryService;

    /**
     * 新增菜品，同时保存对应的口味数据
     *
     * @param dishDto
     */
    @Transactional
    public void saveWithFlavor(DishDto dishDto) {
        //将菜品信息保存到dish表
        this.save(dishDto);
        //口味相关的信息保存到flavor表
        Long dishId = dishDto.getId();//菜品Id
        //菜品口味
        List<DishFlavor> flavors = dishDto.getFlavors();
        flavors = flavors.stream().map((item) -> {
            item.setDishId(dishId);
            return item;
        }).collect(Collectors.toList());
        // 保存菜品口味数据到菜品口味表dish_flavor
        dishFlavorService.saveBatch(flavors);
    }

    /**
     * 根据id查询菜品信息和口味信息,菜品信息回显
     * @param id
     * @return
     */
    @Override
    public DishDto getWithFlavor(Long id) {
        DishDto dishDto = new DishDto();
        //1、查询菜品的基本信息，从dish表查询
        Dish dish = this.getById(id);
        BeanUtils.copyProperties(dish, dishDto);
        //2、查询当前菜品对应的口味信息，从dish_flavor
        //添加查询构造器
        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper();
        //添加查询条件
        queryWrapper.eq(DishFlavor::getDishId, dish.getId());
        List<DishFlavor> dishFlavors = dishFlavorService.list(queryWrapper);
        dishDto.setFlavors(dishFlavors);
        return dishDto;
    }

    /**
     * 修改菜品
     * @param dishDto
     */
    @Override
    @Transactional
    public void updateWithFlavor(DishDto dishDto) {
        //1、更新dish表基本信息
        this.updateById(dishDto);
        //2、删除当前菜品对应的口味信息
        //添加查询构造器
        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
        //添加查询条件
        queryWrapper.eq(DishFlavor::getDishId, dishDto.getId());
        dishFlavorService.remove(queryWrapper);
        //3、保存菜品对应的口味信息
        List<DishFlavor> flavors = dishDto.getFlavors();
        flavors = flavors.stream().map((item -> {
            item.setDishId(dishDto.getId());
            return item;
        })).collect(Collectors.toList());

        dishFlavorService.saveBatch(flavors);
    }

    /**
     * 菜品信息的分页查询
     *
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    public Page<DishDto> page(int page, int pageSize, String name) {
        //新建一个分页构造器
        Page<Dish> pageInfo = new Page<>(page, pageSize);
        Page<DishDto> dishDtoPage = new Page<>();
        //新建一个查询构造器
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        //添加过滤条件
        queryWrapper.like(name != null, Dish::getName, name);
        //添加排序条件
        queryWrapper.orderByDesc(Dish::getUpdateTime);
        //执行分页查询
        this.page(pageInfo, queryWrapper);
        //  将 dish 中的属性值复制到 dtoPage，但是忽略 records
        //  records需要另外去设置
        BeanUtils.copyProperties(pageInfo, dishDtoPage, "records");
        List<Dish> records = pageInfo.getRecords();
        //将每个record单独拿出来copy，并设置categoryName
        List<DishDto> dishDtoList = records.stream().map(dish -> {
            DishDto dishDto = new DishDto();
            Long categoryId = dish.getCategoryId();//菜品的分类id
            Category category = categoryService.getById(categoryId);
            if (category != null) {
                dishDto.setCategoryName(category.getName());
            }
            BeanUtils.copyProperties(dish, dishDto);
            return dishDto;
        }).collect(Collectors.toList());

        dishDtoPage.setRecords(dishDtoList);
        return dishDtoPage;
    }

    public void remove(List<Long> ids){
        //先依据id删除dish_flavor表中的菜品口味信息
            //添加查询构造器
            LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.in(DishFlavor::getDishId,ids);
            //依据dish_id删除dish_flavor中的相关数据
            dishFlavorService.remove(queryWrapper);
        //删除dish表中的菜品信息
        this.removeByIds(ids);
    }

    public List<Dish> list(Dish dish){
        //添加查询构造器
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper();
        //添加查询条件
        queryWrapper.eq(dish.getCategoryId() != null,Dish::getCategoryId,dish.getCategoryId());
        queryWrapper.eq(Dish::getStatus,1);
        //添加排序条件
        queryWrapper.orderByAsc(Dish::getSort).orderByDesc(Dish::getUpdateTime);
        List<Dish> dishList = this.list(queryWrapper);
        return dishList;
    }

    public void updateStatus(int status,List<Long> ids){
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(Dish::getId,ids);
        List<Dish> list = this.list(queryWrapper);
        list.forEach(dish -> {
            dish.setStatus(status);
            this.updateById(dish);
        });
    }
}
