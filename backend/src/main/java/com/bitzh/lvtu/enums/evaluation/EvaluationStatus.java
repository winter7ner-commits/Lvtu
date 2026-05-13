package com.bitzh.lvtu.enums.evaluation;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * 评价状态枚举（匹配 SQL ENUM('normal', 'hidden', 'deleted', 'pending')）
 */
public enum EvaluationStatus {
    NORMAL(""normal""), 
    HIDDEN(""hidden""), 
    DELETED(""deleted""), 
    PENDING(""pending"");

    private final String value;

    EvaluationStatus(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static EvaluationStatus fromValue(String value) {
        for (EvaluationStatus status : values()) {
            if (status.value.equalsIgnoreCase(value)) {
                return status;
            }
        }
        throw new IllegalArgumentException(""未知的评价状态: "" + value);
    }
}
