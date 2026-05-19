package com.bitzh.lvtu.mapper.evaluation;

import com.bitzh.lvtu.entity.evaluation.EvaluationReport;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 评价举报Mapper接口
 */
@Mapper
public interface EvaluationReportMapper {

    int insert(EvaluationReport report);
    
    List<EvaluationReport> findByEvaluationId(@Param("evaluationId") Long evaluationId);
    
    List<EvaluationReport> findByEvaluationIdAndReporterId(
        @Param("evaluationId") Long evaluationId,
        @Param("reporterId") Long reporterId
    );
}
