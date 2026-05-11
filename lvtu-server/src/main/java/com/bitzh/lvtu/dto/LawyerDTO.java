package com.bitzh.lvtu.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Data;

    /**
     * 按现在定下的律师表进行修改
     * 2026/4/28
     * 修改完删除这一段
     */
@Data
public class LawyerDTO {

    private Long lawyerId;
    private Long userId;
    private String realname;
    private String avatar;
    private String phone;
    private String lawFirm;
    private Integer practiceYears;
    private BigDecimal rating;
    private Integer status;
    private String bio;

    private String specialty;
    private List<SpecialtyDTO> specialties; // 擅长领域列表

    private LocalDateTime createTime;

    private String licenseNo;
    private String licenseUrl;

            }