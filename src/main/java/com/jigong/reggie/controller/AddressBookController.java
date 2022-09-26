package com.jigong.reggie.controller;

import com.jigong.reggie.commom.Result;
import com.jigong.reggie.entity.AddressBook;
import com.jigong.reggie.service.impl.AddressBookServiceImpl;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 地址簿管理
 */
@Slf4j
@RestController
@RequestMapping("/addressBook")
@Api("地址")
public class AddressBookController {
    @Autowired
    private AddressBookServiceImpl addressBookServiceImpl;

    /**
     * 新增地址
     * @param addressBook
     * @return
     */
    @PostMapping
    public Result<AddressBook> save(@RequestBody AddressBook addressBook) {
        addressBookServiceImpl.saveAddress(addressBook);
        log.info("addressBook:{}", addressBook);
        return Result.success(addressBook);
    }

    /**
     * 设置默认地址
     * @param addressBook
     * @return
     */
    @PutMapping("default")
    public Result<AddressBook> setDefault(@RequestBody AddressBook addressBook) {
        log.info("addressBook:{}", addressBook);
        addressBookServiceImpl.setDefault(addressBook);
        return Result.success(addressBook);
    }

    /**
     * 根据id查询地址
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public Result get(@PathVariable Long id) {
        AddressBook addressBook = addressBookServiceImpl.get(id);
        if (addressBook != null) {
            return Result.success(addressBook);
        } else {
            return Result.error("没有找到该对象");
        }
    }

    /**
     * 查询默认地址
     * @return
     */
    @GetMapping("default")
    public Result<AddressBook> getDefault() {
        AddressBook addressBook = addressBookServiceImpl.getDefault();

        if (null == addressBook) {
            return Result.error("没有找到该对象");
        } else {
            return Result.success(addressBook);
        }
    }

    /**
     * 查询指定用户的全部地址
     * @param addressBook
     * @return
     */
    @GetMapping("/list")
    public Result<List<AddressBook>> list(AddressBook addressBook) {
        log.info("addressBook:{}", addressBook);
        List<AddressBook> addressBooks = addressBookServiceImpl.list(addressBook);

        return Result.success(addressBooks);
    }
}
