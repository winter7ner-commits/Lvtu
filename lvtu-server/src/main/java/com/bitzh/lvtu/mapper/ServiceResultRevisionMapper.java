package com.bitzh.lvtu.mapper;

import com.bitzh.lvtu.entity.ServiceResultRevision;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ServiceResultRevisionMapper {

    int insert(ServiceResultRevision revision);

    int countByOrderId(@Param("orderId") Long orderId);

    List<ServiceResultRevision> selectByOrderId(@Param("orderId") Long orderId);

    int markPendingHandled(@Param("orderId") Long orderId);
}
