package com.jigong.reggie.controller;

import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jigong.reggie.commom.Result;
import com.jigong.reggie.entity.Employee;
import com.jigong.reggie.service.impl.EmployeeServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@RestController
@RequestMapping("/employee")
public class EmployeeController {

    @Autowired
    private EmployeeServiceImpl employeeServiceImpl;

    /**
     * 员工登录
     * @param request
     * @param employee
     * @return
     */
    @PostMapping("/login")
    public Result<Employee> login(HttpServletRequest request, @RequestBody Employee employee){
        Result<Employee> result = employeeServiceImpl.login(employee);
        //登录成功，将员工id存入Session并返回登录成功结果
        request.getSession().setAttribute("employee",result.getData().getId());
        return result;
    }

    /**
     * 员工退出
     * @param request
     * @return
     */
    @PostMapping("/logout")
    public Result<String> logout(HttpServletRequest request){
        //清除Session中当前登录的用户id
        request.getSession().removeAttribute("employee");
        return Result.success("退出成功");
    }

    /**
     * 新增员工
     * @param request
     * @param employee
     * @return
     */
    @PostMapping
    public Result<String> save(HttpServletRequest request,@RequestBody Employee employee){
        log.info("新增员工，员工信息：{}",employee.toString());
        //获取当前登录用户的员工Id
        employeeServiceImpl.saveEmp(employee);
        return Result.success("成功新增员工");
    }

    /**
     * 员工信息的分页查询
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    public Result<Page> page(int page, int pageSize,String name){
        log.info("page = {},pagesize = {},name = {}",page,pageSize,name);
        Page pageInfo = employeeServiceImpl.pageResult(page,pageSize,name);

        return Result.success(pageInfo);
    }

    /**
     * 修改员工状态 以及 员工信息更新保存
     * @param employee
     * @return
     */
    @PutMapping
    public Result<String> update(@RequestBody Employee employee){
        employeeServiceImpl.updateEmployee(employee);
        return Result.success("员工信息修改成功");
    }

    /**
     * 根据id查询员工信息
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public Result<Employee> getEmpById(@PathVariable Long id){
        Employee emp = employeeServiceImpl.getEmpById(id);
        if (emp != null){
            return Result.success(emp);
        }
        return Result.error("没有查询到对应员工");
    }
}
