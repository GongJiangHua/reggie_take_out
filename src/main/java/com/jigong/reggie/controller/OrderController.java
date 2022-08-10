package com.jigong.reggie.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jigong.reggie.commom.Result;
import com.jigong.reggie.entity.OrderDetail;
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
    public Result<String> submit(@RequestBody Orders orders){
    orderServiceImpl.submit(orders);
    return Result.success("下单成功");
}

@GetMapping("/userPage")
    public Result<Page<OrderDetail>> page(int page, int pageSize){
    Page<OrderDetail> detailPage = orderServiceImpl.page(page, pageSize);
    return Result.success(detailPage);
    }
}
