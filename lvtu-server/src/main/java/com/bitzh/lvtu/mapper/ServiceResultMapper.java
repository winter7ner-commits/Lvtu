package com.bitzh.lvtu.mapper;

import com.bitzh.lvtu.entity.ServiceResult;
import org.apache.ibatis.annotations.Param;

public interface ServiceResultMapper {

    int insert(ServiceResult serviceResult);

    ServiceResult selectByOrderId(@Param("orderId") Long orderId);

    ServiceResult selectById(@Param("id") Long id);

    int update(ServiceResult serviceResult);

    int updateStatusByOrderId(@Param("orderId") Long orderId, @Param("status") Integer status);

    int updateStatusById(@Param("id") Long id, @Param("status") Integer status);
}
