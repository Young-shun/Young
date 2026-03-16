package com.sky.controller.admin;

import java.io.IOException;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.sky.constant.MessageConstant;
import com.sky.dto.EmployeeLoginDTO;
import com.sky.result.Result;
import com.sky.utils.AliOssUtil;
import com.sky.vo.EmployeeLoginVO;

import lombok.extern.slf4j.Slf4j;

/**
 * 公共接口
 */
@RestController
@RequestMapping("/admin/common")
@Slf4j
public class CommonController {

  @Autowired
  AliOssUtil aliOssUtil;

  /**
   * 登录
   *
   * @param file
   * @return
   */
  @PostMapping("/upload")
  public Result<String> upload(MultipartFile file) {

    // 处理文件上传逻辑
    try {
      String originalFileName = file.getOriginalFilename();
      String objectName = UUID.randomUUID().toString() + originalFileName.substring(originalFileName.lastIndexOf("."));
      String fileUrl = aliOssUtil.upload(file.getBytes(), objectName);
      log.info("文件上传成功，文件地址：{}", fileUrl);
      return Result.success(fileUrl);
    } catch (IOException e) {

      log.info("文件上传失败，错误信息：{}", e.getMessage());
    }

    return Result.error(MessageConstant.UPLOAD_FAILED);
  }

}
