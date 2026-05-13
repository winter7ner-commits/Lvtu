package com.bitzh.lvtu.common;

import java.time.OffsetDateTime;

public class ApiResponse<T> {

    private int code;
    private String message;
    private T data;
    private String timestamp;
    private Object errors;

    public ApiResponse() {
    }

    public ApiResponse(int code, String message, T data, Object errors) {
        this.code = code;
        this.message = message;
        this.data = data;
        this.errors = errors;
        this.timestamp = OffsetDateTime.now().toString();
    }

    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(200, "success", data, null);
    }

    public static <T> ApiResponse<T> success(String message, T data) {
        return new ApiResponse<>(200, message, data, null);
    }

    public static <T> ApiResponse<T> fail(int code, String message) {
        return new ApiResponse<>(code, message, null, null);
    }

    public static <T> ApiResponse<T> fail(int code, String message, Object errors) {
        return new ApiResponse<>(code, message, null, errors);
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public Object getErrors() {
        return errors;
    }

    public void setErrors(Object errors) {
        this.errors = errors;
    }
}
