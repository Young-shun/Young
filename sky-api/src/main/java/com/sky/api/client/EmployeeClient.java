package com.sky.api.client;

import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(name = "sky-take-out-employee")
public interface EmployeeClient {
  // add RPC methods as needed
}
