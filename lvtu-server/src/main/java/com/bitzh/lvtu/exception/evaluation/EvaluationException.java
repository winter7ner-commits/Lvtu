package com.bitzh.lvtu.exception.evaluation;

import com.bitzh.lvtu.exception.BusinessException;

/**
 * 评价业务异常
 */
public class EvaluationException extends BusinessException {
    public EvaluationException(String message) {
        super(400, message);
    }
}
