package com.bitzh.lvtu.service.impl;

import com.bitzh.lvtu.dto.request.AttachmentRequest;
import com.bitzh.lvtu.dto.request.RequestRevisionRequest;
import com.bitzh.lvtu.dto.request.SubmitServiceResultRequest;
import com.bitzh.lvtu.dto.request.UpdateServiceResultRequest;
import com.bitzh.lvtu.dto.response.AttachmentResponse;
import com.bitzh.lvtu.dto.response.ResultRevisionResponse;
import com.bitzh.lvtu.dto.response.ServiceResultResponse;
import com.bitzh.lvtu.dto.response.ServiceResultSubmissionResponse;
import com.bitzh.lvtu.entity.ServiceOrder;
import com.bitzh.lvtu.entity.ServiceResult;
import com.bitzh.lvtu.entity.ServiceResultAttachment;
import com.bitzh.lvtu.entity.ServiceResultRevision;
import com.bitzh.lvtu.entity.ServiceResultSubmission;
import com.bitzh.lvtu.entity.ServiceResultSubmissionAttachment;
import com.bitzh.lvtu.exception.BusinessException;
import com.bitzh.lvtu.mapper.ServiceOrderMapper;
import com.bitzh.lvtu.mapper.ServiceResultAttachmentMapper;
import com.bitzh.lvtu.mapper.ServiceResultMapper;
import com.bitzh.lvtu.mapper.ServiceResultRevisionMapper;
import com.bitzh.lvtu.mapper.ServiceResultSubmissionMapper;
import com.bitzh.lvtu.mapper.LawyerProfileMapper;
import com.bitzh.lvtu.service.ServiceResultService;
import com.bitzh.lvtu.service.NotificationService;
import com.bitzh.lvtu.service.SystemConfigService;
import jakarta.annotation.Resource;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ServiceResultServiceImpl implements ServiceResultService {

    private static final int RESULT_STATUS_SUBMITTED = 0;
    private static final int RESULT_STATUS_NEEDS_REVISION = 2;
    private static final int REVISION_STATUS_PENDING = 0;
    private static final int REVISION_STATUS_HANDLED = 1;
    private static final int REVISION_STATUS_INTERVENTION = 2;

    @Resource
    private ServiceOrderMapper serviceOrderMapper;

    @Resource
    private ServiceResultMapper serviceResultMapper;

    @Resource
    private ServiceResultAttachmentMapper serviceResultAttachmentMapper;

    @Resource
    private ServiceResultRevisionMapper serviceResultRevisionMapper;

    @Resource
    private ServiceResultSubmissionMapper serviceResultSubmissionMapper;

    @Resource
    private LawyerProfileMapper lawyerProfileMapper;

    @Resource
    private NotificationService notificationService;

    @Resource
    private SystemConfigService systemConfigService;

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
        serviceResult.setStatus(RESULT_STATUS_SUBMITTED);
        serviceResultMapper.insert(serviceResult);

        insertAttachments(serviceResult.getId(), request.getAttachments());
        createSubmissionSnapshot(
                orderId,
                serviceResult.getId(),
                request.getLawyerId(),
                request.getTitle(),
                request.getContent(),
                request.getAttachments()
        );

        int updated = serviceOrderMapper.updateStatusWithCurrent(orderId, "处理中", "待客户确认");
        if (updated == 0) {
            throw new BusinessException("订单状态更新失败");
        }
        notificationService.create(
                order.getUserId(),
                "SERVICE_RESULT_SUBMITTED",
                "律师已提交服务结果",
                "您的订单 #" + orderId + " 已提交服务结果，请查看并确认是否满意。",
                orderId,
                "/orders/" + orderId
        );
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
        if (!(result.getStatus() == RESULT_STATUS_SUBMITTED || result.getStatus() == RESULT_STATUS_NEEDS_REVISION)) {
            throw new BusinessException("当前服务结果状态不允许修改");
        }
        boolean resolvingRevision = result.getStatus() == RESULT_STATUS_NEEDS_REVISION;
        if (resolvingRevision && !"处理中".equals(order.getStatus())) {
            throw new BusinessException("当前订单状态不允许重新提交服务结果");
        }

        ensureExistingResultSnapshot(result);

        result.setTitle(request.getTitle());
        result.setContent(request.getContent());
        result.setStatus(RESULT_STATUS_SUBMITTED);
        serviceResultMapper.update(result);

        if (request.getAttachments() != null) {
            serviceResultAttachmentMapper.deleteByResultId(resultId);
            insertAttachments(resultId, request.getAttachments());
        }
        createSubmissionSnapshot(
                result.getOrderId(),
                resultId,
                request.getLawyerId(),
                request.getTitle(),
                request.getContent(),
                request.getAttachments()
        );

        if (resolvingRevision) {
            int updated = serviceOrderMapper.updateStatusWithCurrent(result.getOrderId(), "处理中", "待客户确认");
            if (updated == 0) {
                throw new BusinessException("订单状态更新失败");
            }
            serviceResultRevisionMapper.markPendingHandled(result.getOrderId());
            notificationService.create(
                    order.getUserId(),
                    "SERVICE_RESULT_RESUBMITTED",
                    "律师已重新提交服务结果",
                    "您的订单 #" + result.getOrderId() + " 已根据修改意见重新提交服务结果，请查看确认。",
                    result.getOrderId(),
                    "/orders/" + result.getOrderId()
            );
        }
    }

    @Override
    @Transactional
    public ServiceResultResponse requestRevision(Long orderId, RequestRevisionRequest request) {
        if (request == null || request.getUserId() == null) {
            throw new BusinessException("用户ID不能为空");
        }
        if (request.getContent() == null || request.getContent().isBlank()) {
            throw new BusinessException("修改意见不能为空");
        }

        ServiceOrder order = getRequiredOrder(orderId);
        if (!request.getUserId().equals(order.getUserId())) {
            throw new BusinessException("只有下单用户可以申请修改服务结果");
        }
        if (!"待客户确认".equals(order.getStatus())) {
            throw new BusinessException("当前订单状态不允许申请修改");
        }
        if (order.getLawyerId() == null) {
            throw new BusinessException("订单尚未分配律师");
        }

        ServiceResult result = serviceResultMapper.selectByOrderId(orderId);
        if (result == null) {
            throw new BusinessException("服务结果不存在");
        }
        if (!order.getLawyerId().equals(result.getLawyerId())) {
            throw new BusinessException("服务结果与订单律师信息不一致");
        }

        ensureExistingResultSnapshot(result);

        int revisionCount = serviceResultRevisionMapper.countByOrderId(orderId);
        int maxRevisionRequestCount = systemConfigService.getMaxRevisionRequestCount();
        boolean needIntervention = revisionCount >= maxRevisionRequestCount;
        ServiceResultRevision revision = new ServiceResultRevision();
        revision.setOrderId(orderId);
        revision.setResultId(result.getId());
        revision.setUserId(request.getUserId());
        revision.setLawyerId(order.getLawyerId());
        revision.setRevisionNo(revisionCount + 1);
        revision.setContent(request.getContent().trim());
        revision.setStatus(needIntervention ? REVISION_STATUS_INTERVENTION : REVISION_STATUS_PENDING);
        serviceResultRevisionMapper.insert(revision);

        serviceResultMapper.updateStatusById(result.getId(), RESULT_STATUS_NEEDS_REVISION);

        String nextStatus = needIntervention ? "平台介入" : "处理中";
        int updated = serviceOrderMapper.updateStatusWithCurrent(orderId, "待客户确认", nextStatus);
        if (updated == 0) {
            throw new BusinessException("订单状态更新失败");
        }

        Long lawyerUserId = lawyerProfileMapper.selectByLawyerId(order.getLawyerId()).getUserId();
        notificationService.create(
                lawyerUserId,
                needIntervention ? "ORDER_INTERVENTION" : "SERVICE_RESULT_REVISION_REQUESTED",
                needIntervention ? "订单进入平台介入" : "用户申请修改服务结果",
                needIntervention
                        ? "订单 #" + orderId + " 的修改次数已达上限，用户已申请平台介入。"
                        : "用户对订单 #" + orderId + " 的服务结果提交了修改意见，请查看并重新处理。",
                orderId,
                "/lawyer/orders/" + orderId
        );

        ServiceResult updatedResult = serviceResultMapper.selectByOrderId(orderId);
        return toServiceResultResponse(updatedResult);
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
        response.setRevisionRequests(toRevisionResponses(serviceResultRevisionMapper.selectByOrderId(result.getOrderId())));
        response.setSubmissions(loadSubmissionResponses(result.getOrderId()));
        response.setRevisionRequestCount(serviceResultRevisionMapper.countByOrderId(result.getOrderId()));
        response.setMaxRevisionRequestCount(systemConfigService.getMaxRevisionRequestCount());

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

    private void createSubmissionSnapshot(Long orderId,
                                          Long resultId,
                                          Long lawyerId,
                                          String title,
                                          String content,
                                          List<AttachmentRequest> attachments) {
        ServiceResultSubmission submission = new ServiceResultSubmission();
        submission.setOrderId(orderId);
        submission.setResultId(resultId);
        submission.setLawyerId(lawyerId);
        submission.setSubmissionNo(getSubmissionCount(orderId) + 1);
        submission.setTitle(title);
        submission.setContent(content);
        try {
            serviceResultSubmissionMapper.insert(submission);
        } catch (DataAccessException ex) {
            return;
        }

        if (attachments == null || attachments.isEmpty()) {
            return;
        }

        List<ServiceResultSubmissionAttachment> snapshotAttachments = attachments.stream().map(item -> {
            ServiceResultSubmissionAttachment attachment = new ServiceResultSubmissionAttachment();
            attachment.setSubmissionId(submission.getId());
            attachment.setFileName(item.getFileName());
            attachment.setFileUrl(item.getFileUrl());
            attachment.setFileType(item.getFileType());
            return attachment;
        }).collect(Collectors.toList());
        try {
            serviceResultSubmissionMapper.batchInsertAttachments(snapshotAttachments);
        } catch (DataAccessException ignored) {
            // The current result has already been saved. History attachments can be backfilled after running SQL migration.
        }
    }

    private void ensureExistingResultSnapshot(ServiceResult result) {
        if (getSubmissionCount(result.getOrderId()) > 0) {
            return;
        }
        List<ServiceResultAttachment> attachments = serviceResultAttachmentMapper.selectByResultId(result.getId());
        List<AttachmentRequest> attachmentRequests = attachments == null ? Collections.emptyList() : attachments.stream().map(item -> {
            AttachmentRequest request = new AttachmentRequest();
            request.setFileName(item.getFileName());
            request.setFileUrl(item.getFileUrl());
            request.setFileType(item.getFileType());
            return request;
        }).collect(Collectors.toList());

        createSubmissionSnapshot(
                result.getOrderId(),
                result.getId(),
                result.getLawyerId(),
                result.getTitle(),
                result.getContent(),
                attachmentRequests
        );
    }

    private int getSubmissionCount(Long orderId) {
        try {
            return serviceResultSubmissionMapper.countByOrderId(orderId);
        } catch (DataAccessException ex) {
            return 0;
        }
    }

    private List<ServiceResultSubmissionResponse> loadSubmissionResponses(Long orderId) {
        try {
            return toSubmissionResponses(serviceResultSubmissionMapper.selectByOrderId(orderId));
        } catch (DataAccessException ex) {
            return Collections.emptyList();
        }
    }

    private List<ServiceResultSubmissionResponse> toSubmissionResponses(List<ServiceResultSubmission> submissions) {
        if (submissions == null || submissions.isEmpty()) {
            return Collections.emptyList();
        }

        List<Long> submissionIds = submissions.stream()
                .map(ServiceResultSubmission::getId)
                .collect(Collectors.toList());
        Map<Long, List<ServiceResultSubmissionAttachment>> attachmentMap =
                serviceResultSubmissionMapper.selectAttachmentsBySubmissionIds(submissionIds).stream()
                        .collect(Collectors.groupingBy(ServiceResultSubmissionAttachment::getSubmissionId));

        return submissions.stream().map(item -> {
            ServiceResultSubmissionResponse response = new ServiceResultSubmissionResponse();
            response.setId(item.getId());
            response.setOrderId(item.getOrderId());
            response.setResultId(item.getResultId());
            response.setLawyerId(item.getLawyerId());
            response.setSubmissionNo(item.getSubmissionNo());
            response.setTitle(item.getTitle());
            response.setContent(item.getContent());
            response.setCreatedAt(item.getCreatedAt());
            List<ServiceResultSubmissionAttachment> attachments = attachmentMap.getOrDefault(item.getId(), Collections.emptyList());
            response.setAttachments(attachments.stream().map(attachment -> {
                AttachmentResponse attachmentResponse = new AttachmentResponse();
                attachmentResponse.setId(attachment.getId());
                attachmentResponse.setFileName(attachment.getFileName());
                attachmentResponse.setFileUrl(attachment.getFileUrl());
                attachmentResponse.setFileType(attachment.getFileType());
                attachmentResponse.setCreatedAt(attachment.getCreatedAt());
                return attachmentResponse;
            }).collect(Collectors.toList()));
            return response;
        }).collect(Collectors.toList());
    }

    private List<ResultRevisionResponse> toRevisionResponses(List<ServiceResultRevision> revisions) {
        if (revisions == null || revisions.isEmpty()) {
            return Collections.emptyList();
        }
        return revisions.stream().map(item -> {
            ResultRevisionResponse response = new ResultRevisionResponse();
            response.setId(item.getId());
            response.setOrderId(item.getOrderId());
            response.setResultId(item.getResultId());
            response.setUserId(item.getUserId());
            response.setLawyerId(item.getLawyerId());
            response.setRevisionNo(item.getRevisionNo());
            response.setContent(item.getContent());
            response.setStatus(item.getStatus());
            response.setCreatedAt(item.getCreatedAt());
            response.setHandledAt(item.getHandledAt());
            return response;
        }).collect(Collectors.toList());
    }
}
