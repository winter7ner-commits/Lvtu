package com.bitzh.lvtu.enums.evaluation;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * 举报状态枚举（匹配 SQL ENUM('pending', 'approved', 'rejected')）
 */
public enum ReportStatus {
    PENDING(""pending""), 
    APPROVED(""approved""), 
    REJECTED(""rejected"");

    private final String value;

    ReportStatus(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static ReportStatus fromValue(String value) {
        for (ReportStatus status : values()) {
            if (status.value.equalsIgnoreCase(value)) {
                return status;
            }
        }
        throw new IllegalArgumentException(""未知的举报状态: "" + value);
    }
}
