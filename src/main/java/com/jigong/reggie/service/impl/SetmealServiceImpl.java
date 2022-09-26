package com.jigong.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jigong.reggie.commom.MyCustomException;
import com.jigong.reggie.dto.SetmealDto;
import com.jigong.reggie.entity.Category;
import com.jigong.reggie.entity.Setmeal;
import com.jigong.reggie.entity.SetmealDish;
import com.jigong.reggie.mapper.SetmealMapper;
import com.jigong.reggie.service.CategoryService;
import com.jigong.reggie.service.SetmealDishService;
import com.jigong.reggie.service.SetmealService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SetmealServiceImpl extends ServiceImpl<SetmealMapper, Setmeal> implements SetmealService {
    @Autowired
    public SetmealDishService setmealDishService;
    @Autowired
    public CategoryService categoryService;

    /**
     * 新增套餐，同时保存套餐和菜品的关联关系
     * @param setmealDto
     */
    @Override
    @Transactional
    public void saveWithDish(SetmealDto setmealDto) {
        //保存套餐的基本信息，操作setmeal，执行insert操作
        this.save(setmealDto);
        //获取套餐内的菜品信息
        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();
        //给setmealDish设置SetmealId
        setmealDishes.stream().map(item -> {
            item.setSetmealId(setmealDto.getId());
            return item;
        }).collect(Collectors.toList());
        setmealDishService.saveBatch(setmealDishes);
    }
    /**
     * 删除套餐，同时需要删除套餐和菜品的关联关系
     * @param ids
     */
    @Override
    public void removeWithDish(List<Long> ids) {
        //SELECT COUNT(*) FROM setmeal s2 where id =  '1415580119015145474' AND status = '1';
        //查询套餐状态，确定是否可以删除
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(Setmeal::getId,ids);
        queryWrapper.eq(Setmeal::getStatus,1);
        int count = this.count(queryWrapper);
        //如果不能删除，则抛出一个业务异常
        if (count > 0){
            throw new MyCustomException("套餐正在售卖中，不能删除");
        }
        //如果可以删除，则先删除套餐表中的数据
        this.removeByIds(ids);
        //再删除关系表setmeal_dish中的数据
        LambdaQueryWrapper<SetmealDish> queryWrapper1 = new LambdaQueryWrapper<>();
        queryWrapper1.in(SetmealDish::getSetmealId,ids);
        setmealDishService.remove(queryWrapper1);
    }

    /**
     * 回显套餐信息
     * @param id
     * @return
     */
    @Override
    public SetmealDto getWithDish(Long id) {
        //new一个SetmealDto，用于存储返回的信息
        SetmealDto setmealDto = new SetmealDto();
        //根据套餐id 查询套餐
        Setmeal setmeal = this.getById(id);
        //将套餐的基本信息copy到SetmealDto
        BeanUtils.copyProperties(setmeal,setmealDto);
        //根据分类id在分类表中查出分类名称
        Category category = categoryService.getById(setmeal.getCategoryId());
        String categoryName = category.getName();
        setmealDto.setCategoryName(categoryName);
        //根据id获取与套餐关联的菜品
        LambdaQueryWrapper<SetmealDish> queryWrapper = new LambdaQueryWrapper();
        queryWrapper.eq(SetmealDish::getSetmealId,id);
        List<SetmealDish> dishes = setmealDishService.list(queryWrapper);
        setmealDto.setSetmealDishes(dishes);

        return setmealDto;
    }

    /**
     * 更新套餐信息
         * @param setmealDto
     * @return
     */
    @Override
    public void updateWithDish(SetmealDto setmealDto) {
        this.updateById(setmealDto);
        //先删除setmeal_dish中与之相关的菜品信息
        LambdaQueryWrapper<SetmealDish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SetmealDish::getSetmealId,setmealDto.getId());
        setmealDishService.remove(queryWrapper);
        //然后对setmeal_dish表与之相关的菜品进行重新添加
        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();
        //给setmealDishes添加setmealid
        setmealDishes.stream().map(item -> {
            item.setSetmealId(setmealDto.getId());
            return item;
        }).collect(Collectors.toList());

        setmealDishService.saveBatch(setmealDishes);
    }

    /**
     * 套餐信息的分页查询
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    public Page<SetmealDto> page(int page, int pageSize, String name) {
        Page<Setmeal> setmealPage = new Page<>(page, pageSize);
        Page<SetmealDto> dtoPage = new Page<>();

        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        //添加查询条件
        queryWrapper.eq(name != null, Setmeal::getName, name);
        //添加排序条件,根据更新时间降序排序
        queryWrapper.orderByDesc(Setmeal::getUpdateTime);
        this.page(setmealPage, queryWrapper);

        BeanUtils.copyProperties(setmealPage, dtoPage, "records");
        List<Setmeal> records = setmealPage.getRecords();
        List<SetmealDto> list = records.stream().map(item -> {
            SetmealDto setmealDto = new SetmealDto();
            //属性赋值
            BeanUtils.copyProperties(item, setmealDto);
            //分类id
            Long categoryId = item.getCategoryId();
            //根据分类id查询分类对象
            Category category = categoryService.getById(categoryId);
            if (category != null) {
                String categoryName = category.getName();
                setmealDto.setCategoryName(categoryName);
            }
            return setmealDto;
        }).collect(Collectors.toList());
        return dtoPage.setRecords(list);
    }

    /**
     * 修改套餐状态，停售或者起售
     * @param ids
     * @param status
     * @return
     */
    public void updateStatus(int status,List<Long> ids){
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(ids != null,Setmeal::getId,ids);
        List<Setmeal> list = this.list(queryWrapper);
        list.forEach(setmeal -> {
            setmeal.setStatus(status);
            this.updateById(setmeal);
        });
    }

    /**
     * 起售状态的套餐列表，用于C端显示给客户
     * @param setmeal
     * @return
     */
    public List<Setmeal> list(Setmeal setmeal){
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(setmeal.getCategoryId() != null,Setmeal::getCategoryId,setmeal.getCategoryId());
        queryWrapper.eq(setmeal.getStatus() != null,Setmeal::getStatus,setmeal.getStatus());

        List<Setmeal> setmealList = this.list(queryWrapper);
        return setmealList;
    }

}
