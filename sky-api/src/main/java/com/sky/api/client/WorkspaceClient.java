package com.sky.api.client;

import org.springframework.cloud.openfeign.FeignClient;
import com.sky.api.client.fallback.WorkspaceClientFallbackFactory;

@FeignClient(name = "sky-take-out-workspace", fallbackFactory = WorkspaceClientFallbackFactory.class)
public interface WorkspaceClient {
  // add RPC methods as needed
}
