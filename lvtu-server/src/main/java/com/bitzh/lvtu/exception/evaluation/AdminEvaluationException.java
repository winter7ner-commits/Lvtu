package com.bitzh.lvtu.exception.evaluation;

import com.bitzh.lvtu.exception.BusinessException;

/**
 * 管理员评价操作业务异常
 */
public class AdminEvaluationException extends BusinessException {
    public AdminEvaluationException(String message) {
        super(400, message);
    }
}
