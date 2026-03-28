package com.sky.service;

import java.time.LocalDateTime;

import com.sky.vo.BusinessDataVO;
import com.sky.vo.DishOverViewVO;
import com.sky.vo.OrderOverViewVO;
import com.sky.vo.SetmealOverViewVO;

public interface WorkspaceService {
    BusinessDataVO getBusinessData(LocalDateTime begin, LocalDateTime end);
    OrderOverViewVO getOrderOverView();
    DishOverViewVO getDishOverView();
    SetmealOverViewVO getSetmealOverView();
}
