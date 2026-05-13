package com.bitzh.lvtu.enums.evaluation;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * 举报原因枚举（匹配 SQL ENUM('abuse', 'false', 'ad', 'privacy')）
 */
public enum ReportReason {
    ABUSE(""abuse""), 
    FALSE(""false""), 
    AD(""ad""), 
    PRIVACY(""privacy"");

    private final String value;

    ReportReason(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static ReportReason fromValue(String value) {
        for (ReportReason reason : values()) {
            if (reason.value.equalsIgnoreCase(value)) {
                return reason;
            }
        }
        throw new IllegalArgumentException(""未知的举报原因: "" + value);
    }
}
