package com.bitzh.lvtu.mapper;

import com.bitzh.lvtu.entity.Payment;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface PaymentMapper {
    
    // 基础 CRUD 操作
    int insert(Payment payment);

    int update(Payment payment);

    int deleteById(@Param("paymentId") Long paymentId);

    //按支付订单id搜索
    Payment selectById(@Param("paymentId") Long paymentId);
    
    //按支付状态搜索
     // 待支付
     // 已支付
     // 已取消
    List<Payment> selectByStatus(@Param("status") String status);
     
    // 按订单id搜索支付信息
    List<Payment> selectByOrderId(@Param("orderId") Long orderId);
}
