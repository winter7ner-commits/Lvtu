package com.bitzh.lvtu.enums.evaluation;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * 管理员操作类型枚举（匹配 SQL ENUM 值）
 */
public enum ActionType {
    HIDE(""hide""), 
    DELETE(""delete""), 
    RESTORE(""restore""), 
    FEATURE(""feature""), 
    UNFEATURE(""unfeature""), 
    REJECT_REPORT(""reject_report"");

    private final String value;

    ActionType(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static ActionType fromValue(String value) {
        for (ActionType type : values()) {
            if (type.value.equalsIgnoreCase(value)) {
                return type;
            }
        }
        throw new IllegalArgumentException(""未知的操作类型: "" + value);
    }
}
