package com.sky.api.client;

import org.springframework.cloud.openfeign.FeignClient;
import com.sky.api.client.fallback.EmployeeClientFallbackFactory;

@FeignClient(name = "sky-take-out-employee", fallbackFactory = EmployeeClientFallbackFactory.class)
public interface EmployeeClient {
  // add RPC methods as needed
}
