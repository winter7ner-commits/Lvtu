package com.bitzh.lvtu.mapper;

import com.bitzh.lvtu.entity.ServiceResultSubmission;
import com.bitzh.lvtu.entity.ServiceResultSubmissionAttachment;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ServiceResultSubmissionMapper {
    int insert(ServiceResultSubmission submission);

    int countByOrderId(@Param("orderId") Long orderId);

    List<ServiceResultSubmission> selectByOrderId(@Param("orderId") Long orderId);

    int batchInsertAttachments(@Param("attachments") List<ServiceResultSubmissionAttachment> attachments);

    List<ServiceResultSubmissionAttachment> selectAttachmentsBySubmissionIds(@Param("submissionIds") List<Long> submissionIds);
}
