package com.bitzh.lvtu.exception.evaluation;

import com.bitzh.lvtu.exception.BusinessException;

/**
 * 评价举报业务异常
 */
public class ReportException extends BusinessException {
    public ReportException(String message) {
        super(""REPORT_ERROR"", message);
    }
}
