package com.jigong.reggie.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jigong.reggie.commom.Result;
import com.jigong.reggie.dto.OrdersDto;
import com.jigong.reggie.dto.QueryDto;
import com.jigong.reggie.entity.Orders;
import com.jigong.reggie.service.impl.OrderServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


@RestController
@Slf4j
@RequestMapping("/order")
public class OrderController {
    @Autowired
    public OrderServiceImpl orderServiceImpl;

    @PostMapping("/submit")
    public Result<String> submit(@RequestBody Orders orders) {
        orderServiceImpl.submit(orders);
        return Result.success("下单成功");
    }

    @GetMapping("/userPage")
    public Result<Page> page(int page, int pageSize) {
        Page<OrdersDto> detailPage = orderServiceImpl.page(page, pageSize);
        return Result.success(detailPage);
    }

    @GetMapping("/page")
    public Result<Page> list(QueryDto queryDto) {
        Page<Orders> orderPage = orderServiceImpl.list(queryDto);
        return Result.success(orderPage);
    }
}
