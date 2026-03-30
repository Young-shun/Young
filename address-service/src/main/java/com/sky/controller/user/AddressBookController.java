package com.sky.controller.user;

import com.sky.context.BaseContext;
import com.sky.entity.AddressBook;
import com.sky.result.Result;
import com.sky.service.AddressBookService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/user/addressBook")
@Api(tags = "address api")
public class AddressBookController {

    @Autowired
    private AddressBookService addressBookService;

    @GetMapping("/list")
    @ApiOperation("list addresses for current user")
    /**
     * 作用: 执行list相关逻辑。
     * 输入: 无。
     * 输出: Result<List<AddressBook>>。
     */
    public Result<List<AddressBook>> list() {
        AddressBook addressBook = new AddressBook();
        addressBook.setUserId(BaseContext.getCurrentId());
        List<AddressBook> list = addressBookService.list(addressBook);
        return Result.success(list);
    }

    @PostMapping
    @ApiOperation("create address")
    /**
     * 作用: 执行save相关逻辑。
     * 输入: @RequestBody AddressBook addressBook。
     * 输出: Result。
     */
    public Result save(@RequestBody AddressBook addressBook) {
        addressBookService.save(addressBook);
        return Result.success();
    }

    @GetMapping("/{id}")
    @ApiOperation("get address by id")
    /**
     * 作用: 执行getById相关逻辑。
     * 输入: @PathVariable Long id。
     * 输出: Result<AddressBook>。
     */
    public Result<AddressBook> getById(@PathVariable Long id) {
        AddressBook addressBook = addressBookService.getById(id);
        return Result.success(addressBook);
    }

    @PutMapping
    @ApiOperation("update address by id")
    /**
     * 作用: 执行update相关逻辑。
     * 输入: @RequestBody AddressBook addressBook。
     * 输出: Result。
     */
    public Result update(@RequestBody AddressBook addressBook) {
        addressBookService.update(addressBook);
        return Result.success();
    }

    @PutMapping("/default")
    @ApiOperation("set default address")
    /**
     * 作用: 执行setDefault相关逻辑。
     * 输入: @RequestBody AddressBook addressBook。
     * 输出: Result。
     */
    public Result setDefault(@RequestBody AddressBook addressBook) {
        addressBookService.setDefault(addressBook);
        return Result.success();
    }

    @DeleteMapping
    @ApiOperation("delete by id")
    /**
     * 作用: 执行deleteById相关逻辑。
     * 输入: Long id。
     * 输出: Result。
     */
    public Result deleteById(Long id) {
        addressBookService.deleteById(id);
        return Result.success();
    }

    @GetMapping("default")
    @ApiOperation("get default address")
    /**
     * 作用: 执行getDefault相关逻辑。
     * 输入: 无。
     * 输出: Result<AddressBook>。
     */
    public Result<AddressBook> getDefault() {
        AddressBook addressBook = new AddressBook();
        addressBook.setIsDefault(1);
        addressBook.setUserId(BaseContext.getCurrentId());
        List<AddressBook> list = addressBookService.list(addressBook);

        if (list != null && list.size() == 1) {
            return Result.success(list.get(0));
        }

        return Result.error("no default address");
    }

}
