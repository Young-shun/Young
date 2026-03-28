package com.sky.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sky.context.BaseContext;
import com.sky.entity.AddressBook;
import com.sky.mapper.AddressBookMapper;
import com.sky.service.AddressBookService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@Slf4j
public class AddressBookServiceImpl extends ServiceImpl<AddressBookMapper, AddressBook> implements AddressBookService {

  /**
   * 条件查询
   *
   * @param addressBook
   * @return
   */
  @Override
  public List<AddressBook> list(AddressBook addressBook) {
    LambdaQueryWrapper<AddressBook> wrapper = new QueryWrapper<AddressBook>().lambda()
        .like(addressBook.getUserId() != null, AddressBook::getUserId, addressBook.getUserId())
        .like(addressBook.getPhone() != null, AddressBook::getPhone, addressBook.getPhone())
        .eq(addressBook.getIsDefault() != null, AddressBook::getIsDefault, addressBook.getIsDefault());
    return this.list(wrapper);
  }

  /**
   * 新增地址
   *
   * @param addressBook
   */
  @Override
  public boolean save(AddressBook addressBook) {
    addressBook.setUserId(BaseContext.getCurrentId());
    addressBook.setIsDefault(0);
    return super.save(addressBook);
  }

  /**
   * 根据id查询
   *
   * @param id
   * @return
   */
  @Override
  public AddressBook getById(Long id) {
    return super.getById(id);
  }

  /**
   * 根据id修改地址
   *
   * @param addressBook
   */
  @Override
  public void update(AddressBook addressBook) {
    super.updateById(addressBook);
  }

  /**
   * 设置默认地址
   *
   * @param addressBook
   */
  @Transactional
  @Override
  public void setDefault(AddressBook addressBook) {
    // 1、将当前用户的所有地址修改为非默认地址 update address_book set is_default = ? where user_id = ?
    addressBook.setUserId(BaseContext.getCurrentId());
    LambdaUpdateWrapper<AddressBook> wrapper = new LambdaUpdateWrapper<AddressBook>()
        .eq(AddressBook::getUserId, addressBook.getUserId())
        .set(AddressBook::getIsDefault, 0);
    this.update(null, wrapper);

    // 2、将当前地址改为默认地址 update address_book set is_default = ? where id = ?
    addressBook.setIsDefault(1);
    super.updateById(addressBook);
  }

  /**
   * 根据id删除地址
   *
   * @param id
   */
  @Override
  public void deleteById(Long id) {
    super.removeById(id);
  }

}
