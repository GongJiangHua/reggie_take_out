package com.jigong.reggie.controller;

import com.jigong.reggie.commom.Result;
import com.jigong.reggie.entity.User;
import com.jigong.reggie.service.impl.UserServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Map;

@RestController
@Slf4j
@RequestMapping("/user")
public class UserController {
    @Autowired
    public UserServiceImpl userServiceImpl;

    @PostMapping("/sendMsg")
    public Result<String> senMsg(@RequestBody User user, HttpSession session){
        Boolean msg = userServiceImpl.sendMsg(user);
        if (msg){
            return Result.success("验证码发送成功，请及时查看!");
        }
        return Result.error("验证码发送失败，请重新输入!");
    }

    @PostMapping("/login")
    public Result<User> login(@RequestBody Map map, HttpServletRequest request){
        User user = userServiceImpl.login(map);
        if (user == null){
            return Result.error("用户登录失败");
        }
        //登录成功，将员工id存入Session并返回登录成功结果
        request.getSession().setAttribute("user",user.getId());
        return Result.success(user);
    }
}
