package com.bitzh.lvtu.mapper;

import com.bitzh.lvtu.entity.ServiceResultAttachment;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ServiceResultAttachmentMapper {

    int batchInsert(@Param("attachments") List<ServiceResultAttachment> attachments);

    List<ServiceResultAttachment> selectByResultId(@Param("resultId") Long resultId);

    int deleteByResultId(@Param("resultId") Long resultId);
}
