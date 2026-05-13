package com.bitzh.lvtu.mapper.evaluation;

import com.bitzh.lvtu.entity.evaluation.AdminEvaluationLog;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 管理员评价操作日志Mapper接口
 */
@Mapper
public interface AdminEvaluationLogMapper {

    int insert(AdminEvaluationLog log);
    
    List<AdminEvaluationLog> findByEvaluationIdOrderByCreatedTimeDesc(
        @Param(""evaluationId"") Long evaluationId
    );
}
