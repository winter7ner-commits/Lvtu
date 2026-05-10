package com.bitzh.lvtu.mapper.evaluation;

import com.bitzh.lvtu.entity.evaluation.Evaluation;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Optional;

/**
 * 评价Mapper接口
 */
@Mapper
public interface EvaluationMapper {

    int insert(Evaluation evaluation);
    int update(Evaluation evaluation);
    
    Optional<Evaluation> findById(@Param(""evaluationId"") Long evaluationId);
    Optional<Evaluation> findByOrderId(@Param(""orderId"") Long orderId);
    
    List<Evaluation> findByLawyerIdAndStatus(
        @Param(""lawyerId"") Long lawyerId,
        @Param(""status"") String status
    );
    
    int incrementReportCount(@Param(""evaluationId"") Long evaluationId);
}
