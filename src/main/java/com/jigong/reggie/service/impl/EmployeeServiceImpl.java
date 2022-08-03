package com.jigong.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jigong.reggie.commom.Result;
import com.jigong.reggie.entity.Employee;
import com.jigong.reggie.mapper.EmployeeMapper;
import com.jigong.reggie.service.EmployeeService;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;


@Service
public class EmployeeServiceImpl extends ServiceImpl<EmployeeMapper, Employee> implements EmployeeService{

    /**
     * 员工登录
     * @param employee
     * @return
     */
    public Result<Employee> login(Employee employee){
        //1、将页面提交的密码进行md5加密
        String password = employee.getPassword();
        password = DigestUtils.md5DigestAsHex(password.getBytes());
        //2、根据前端提交的username查询数据库
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Employee::getUsername,employee.getUsername());
        Employee emp = this.getOne(queryWrapper);
        //3、如果没有查询到则返回登录失败的结果
        if (emp == null){
            return Result.error("用户名未找到，登陆失败");
        }
        //4、若能查询到该用户，则判断password是否正确,密码不一致则返回登录失败
        if (!emp.getPassword().equals(password)){
            return Result.error("密码错误，登陆失败");
        }
        //5、查看员工状态，若为已禁用，则返回已禁用状态
        if (emp.getStatus() == 0){
            return Result.error("该账户已禁用");
        }
        return Result.success(emp);
    }

    /**
     * 新增员工
     * @param employee
     * @return
     */
    public void saveEmp(Employee employee){
        //对员工密码进行赋值，默认为123456
        employee.setPassword(DigestUtils.md5DigestAsHex("123456".getBytes()));
        this.save(employee);
    }

    /**
     * 员工信息的分页查询
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    public Page pageResult(Integer page,Integer pageSize,String name){
        //构造分页构造器
        Page pageInfo = new Page(page,pageSize);

        //构造条件构造器
        LambdaQueryWrapper<Employee> wrapper = new LambdaQueryWrapper<>();
        //添加过滤条件
        wrapper.like(StringUtils.isNotBlank(name),Employee::getName,name);
        //添加排序条件
        wrapper.orderByDesc(Employee::getUpdateTime);

        this.page(pageInfo,wrapper);

        return pageInfo;
    }

    /**
     * 修改员工状态 以及 员工信息更新保存
     * @param employee
     */
    public void updateEmployee(Employee employee) {
        this.updateById(employee);
    }


    /**
     * 根据id查询员工信息,编辑员工信息回显
     * @param id
     * @return
     */
    public Employee getEmpById(Long id){
        Employee emp = this.getById(id);
        if (emp != null){
            return emp;
        }
        return null;
    }
}
