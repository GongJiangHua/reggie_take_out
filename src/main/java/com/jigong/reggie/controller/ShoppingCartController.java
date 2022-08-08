package com.jigong.reggie.controller;

import com.jigong.reggie.commom.Result;
import com.jigong.reggie.entity.ShoppingCart;
import com.jigong.reggie.service.impl.ShoppingCartServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@RequestMapping("/shoppingCart")
public class ShoppingCartController {
    @Autowired
    public ShoppingCartServiceImpl shoppingCartServiceImpl;

    /**
     * 查看购物车
     * @return
     */
    @GetMapping("/list")
    public Result<List<ShoppingCart>> list(){
        List<ShoppingCart> list = shoppingCartServiceImpl.list();
        return Result.success(list);
    }

    /**
     * 加购菜品或者套餐数量+1
     * @param shoppingCart
     * @return
     */
    @PostMapping("/add")
    public Result<ShoppingCart> add(@RequestBody ShoppingCart shoppingCart){
        ShoppingCart cart = shoppingCartServiceImpl.addShoppingCart(shoppingCart);
        return Result.success(cart);
    }

    /**
     * 加购菜品或套餐数量-1
     * @param shoppingCart
     * @return
     */
    @PostMapping("/sub")
    public Result<String> sub(@RequestBody ShoppingCart shoppingCart){
        shoppingCartServiceImpl.subShoppingCart(shoppingCart);
        return Result.success("成功删减订单");
    }

    /**
     * 清空购物车
     * @return
     */
    @DeleteMapping("/clean")
    public Result<String> clean(){
        shoppingCartServiceImpl.clean();
        return Result.success("购物车清空成功");
    }
}
