
package com.bitzh.lvtu.dto;

import java.time.LocalDateTime;
import lombok.Data;

@Data
public class ApplicationDTO {
    private Long applicationId;
    private Long userId;
    private String licenseNo;
    private String licenseUrl;
    private String lawFirm;
    private Integer practiceStartYear;
    private Integer status;          // 0待审核 1通过 2驳回
    private String rejectReason;
    private LocalDateTime createTime;
    private LocalDateTime auditTime;

    // 关联用户信息（方便管理员审核页显示）
    private String username;         // 申请人用户名
    private String realname;         // 申请人真实姓名（如果有）
    private Integer applyType;
    private String avatarUrl;

    }