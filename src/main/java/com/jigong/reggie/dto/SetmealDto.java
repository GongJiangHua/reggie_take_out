package com.jigong.reggie.dto;

import com.jigong.reggie.entity.Setmeal;
import com.jigong.reggie.entity.SetmealDish;
import lombok.Data;

import java.util.List;

@Data
public class SetmealDto extends Setmeal {
    private List<SetmealDish> setmealDishes;

    private String categoryName;
}
