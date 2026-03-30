package com.sky.mapper;

import java.time.LocalDateTime;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sky.dto.GoodsSalesDTO;
import com.sky.entity.Orders;

@Mapper
public interface OrderMapper extends BaseMapper<Orders> {

  @Select("select sum(amount) from orders where status = #{status} and order_time >= #{beginTime} and order_time < #{endTime} ")
  /**
   * 作用: 执行getTotalAmount相关逻辑。
   * 输入: LocalDateTime beginTime, LocalDateTime endTime, Integer status。
   * 输出: Double。
   */
  Double getTotalAmount(LocalDateTime beginTime, LocalDateTime endTime, Integer status);

  List<GoodsSalesDTO> getSalesTop10(LocalDateTime begin, LocalDateTime end);

}
