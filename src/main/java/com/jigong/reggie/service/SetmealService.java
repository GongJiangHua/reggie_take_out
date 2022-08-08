package com.jigong.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jigong.reggie.dto.SetmealDto;
import com.jigong.reggie.entity.Setmeal;

import java.util.List;

public interface SetmealService extends IService<Setmeal> {
    /**
     * 新增套餐，同时保存套餐和菜品的关联关系
     * @param setmealDto
     */
    public void saveWithDish(SetmealDto setmealDto);

    /**
     * 删除套餐，同时需要删除套餐和菜品的关联关系
     * @param ids
     */
    public void removeWithDish(List<Long> ids);
    /**
     * 回显套餐信息,根据id查询套餐信息、套餐关联的菜品及套餐分类信息
     * @param id
     */
    public SetmealDto getWithDish(Long id);

    /**
     * 更新套餐信息及与其相关的关联菜品表信息
     * @param setmealDto
     */
    public void updateWithDish(SetmealDto setmealDto);
}
