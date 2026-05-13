package com.bitzh.lvtu.mapper;

import com.bitzh.lvtu.entity.Order;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.List;

@Mapper
public interface OrderMapper {

    // 基础 CRUD 操作
    int insert(Order order);

    int insertDetail(Order order);
    
    int update(Order order);
    
    int deleteById(@Param("orderId") Long orderId);

    //按用户id查询所有订单
    List<Order> selectByUserId(@Param("userId") Long userId);
    
    //按订单状态筛选订单
        // 待支付
        // 待接单
        // 处理中
        // 待评价
        // 已完成
    List<Order> selectByStatus(@Param("status") String status);
    
    //按订单id查询订单详情
    Order selectDetailByOrderId(@Param("orderId") Long orderId);

    Order selectById(@Param("orderId") Long orderId);
    
    // 按订单id搜索支付订单 并计算总金额
    BigDecimal calculateTotalAmountByOrderId(@Param("orderId") Long orderId);
}
