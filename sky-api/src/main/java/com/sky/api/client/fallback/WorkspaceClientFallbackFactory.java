package com.sky.api.client.fallback;

import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

import com.sky.api.client.WorkspaceClient;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class WorkspaceClientFallbackFactory implements FallbackFactory<WorkspaceClient> {

  @Override
  public WorkspaceClient create(Throwable cause) {
    log.error("Feign调用WorkspaceClient失败", cause);
    return new WorkspaceClient() {
    };
  }
}