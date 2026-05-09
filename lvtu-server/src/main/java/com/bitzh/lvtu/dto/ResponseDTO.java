
package com.bitzh.lvtu.dto;

import com.bitzh.lvtu.constant.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 统一响应DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResponseDTO&lt;T&gt; {
    private Integer code;
    private String message;
    private T data;
    private LocalDateTime timestamp;

    public static &lt;T&gt; ResponseDTO&lt;T&gt; success() {
        return new ResponseDTO&lt;&gt;(ErrorCode.SUCCESS, "success", null, LocalDateTime.now());
    }

    public static &lt;T&gt; ResponseDTO&lt;T&gt; success(T data) {
        return new ResponseDTO&lt;&gt;(ErrorCode.SUCCESS, "success", data, LocalDateTime.now());
    }

    public static &lt;T&gt; ResponseDTO&lt;T&gt; success(String message, T data) {
        return new ResponseDTO&lt;&gt;(ErrorCode.SUCCESS, message, data, LocalDateTime.now());
    }

    public static &lt;T&gt; ResponseDTO&lt;T&gt; error(String message) {
        return new ResponseDTO&lt;&gt;(ErrorCode.BAD_REQUEST, message, null, LocalDateTime.now());
    }

    public static &lt;T&gt; ResponseDTO&lt;T&gt; error(Integer code, String message) {
        return new ResponseDTO&lt;&gt;(code, message, null, LocalDateTime.now());
    }
}
