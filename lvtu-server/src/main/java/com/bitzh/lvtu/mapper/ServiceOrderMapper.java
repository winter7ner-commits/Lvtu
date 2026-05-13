package com.bitzh.lvtu.mapper;

import com.bitzh.lvtu.entity.ServiceOrder;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ServiceOrderMapper {

    List<ServiceOrder> selectAvailableOrders(@Param("serviceTypeId") Integer serviceTypeId);

    ServiceOrder selectByOrderId(@Param("orderId") Long orderId);

    int acceptOrder(@Param("orderId") Long orderId, @Param("lawyerId") Long lawyerId);

    List<ServiceOrder> selectMyOrders(@Param("lawyerId") Long lawyerId, @Param("status") String status);

    int updateStatus(@Param("orderId") Long orderId, @Param("status") String status);

    int updateStatusWithCurrent(@Param("orderId") Long orderId,
                                @Param("currentStatus") String currentStatus,
                                @Param("newStatus") String newStatus);
}
