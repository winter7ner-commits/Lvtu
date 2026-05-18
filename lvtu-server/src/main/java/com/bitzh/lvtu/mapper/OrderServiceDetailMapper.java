package com.bitzh.lvtu.mapper;

import com.bitzh.lvtu.entity.OrderServiceDetail;
import org.apache.ibatis.annotations.Param;

public interface OrderServiceDetailMapper {

    OrderServiceDetail selectByOrderId(@Param("orderId") Long orderId);
}
