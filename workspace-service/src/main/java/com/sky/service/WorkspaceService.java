package com.sky.service;

import java.time.LocalDateTime;

import com.sky.vo.BusinessDataVO;
import com.sky.vo.DishOverViewVO;
import com.sky.vo.OrderOverViewVO;
import com.sky.vo.SetmealOverViewVO;

public interface WorkspaceService {
    /**
     * 作用: 执行getBusinessData相关逻辑。
     * 输入: LocalDateTime begin, LocalDateTime end。
     * 输出: BusinessDataVO。
     */
    BusinessDataVO getBusinessData(LocalDateTime begin, LocalDateTime end);
    /**
     * 作用: 执行getOrderOverView相关逻辑。
     * 输入: 无。
     * 输出: OrderOverViewVO。
     */
    OrderOverViewVO getOrderOverView();
    /**
     * 作用: 执行getDishOverView相关逻辑。
     * 输入: 无。
     * 输出: DishOverViewVO。
     */
    DishOverViewVO getDishOverView();
    /**
     * 作用: 执行getSetmealOverView相关逻辑。
     * 输入: 无。
     * 输出: SetmealOverViewVO。
     */
    SetmealOverViewVO getSetmealOverView();
}
