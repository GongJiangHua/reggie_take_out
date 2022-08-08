package com.jigong.reggie.controller;

import com.jigong.reggie.commom.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequestMapping("/order")
public class OrderController {
@PostMapping("/submit")
    public Result<String> submit(){
    return Result.success("下单成功");
}

}
