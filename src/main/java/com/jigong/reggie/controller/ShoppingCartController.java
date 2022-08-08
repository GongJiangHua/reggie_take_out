package com.jigong.reggie.controller;

import com.jigong.reggie.commom.Result;
import com.jigong.reggie.entity.ShoppingCart;
import com.jigong.reggie.service.impl.ShoppingCartServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequestMapping("/shoppingCart")
public class ShoppingCartController {
    @Autowired
    public ShoppingCartServiceImpl shoppingCartServiceImpl;

//    @GetMapping("/list")
//    public Result<String> list(@RequestBody ShoppingCart shoppingCart){
//        return Result.success("success");
//    }
}
