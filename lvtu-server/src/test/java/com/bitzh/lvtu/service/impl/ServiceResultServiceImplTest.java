package com.bitzh.lvtu.service.impl;

import com.bitzh.lvtu.dto.request.RequestRevisionRequest;
import com.bitzh.lvtu.dto.response.ServiceResultResponse;
import com.bitzh.lvtu.entity.ServiceOrder;
import com.bitzh.lvtu.entity.ServiceResult;
import com.bitzh.lvtu.mapper.LawyerProfileMapper;
import com.bitzh.lvtu.mapper.ServiceOrderMapper;
import com.bitzh.lvtu.mapper.ServiceResultAttachmentMapper;
import com.bitzh.lvtu.mapper.ServiceResultMapper;
import com.bitzh.lvtu.mapper.ServiceResultRevisionMapper;
import com.bitzh.lvtu.mapper.ServiceResultSubmissionMapper;
import com.bitzh.lvtu.service.NotificationService;
import com.bitzh.lvtu.service.SystemConfigService;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ServiceResultServiceImplTest {

    @Test
    void requestRevisionDoesNotFailWhenLawyerNotificationTargetIsMissing() {
        ServiceResultServiceImpl service = new ServiceResultServiceImpl();
        ServiceOrderMapper serviceOrderMapper = mock(ServiceOrderMapper.class);
        ServiceResultMapper serviceResultMapper = mock(ServiceResultMapper.class);
        ServiceResultAttachmentMapper attachmentMapper = mock(ServiceResultAttachmentMapper.class);
        ServiceResultRevisionMapper revisionMapper = mock(ServiceResultRevisionMapper.class);
        ServiceResultSubmissionMapper submissionMapper = mock(ServiceResultSubmissionMapper.class);
        LawyerProfileMapper lawyerProfileMapper = mock(LawyerProfileMapper.class);
        NotificationService notificationService = mock(NotificationService.class);
        SystemConfigService systemConfigService = mock(SystemConfigService.class);

        ReflectionTestUtils.setField(service, "serviceOrderMapper", serviceOrderMapper);
        ReflectionTestUtils.setField(service, "serviceResultMapper", serviceResultMapper);
        ReflectionTestUtils.setField(service, "serviceResultAttachmentMapper", attachmentMapper);
        ReflectionTestUtils.setField(service, "serviceResultRevisionMapper", revisionMapper);
        ReflectionTestUtils.setField(service, "serviceResultSubmissionMapper", submissionMapper);
        ReflectionTestUtils.setField(service, "lawyerProfileMapper", lawyerProfileMapper);
        ReflectionTestUtils.setField(service, "notificationService", notificationService);
        ReflectionTestUtils.setField(service, "systemConfigService", systemConfigService);

        Long orderId = 800004L;
        ServiceOrder order = new ServiceOrder();
        order.setOrderId(orderId);
        order.setUserId(500002L);
        order.setLawyerId(700001L);
        order.setStatus("待客户确认");

        ServiceResult result = new ServiceResult();
        result.setId(900001L);
        result.setOrderId(orderId);
        result.setLawyerId(700001L);
        result.setTitle("处理结果");
        result.setContent("已完成处理");
        result.setStatus(0);

        RequestRevisionRequest request = new RequestRevisionRequest();
        request.setUserId(500002L);
        request.setContent("请补充说明");

        when(serviceOrderMapper.selectByOrderId(orderId)).thenReturn(order);
        when(serviceResultMapper.selectByOrderId(orderId)).thenReturn(result);
        when(submissionMapper.countByOrderId(orderId)).thenReturn(1);
        when(revisionMapper.countByOrderId(orderId)).thenReturn(0, 1);
        when(systemConfigService.getMaxRevisionRequestCount()).thenReturn(2);
        when(serviceOrderMapper.updateStatusWithCurrent(orderId, "待客户确认", "处理中")).thenReturn(1);
        when(lawyerProfileMapper.selectByLawyerId(700001L)).thenReturn(null);
        when(revisionMapper.selectByOrderId(orderId)).thenReturn(Collections.emptyList());
        when(submissionMapper.selectByOrderId(orderId)).thenReturn(Collections.emptyList());
        when(attachmentMapper.selectByResultId(900001L)).thenReturn(Collections.emptyList());

        ServiceResultResponse response = service.requestRevision(orderId, request);

        assertNotNull(response);
        assertEquals(orderId, response.getOrderId());
        verify(revisionMapper).insert(any());
        verify(serviceResultMapper).updateStatusById(900001L, 2);
        verify(notificationService).create(
                eq(null),
                eq("SERVICE_RESULT_REVISION_REQUESTED"),
                eq("用户申请修改服务结果"),
                eq("用户对订单 #" + orderId + " 的服务结果提交了修改意见，请查看并重新处理。"),
                eq(orderId),
                eq("/lawyer/orders/" + orderId)
        );
    }
}
