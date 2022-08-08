package com.jigong.reggie.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jigong.reggie.commom.Result;
import com.jigong.reggie.dto.SetmealDto;
import com.jigong.reggie.service.impl.SetmealServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@RequestMapping("/setmeal")
public class SetmealController {
    @Autowired
    public SetmealServiceImpl setmealServiceImpl;

    /**
     * 新增套餐
     * @param setmealDto
     * @return
     */
    @PostMapping
    public Result<String> save(@RequestBody SetmealDto setmealDto){
        setmealServiceImpl.saveWithDish(setmealDto);
        return Result.success("套餐新增成功");
    }

    /**
     * 分页显示
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    public Result<Page> page(int page, int pageSize, String name){
        Page<SetmealDto> setmealDtoPage = setmealServiceImpl.page(page, pageSize, name);
        return Result.success(setmealDtoPage);
    }

    /**
     * 回显套餐信息
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public Result<SetmealDto> get(@PathVariable Long id){
        SetmealDto setmealDto = setmealServiceImpl.getWithDish(id);
        return Result.success(setmealDto);
    }

    /**
     * 更新套餐信息
     * @param setmealDto
     * @return
     */
    @PutMapping
    public Result<String> update(@RequestBody SetmealDto setmealDto){
        setmealServiceImpl.updateWithDish(setmealDto);
        return Result.success("套餐更新成功");
    }
    /**
     * 删除套餐
     * @param ids
     * @return
     */
    @DeleteMapping
    public Result<String> remove(@RequestParam List<Long> ids){
        log.info("ids:",ids);
        setmealServiceImpl.removeWithDish(ids);
        return Result.success("删除套餐成功");
    }

    @PostMapping("/status/{status}")
    public Result<String> updateStatus(@RequestParam List<Long> ids,@PathVariable int status){
        setmealServiceImpl.updateStatus(status,ids);
        return Result.success("状态修改成功");
    }
}
