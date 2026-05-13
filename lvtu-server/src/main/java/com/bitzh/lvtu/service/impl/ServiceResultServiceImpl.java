package com.bitzh.lvtu.service.impl;

import com.bitzh.lvtu.dto.request.AttachmentRequest;
import com.bitzh.lvtu.dto.request.SubmitServiceResultRequest;
import com.bitzh.lvtu.dto.request.UpdateServiceResultRequest;
import com.bitzh.lvtu.dto.response.AttachmentResponse;
import com.bitzh.lvtu.dto.response.ServiceResultResponse;
import com.bitzh.lvtu.entity.ServiceOrder;
import com.bitzh.lvtu.entity.ServiceResult;
import com.bitzh.lvtu.entity.ServiceResultAttachment;
import com.bitzh.lvtu.exception.BusinessException;
import com.bitzh.lvtu.mapper.ServiceOrderMapper;
import com.bitzh.lvtu.mapper.ServiceResultAttachmentMapper;
import com.bitzh.lvtu.mapper.ServiceResultMapper;
import com.bitzh.lvtu.service.ServiceResultService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ServiceResultServiceImpl implements ServiceResultService {

    @Resource
    private ServiceOrderMapper serviceOrderMapper;

    @Resource
    private ServiceResultMapper serviceResultMapper;

    @Resource
    private ServiceResultAttachmentMapper serviceResultAttachmentMapper;

    @Override
    @Transactional
    public void submitResult(Long orderId, SubmitServiceResultRequest request) {
        validateSubmitRequest(request);
        ServiceOrder order = getRequiredOrder(orderId);

        if (!request.getLawyerId().equals(order.getLawyerId())) {
            throw new BusinessException("当前律师无权操作该订单");
        }
        if (!"处理中".equals(order.getStatus())) {
            throw new BusinessException("当前订单状态不允许提交服务结果");
        }

        ServiceResult existingResult = serviceResultMapper.selectByOrderId(orderId);
        if (existingResult != null) {
            throw new BusinessException("该订单已提交服务结果");
        }

        ServiceResult serviceResult = new ServiceResult();
        serviceResult.setOrderId(orderId);
        serviceResult.setLawyerId(request.getLawyerId());
        serviceResult.setTitle(request.getTitle());
        serviceResult.setContent(request.getContent());
        serviceResult.setStatus(0);
        serviceResultMapper.insert(serviceResult);

        insertAttachments(serviceResult.getId(), request.getAttachments());

        int updated = serviceOrderMapper.updateStatusWithCurrent(orderId, "处理中", "待评价");
        if (updated == 0) {
            throw new BusinessException("订单状态更新失败");
        }
    }

    @Override
    public ServiceResultResponse getResultByOrderId(Long orderId, Long lawyerId) {
        if (lawyerId == null) {
            throw new BusinessException("律师ID不能为空");
        }

        ServiceOrder order = getRequiredOrder(orderId);
        if (!lawyerId.equals(order.getLawyerId())) {
            throw new BusinessException("只有该订单的接单律师可以查看服务结果");
        }

        ServiceResult result = serviceResultMapper.selectByOrderId(orderId);
        if (result == null) {
            throw new BusinessException("服务结果不存在");
        }

        return toServiceResultResponse(result);
    }

    @Override
    public ServiceResultResponse getResultByOrderIdForUser(Long orderId, Long userId) {
        if (userId == null) {
            throw new BusinessException("用户ID不能为空");
        }

        ServiceOrder order = getRequiredOrder(orderId);
        if (!userId.equals(order.getUserId())) {
            throw new BusinessException("只有下单用户可以查看服务结果");
        }

        ServiceResult result = serviceResultMapper.selectByOrderId(orderId);
        if (result == null) {
            throw new BusinessException("服务结果不存在");
        }

        return toServiceResultResponse(result);
    }

    @Override
    @Transactional
    public void updateResult(Long resultId, UpdateServiceResultRequest request) {
        validateUpdateRequest(request);
        ServiceResult result = serviceResultMapper.selectById(resultId);
        if (result == null) {
            throw new BusinessException("服务结果不存在");
        }

        ServiceOrder order = getRequiredOrder(result.getOrderId());
        if (!request.getLawyerId().equals(order.getLawyerId()) || !request.getLawyerId().equals(result.getLawyerId())) {
            throw new BusinessException("只有该订单的接单律师可以修改服务结果");
        }
        if (!(result.getStatus() == 0 || result.getStatus() == 2)) {
            throw new BusinessException("当前服务结果状态不允许修改");
        }

        result.setTitle(request.getTitle());
        result.setContent(request.getContent());
        serviceResultMapper.update(result);

        if (request.getAttachments() != null) {
            serviceResultAttachmentMapper.deleteByResultId(resultId);
            insertAttachments(resultId, request.getAttachments());
        }
    }

    private ServiceOrder getRequiredOrder(Long orderId) {
        ServiceOrder order = serviceOrderMapper.selectByOrderId(orderId);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }
        return order;
    }

    private void validateSubmitRequest(SubmitServiceResultRequest request) {
        if (request == null || request.getLawyerId() == null) {
            throw new BusinessException("律师ID不能为空");
        }
        if (request.getTitle() == null || request.getTitle().isBlank()) {
            throw new BusinessException("结果标题不能为空");
        }
    }

    private void validateUpdateRequest(UpdateServiceResultRequest request) {
        if (request == null || request.getLawyerId() == null) {
            throw new BusinessException("律师ID不能为空");
        }
        if (request.getTitle() == null || request.getTitle().isBlank()) {
            throw new BusinessException("结果标题不能为空");
        }
    }

    private void insertAttachments(Long resultId, List<AttachmentRequest> attachments) {
        if (attachments == null || attachments.isEmpty()) {
            return;
        }

        List<ServiceResultAttachment> entities = attachments.stream().map(item -> {
            ServiceResultAttachment attachment = new ServiceResultAttachment();
            attachment.setResultId(resultId);
            attachment.setFileName(item.getFileName());
            attachment.setFileUrl(item.getFileUrl());
            attachment.setFileType(item.getFileType());
            return attachment;
        }).collect(Collectors.toList());

        serviceResultAttachmentMapper.batchInsert(entities);
    }

    private ServiceResultResponse toServiceResultResponse(ServiceResult result) {
        ServiceResultResponse response = new ServiceResultResponse();
        response.setId(result.getId());
        response.setOrderId(result.getOrderId());
        response.setLawyerId(result.getLawyerId());
        response.setTitle(result.getTitle());
        response.setContent(result.getContent());
        response.setStatus(result.getStatus());
        response.setCreatedAt(result.getCreatedAt());
        response.setUpdatedAt(result.getUpdatedAt());

        List<ServiceResultAttachment> attachments = serviceResultAttachmentMapper.selectByResultId(result.getId());
        if (attachments == null || attachments.isEmpty()) {
            response.setAttachments(Collections.emptyList());
            return response;
        }

        response.setAttachments(attachments.stream().map(item -> {
            AttachmentResponse attachmentResponse = new AttachmentResponse();
            attachmentResponse.setId(item.getId());
            attachmentResponse.setFileName(item.getFileName());
            attachmentResponse.setFileUrl(item.getFileUrl());
            attachmentResponse.setFileType(item.getFileType());
            attachmentResponse.setCreatedAt(item.getCreatedAt());
            return attachmentResponse;
        }).collect(Collectors.toList()));
        return response;
    }
}
