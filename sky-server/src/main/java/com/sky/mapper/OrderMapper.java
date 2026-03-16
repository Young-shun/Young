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

  /**
   * 统计当日销售额
   * 
   * @param beginTime
   * @param endTime
   * @param status
   * @return
   */
  @Select("select sum(amount) from orders where status = #{status} and order_time >= #{beginTime} and order_time < #{endTime} ")
  Double getTotalAmount(LocalDateTime beginTime, LocalDateTime endTime, Integer status);

  /**
   * 查询商品销量排名
   * 
   * @param begin
   * @param end
   */
  List<GoodsSalesDTO> getSalesTop10(LocalDateTime begin, LocalDateTime end);

}
