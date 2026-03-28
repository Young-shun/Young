package com.sky.api.client;

import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(name = "sky-take-out-workspace")
public interface WorkspaceClient {
  // add RPC methods as needed
}
