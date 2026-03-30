package com.sky.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.sky.entity.AddressBook;

import java.util.List;

public interface AddressBookService extends IService<AddressBook> {
    List<AddressBook> list(AddressBook addressBook);
    /**
     * 作用: 执行save相关逻辑。
     * 输入: AddressBook addressBook。
     * 输出: boolean。
     */
    boolean save(AddressBook addressBook);
    /**
     * 作用: 执行getById相关逻辑。
     * 输入: Long id。
     * 输出: AddressBook。
     */
    AddressBook getById(Long id);
    /**
     * 作用: 执行update相关逻辑。
     * 输入: AddressBook addressBook。
     * 输出: 无。
     */
    void update(AddressBook addressBook);
    /**
     * 作用: 执行setDefault相关逻辑。
     * 输入: AddressBook addressBook。
     * 输出: 无。
     */
    void setDefault(AddressBook addressBook);
    /**
     * 作用: 执行deleteById相关逻辑。
     * 输入: Long id。
     * 输出: 无。
     */
    void deleteById(Long id);
}
