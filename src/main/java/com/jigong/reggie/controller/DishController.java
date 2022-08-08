package com.jigong.reggie.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jigong.reggie.commom.Result;
import com.jigong.reggie.dto.DishDto;
import com.jigong.reggie.entity.Dish;
import com.jigong.reggie.service.impl.DishServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 菜品管理
 */
@RestController
@Slf4j
@RequestMapping("/dish")
public class DishController {
    @Autowired
    public DishServiceImpl dishServiceImpl;

    @PostMapping
    public Result<String> save(@RequestBody DishDto dishDto) {
        log.info("当前的dishDto:", dishDto.toString());
        dishServiceImpl.saveWithFlavor(dishDto);
        return Result.success("新增菜品成功");
    }

    /**
     * 菜品信息的分页查询
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    public Result<Page> page(int page, int pageSize, String name){
        Page<DishDto> dtoPage = dishServiceImpl.page(page, pageSize, name);
        return Result.success(dtoPage);
    }

    /**
     * 根据id查询菜品信息和口味信息,菜品信息回显
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public Result<DishDto> get(@PathVariable Long id){
        DishDto dishDto = dishServiceImpl.getWithFlavor(id);
        return Result.success(dishDto);
    }

    /**
     * 修改菜品
     * @param dishDto
     * @return
     */
    @PutMapping
    public Result<String> update(@RequestBody DishDto dishDto){
        dishServiceImpl.updateWithFlavor(dishDto);
        return Result.success("更新菜品成功");
    }

    @DeleteMapping
    public Result<String> remove(@RequestParam("ids") List<Long> ids){
        dishServiceImpl.remove(ids);
        return Result.success("删除菜品成功");
    }

    @GetMapping("/list")
    public Result<List<DishDto>> list(Dish dish){
        List<DishDto> list = dishServiceImpl.list(dish);
        return Result.success(list);
    }

    @PostMapping("/status/{status}")
    public Result<String> updateStatus(@PathVariable int status,@RequestParam List<Long> ids){
        dishServiceImpl.updateStatus(status,ids);
        return Result.success("菜品状态修改成功");
    }
}
