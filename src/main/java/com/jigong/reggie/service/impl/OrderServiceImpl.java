package com.jigong.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.interfaces.Func;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jigong.reggie.commom.BaseContext;
import com.jigong.reggie.commom.MyCustomException;
import com.jigong.reggie.dto.OrdersDto;
import com.jigong.reggie.dto.QueryDto;
import com.jigong.reggie.entity.*;
import com.jigong.reggie.mapper.OrderMapper;
import com.jigong.reggie.service.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
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
     *
     * @param orders
     */
    @Transactional
    public void submit(Orders orders) {
        //获取当前用户的id
        Long userId = BaseContext.getCurrentId();
        //查询当前用户的购物车数据
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId, userId);
        List<ShoppingCart> cartList = shoppingCartService.list(queryWrapper);
        if (cartList == null || cartList.size() == 0) {
            throw new MyCustomException("购物车为空，不能下单");
        }

        User user = userService.getById(userId);
        AddressBook addressBook = addressBookService.getById(orders.getAddressBookId());
        if (addressBook == null) {
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

    public Page<OrdersDto> page(int page, int pageSize) {
        //添加分页构造器
        Page<Orders> pageInfo = new Page<>(page, pageSize);
        Page<OrdersDto> ordersDtoPage = new Page<>();
        //添加查询构造器
        LambdaQueryWrapper<Orders> queryWrapper = new LambdaQueryWrapper<>();
        //添加过滤条件
        queryWrapper.eq(Orders::getUserId, BaseContext.getCurrentId());
        //添加排序条件
        queryWrapper.orderByDesc(Orders::getCheckoutTime);
        //执行查询
        this.page(pageInfo, queryWrapper);

        BeanUtils.copyProperties(pageInfo, ordersDtoPage, "records");

        List<Orders> records = pageInfo.getRecords();
        List<OrdersDto> list = records.stream().map(item -> {
            OrdersDto ordersDto = new OrdersDto();
            BeanUtils.copyProperties(item, ordersDto);
            Long orderId = item.getId();

            //添加查询构造器
            LambdaQueryWrapper<OrderDetail> wrapper = new LambdaQueryWrapper<>();
            //添加查询条件
            wrapper.eq(OrderDetail::getOrderId, orderId);
            //执行查询
            List<OrderDetail> orderDetailList = orderDetailService.list(wrapper);
            //对ordersDto进行赋值
            ordersDto.setOrderDetails(orderDetailList);
            ordersDto.setSumNum(orderDetailList.size());

            return ordersDto;
        }).collect(Collectors.toList());
        ordersDtoPage.setRecords(list);

        return ordersDtoPage;
    }

    public Page<Orders> list(QueryDto queryDto) {
        //添加分页构造器
        Page<Orders> ordersPage = new Page<>(queryDto.getPage(), queryDto.getPageSize());
        //添加查询构造器
        QueryWrapper<Orders> wrapper = new QueryWrapper<>();

        String num = queryDto.getNumber();
        String begin = queryDto.getBeginTime();
        String end = queryDto.getEndTime();
        //添加查询条件
        wrapper
        .like(StringUtils.isNotBlank(num), "number", num)
        //添加构造条件
        .ge(null != begin,"order_time",begin)
        .le(null != end,"order_time",end)
        //添加排序条件
        .orderByDesc("order_time");
        Page<Orders> page = this.page(ordersPage, wrapper);
        return page;
    }
}
