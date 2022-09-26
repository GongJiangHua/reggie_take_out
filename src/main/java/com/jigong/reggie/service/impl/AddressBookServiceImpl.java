package com.jigong.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jigong.reggie.commom.BaseContext;
import com.jigong.reggie.entity.AddressBook;
import com.jigong.reggie.mapper.AddressBookMapper;
import com.jigong.reggie.service.AddressBookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AddressBookServiceImpl extends ServiceImpl<AddressBookMapper, AddressBook> implements AddressBookService {
    @Autowired
    public AddressBookService addressBookService;

    public void saveAddress(AddressBook addressBook) {
        addressBook.setUserId(BaseContext.getCurrentId());
        addressBookService.save(addressBook);
    }

    public AddressBook get(Long id) {
        AddressBook addressBook = addressBookService.getById(id);
        return addressBook;

    }

    public void setDefault(AddressBook addressBook) {
        LambdaUpdateWrapper<AddressBook> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(AddressBook::getUserId, BaseContext.getCurrentId());
        wrapper.set(AddressBook::getIsDefault, 0);
        //SQL:update address_book set is_default = 0 where user_id = ?
        addressBookService.update(wrapper);

        addressBook.setIsDefault(1);
        //SQL:update address_book set is_default = 1 where id = ?
        addressBookService.updateById(addressBook);
    }

    public AddressBook getDefault() {
        LambdaQueryWrapper<AddressBook> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AddressBook::getUserId, BaseContext.getCurrentId());
        queryWrapper.eq(AddressBook::getIsDefault, 1);

        //SQL:select * from address_book where user_id = ? and is_default = 1
        AddressBook addressBook = addressBookService.getOne(queryWrapper);
        return addressBook;
    }

    public List<AddressBook> list(AddressBook addressBook) {
        addressBook.setUserId(BaseContext.getCurrentId());
        //条件构造器
        LambdaQueryWrapper<AddressBook> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(null != addressBook.getUserId(), AddressBook::getUserId, addressBook.getUserId());
        queryWrapper.orderByDesc(AddressBook::getUpdateTime);

        //SQL:select * from address_book where user_id = ? order by update_time desc
        List<AddressBook> list = addressBookService.list(queryWrapper);
        return list;
    }
}
