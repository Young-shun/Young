package com.sky.dto;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import com.sky.query.PageQuery;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class OrdersPageQueryDTO extends PageQuery implements Serializable {

    private String number;

    private String phone;

    private Integer status;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime beginTime;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endTime;

    private Long userId;

}
