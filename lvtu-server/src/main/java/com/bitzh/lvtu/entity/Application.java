package com.bitzh.lvtu.entity;


import java.time.LocalDateTime;
import lombok.Data;

@Data
public class Application {
    private Long applicationId;
    private Long userId;
    private String licenseNo;
    private String licenseUrl;
    private String lawFirm;

    private Integer practiceStartYear;
    private Integer applyType; // 0-首次入驻, 1-资料变更
    private Integer status;    // 0待审核, 1通过, 2驳回
    private LocalDateTime auditTime;
    private String rejectReason;
    private LocalDateTime createTime;
}