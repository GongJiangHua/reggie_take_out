package com.jigong.reggie.filter;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.extension.api.R;
import com.jigong.reggie.commom.BaseContext;
import com.jigong.reggie.commom.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.AntPathMatcher;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 检查用户是否已经完成登录
 */
@WebFilter(filterName = "loginCheckFilter",urlPatterns = "/*")
@Slf4j
public class LoginCheckFilter implements Filter {
    //路径匹配器，支持通配符
    public static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        //1、获取本次请求的URLI
        String requestURI = request.getRequestURI();
        log.info("拦截到请求{}",requestURI);
        //2、定义不需要处理的请求路径
        String[] urls = new String[]{
                "/employee/login",
                "/employee/logout",
                "/backend/**",
                "/front/**",
                "/user/sendMsg",
                "/user/login",
                "/doc.html",
                "/webjars/**",
                "/swagger-resources",
                "/v2/api-docs"
        };
        //3、判断本次请求是否需要处理
        boolean check = check(urls, requestURI);
        //4、如果不需要处理，则直接放行
        if (check){
            log.info("本次请求{}不需要处理",requestURI);
            filterChain.doFilter(request,response);
            return;
        }
        //5-1、判断后台管理端登录状态，如果已登录，则直接放行
        Long empId = (Long) request.getSession().getAttribute("employee");
        if (empId != null){
            log.info("用户已登录，用户id为{}",request.getSession().getAttribute("employee"));
            BaseContext.setCurrentId(empId);
            filterChain.doFilter(request,response);
            return;
        }
        //5-2、判断用户移动端登录状态，如果已登录，则直接放行
        Long userId = (Long) request.getSession().getAttribute("user");
        if (userId != null){
            log.info("用户已登录，用户id为{}",request.getSession().getAttribute("user"));
            BaseContext.setCurrentId(userId);
            filterChain.doFilter(request,response);
            return;
        }
        //6、如果未登录，则返回未登录结果，通过输出流方式向客户端相应数据
        log.info("用户未登录");
        response.getWriter().write(JSON.toJSONString(Result.error("NOTLOGIN")));
        return;
    }


    public boolean check(String[] urls,String requestURL){
        for (String url : urls){
            boolean match = PATH_MATCHER.match(url, requestURL);
            if (match){
                return true;
            }
        }
        return false;
    }
}
