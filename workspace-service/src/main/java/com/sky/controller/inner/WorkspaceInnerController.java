package com.sky.controller.inner;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sky.result.Result;
import com.sky.service.WorkspaceService;
import com.sky.vo.BusinessDataVO;

@RestController
@RequestMapping("/inner/workspace")
public class WorkspaceInnerController {

  @Autowired
  private WorkspaceService workspaceService;

  @GetMapping("/businessData")
  public Result<BusinessDataVO> getBusinessData(
      @RequestParam("begin") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime begin,
      @RequestParam("end") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {
    return Result.success(workspaceService.getBusinessData(begin, end));
  }
}
