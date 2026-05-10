package com.bitzh.lvtu.entity;

import java.time.LocalDateTime;

/**
 * 按现在定下的律师表进行修改
 * 2026/4/28
 * 修改完删除这一段
 */

public class Lawyer {

    private Long lawyerId;
    private String name;
    private String phone;
    private String lawFirm;
    private Integer practiceYears;
    private Double rating;

    private Integer status;       // 1正常 0禁用
    private Integer auditStatus;  // 0待审核 1通过 2拒绝
    private String auditRemark;

    private String description;

    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    // getter / setter

}