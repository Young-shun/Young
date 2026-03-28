package com.sky.api.client;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.cloud.openfeign.FeignClient;

import com.sky.entity.AddressBook;
import com.sky.result.Result;

@FeignClient(name = "sky-take-out-address")
public interface AddressClient {
  @GetMapping("/user/addressBook/{id}")
  Result<AddressBook> getById(@PathVariable("id") Long id);
}
