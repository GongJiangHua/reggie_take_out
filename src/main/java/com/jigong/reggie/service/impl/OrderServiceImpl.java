package com.jigong.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jigong.reggie.commom.BaseContext;
import com.jigong.reggie.commom.MyCustomException;
import com.jigong.reggie.entity.*;
import com.jigong.reggie.mapper.OrderMapper;
import com.jigong.reggie.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Orders> implements OrderService {
    @Autowired
    public ShoppingCartService shoppingCartService;
    @Autowired
    public UserService userService;
    @Autowired
    public AddressBookService addressBookService;
    @Autowired
    public OrderDetailService orderDetailService;
    @Autowired
    public OrderService orderService;
    /**
     * 用户下单
     * @param orders
     */
    @Transactional
    public void submit(Orders orders){
        //获取当前用户的id
        Long userId = BaseContext.getCurrentId();
        //查询当前用户的购物车数据
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId,userId);
        List<ShoppingCart> cartList = shoppingCartService.list(queryWrapper);
        if (cartList == null || cartList.size() == 0){
            throw new MyCustomException("购物车为空，不能下单");
        }

        User user = userService.getById(userId);
        AddressBook addressBook = addressBookService.getById(orders.getAddressBookId());
        if (addressBook == null){
            throw new MyCustomException("用户地址信息有误，不能下单");
        }
        long orderId = IdWorker.getId();  // 订单号

        AtomicInteger amount = new AtomicInteger(0);

        List<OrderDetail> orderDetails = cartList.stream().map(item -> {
            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setOrderId(orderId);
            orderDetail.setName(item.getName());
            orderDetail.setImage(item.getImage());

            orderDetail.setDishId(item.getDishId());
            orderDetail.setSetmealId(item.getSetmealId());
            orderDetail.setDishFlavor(item.getDishFlavor());
            orderDetail.setNumber(item.getNumber());
            orderDetail.setAmount(item.getAmount());

            amount.addAndGet(item.getAmount().multiply(new BigDecimal(item.getNumber())).intValue());

            return orderDetail;
        }).collect(Collectors.toList());


        orders.setId(orderId);
        orders.setOrderTime(LocalDateTime.now());
        orders.setCheckoutTime(LocalDateTime.now());
        orders.setStatus(2);
        orders.setAmount(new BigDecimal(amount.get()));//总金额，需要 遍历购物车，计算相关金额来得到
        orders.setUserId(userId);
        orders.setNumber(String.valueOf(orderId));
        orders.setUserName(user.getName());
        orders.setConsignee(addressBook.getConsignee());
        orders.setPhone(addressBook.getPhone());
        orders.setAddress((addressBook.getProvinceName() == null ? "" : addressBook.getProvinceName())
                + (addressBook.getCityName() == null ? "" : addressBook.getCityName())
                + (addressBook.getDistrictName() == null ? "" : addressBook.getDistrictName())
                + (addressBook.getDetail() == null ? "" : addressBook.getDetail()));
        //向订单表插入数据，一条数据
        this.save(orders);
        //向订单详细表插入数据，多条数据
        orderDetailService.saveBatch(orderDetails);
        //清空购物车数据
        shoppingCartService.remove(queryWrapper);
    }

    public Page<OrderDetail> page(int page,int pageSize){
        //查询当前用户的id
        Long currentId = BaseContext.getCurrentId();
        LambdaQueryWrapper<Orders> query = new LambdaQueryWrapper();
        query.eq(Orders::getUserId,currentId);
        Orders one = orderService.getOne(query);
        //添加分页构造器
        Page<OrderDetail> pageInfo = new Page<>(page,pageSize);
        //添加一个查询构造器
        LambdaQueryWrapper<OrderDetail> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(OrderDetail::getOrderId,one.getNumber());
        //添加分页条件
        Page<OrderDetail> detailPage = orderDetailService.page(pageInfo, queryWrapper);
        return detailPage;
    }
}
