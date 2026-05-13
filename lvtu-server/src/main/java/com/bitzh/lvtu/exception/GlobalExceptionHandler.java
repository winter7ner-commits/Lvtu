package com.bitzh.lvtu.exception;

import com.bitzh.lvtu.common.ApiResponse;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // 处理业务逻辑参数校验异常 (如 UserServiceImpl 抛出的异常)
    @ExceptionHandler(IllegalArgumentException.class)
    public ApiResponse<String> handleIllegalArgument(IllegalArgumentException e) {
        return ApiResponse.error(400, e.getMessage());
    }

    // 处理状态异常
    @ExceptionHandler(IllegalStateException.class)
    public ApiResponse<String> handleIllegalState(IllegalStateException e) {
        return ApiResponse.error(500, e.getMessage());
    }

    // 处理自定义业务异常
    @ExceptionHandler(BusinessException.class)
    public ApiResponse<String> handleBusinessException(BusinessException e) {
        return ApiResponse.error(e.getCode(), e.getMessage());
    }

    // 兜底处理其他未知异常
    @ExceptionHandler(Exception.class)
    public ApiResponse<String> handleException(Exception e) {
        return ApiResponse.error(500, "服务器内部错误");
    }
}
