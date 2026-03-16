package com.sky.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.toolkit.Db;
import com.sky.constant.StatusConstant;
import com.sky.entity.Dish;
import com.sky.entity.Orders;
import com.sky.entity.Setmeal;
import com.sky.entity.User;
import com.sky.mapper.DishMapper;
import com.sky.mapper.OrderMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.mapper.UserMapper;
import com.sky.service.WorkspaceService;
import com.sky.vo.BusinessDataVO;
import com.sky.vo.DishOverViewVO;
import com.sky.vo.OrderOverViewVO;
import com.sky.vo.SetmealOverViewVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;

import javax.management.Query;

@Service
@Slf4j
public class WorkspaceServiceImpl implements WorkspaceService {

        @Autowired
        private OrderMapper orderMapper;
        @Autowired
        private UserMapper userMapper;
        @Autowired
        private DishMapper dishMapper;
        @Autowired
        private SetmealMapper setmealMapper;

        /**
         * 根据时间段统计营业数据
         * 
         * @param begin
         * @param end
         * @return
         */
        public BusinessDataVO getBusinessData(LocalDateTime begin, LocalDateTime end) {
                /**
                 * 营业额：当日已完成订单的总金额
                 * 有效订单：当日已完成订单的数量
                 * 订单完成率：有效订单数 / 总订单数
                 * 平均客单价：营业额 / 有效订单数
                 * 新增用户：当日新增用户的数量
                 */

                Map map = new HashMap();
                map.put("begin", begin);
                map.put("end", end);
                // 查询总订单数
                Long totalOrderCount = orderMapper.selectCount(new LambdaQueryWrapper<Orders>()
                                .between(Orders::getOrderTime, begin, end));

                map.put("status", Orders.COMPLETED);
                // 营业额
                Double turnover = orderMapper.getTotalAmount(begin, end, Orders.COMPLETED);
                turnover = turnover == null ? 0.0 : turnover;

                // 有效订单数
                Long validOrderCount = orderMapper.selectCount(new LambdaQueryWrapper<Orders>()
                                .between(Orders::getOrderTime, begin, end)
                                .eq(Orders::getStatus, Orders.COMPLETED));

                Double unitPrice = 0.0;

                Double orderCompletionRate = 0.0;
                if (totalOrderCount != 0 && validOrderCount != 0) {
                        // 订单完成率
                        orderCompletionRate = validOrderCount.doubleValue() / totalOrderCount;
                        // 平均客单价
                        unitPrice = turnover / validOrderCount;
                }

                // 新增用户数
                Long newUsers = Db.lambdaQuery(User.class)
                                .ge(User::getCreateTime, begin)
                                .lt(User::getCreateTime, end)
                                .count();

                return BusinessDataVO.builder()
                                .turnover(turnover)
                                .validOrderCount(validOrderCount.intValue())
                                .orderCompletionRate(orderCompletionRate)
                                .unitPrice(unitPrice)
                                .newUsers(newUsers.intValue())
                                .build();
        }

        /**
         * 查询订单管理数据
         *
         * @return
         */
        public OrderOverViewVO getOrderOverView() {
                Map map = new HashMap();
                map.put("begin", LocalDateTime.now().with(LocalTime.MIN));
                map.put("status", Orders.TO_BE_CONFIRMED);
                LocalDateTime begin = LocalDateTime.now().with(LocalTime.MIN);
                LocalDateTime end = LocalDateTime.now().with(LocalTime.MAX);
                // 待接单
                Long waitingOrders = orderMapper.selectCount(new LambdaQueryWrapper<Orders>()
                                .between(Orders::getOrderTime, begin, end)
                                .eq(Orders::getStatus, Orders.TO_BE_CONFIRMED));

                // 待派送
                map.put("status", Orders.CONFIRMED);
                Long deliveredOrders = orderMapper.selectCount(new LambdaQueryWrapper<Orders>()
                                .between(Orders::getOrderTime, begin, end)
                                .eq(Orders::getStatus, Orders.CONFIRMED));

                // 已完成
                map.put("status", Orders.COMPLETED);
                Long completedOrders = orderMapper.selectCount(new LambdaQueryWrapper<Orders>()
                                .between(Orders::getOrderTime, begin, end)
                                .eq(Orders::getStatus, Orders.COMPLETED));

                // 已取消
                map.put("status", Orders.CANCELLED);
                Long cancelledOrders = orderMapper.selectCount(new LambdaQueryWrapper<Orders>()
                                .between(Orders::getOrderTime, begin, end)
                                .eq(Orders::getStatus, Orders.CANCELLED));

                // 全部订单
                map.put("status", null);
                Long allOrders = orderMapper.selectCount(new LambdaQueryWrapper<Orders>()
                                .between(Orders::getOrderTime, begin, end));

                return OrderOverViewVO.builder()
                                .waitingOrders(waitingOrders.intValue())
                                .deliveredOrders(deliveredOrders.intValue())
                                .completedOrders(completedOrders.intValue())
                                .cancelledOrders(cancelledOrders.intValue())
                                .allOrders(allOrders.intValue())
                                .build();
        }

        /**
         * 查询菜品总览
         *
         * @return
         */
        public DishOverViewVO getDishOverView() {
                long sold = Db.lambdaQuery(Dish.class)
                                .eq(Dish::getStatus, StatusConstant.ENABLE).count();

                long discontinued = Db.lambdaQuery(Dish.class)
                                .eq(Dish::getStatus, StatusConstant.DISABLE).count();

                return DishOverViewVO.builder()
                                .sold((int) sold)
                                .discontinued((int) discontinued)
                                .build();
        }

        /**
         * 查询套餐总览
         *
         * @return
         */
        public SetmealOverViewVO getSetmealOverView() {

                long sold = Db.lambdaQuery(Setmeal.class)
                                .eq(Setmeal::getStatus, StatusConstant.ENABLE).count();

                long discontinued = Db.lambdaQuery(Setmeal.class)
                                .eq(Setmeal::getStatus, StatusConstant.DISABLE).count();

                return SetmealOverViewVO.builder()
                                .sold((int) sold)
                                .discontinued((int) discontinued)
                                .build();
        }
}
