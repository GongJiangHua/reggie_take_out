package com.jigong.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jigong.reggie.entity.User;
import com.jigong.reggie.mapper.UserMapper;
import com.jigong.reggie.service.UserService;
import com.jigong.reggie.utils.ValidateCodeUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
    @Value("${spring.mail.username}")
    private String from;   // 邮件发送人

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Override
    /**
     * 发送邮件验证码
     * @param user
     * @param session
     * @return
     */
    public Boolean sendMsg(User user) {
        //  获取邮箱账号
        String phone = user.getPhone();

        String subject = "瑞吉外卖登录验证码";
        if (StringUtils.isNotEmpty(phone)) {
            String code = ValidateCodeUtils.generateValidateCode(4).toString();
            String context = "欢迎使用瑞吉外卖，登录验证码为: " + code + ",五分钟内有效，请妥善保管!";

            log.info("code={}", code);

            // 真正地发送邮箱验证码
            SimpleMailMessage mailMessage = new SimpleMailMessage();

            mailMessage.setFrom(from);
            mailMessage.setTo(phone);
            mailMessage.setSubject(subject);
            mailMessage.setText(context);

            // 真正的发送邮件操作，从 from到 to
            mailSender.send(mailMessage);

            //  将随机生成的验证码保存到session中
            //session.setAttribute(phone,code);
            redisTemplate.opsForValue().set("gjh", "11111");
            // 验证码由保存到session 优化为 缓存到Redis中，并且设置验证码的有效时间为 5分钟
            redisTemplate.opsForValue().set(phone, code, 5, TimeUnit.MINUTES);
            return true;
        }
        return false;
    }

    /**
     * 用户登录
     * @param map
     * @return
     */
    public User login(Map map) {
        //获取phone
        String phone = map.get("phone").toString();
        //获取code
        String code = map.get("code").toString();
        //从redis中获取验证码
        String redisCode = redisTemplate.opsForValue().get(phone);
        //进行验证码对比，页面中提交的验证码和redis中的验证码进行对比
        //验证码不一致，则登录失败
        if (!redisCode.equals(code)) {
            return null;
        }
        //验证码一致
        //则判断该邮箱是否是新用户，如果是新用户，则自动完成注册
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getPhone, phone);
        User user = this.getOne(queryWrapper);
        if (user == null) {
            user = new User();
            user.setPhone(phone);
            this.save(user);
        }
        redisTemplate.delete(phone);
        return user;
    }

}