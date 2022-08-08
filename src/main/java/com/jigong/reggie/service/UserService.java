package com.jigong.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jigong.reggie.entity.User;

public interface UserService extends IService<User> {
    //发送验证码给电子邮箱
    public Boolean sendMsg(User user);

}
