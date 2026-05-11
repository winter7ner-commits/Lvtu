package com.bitzh.lvtu.entity;

import java.time.LocalDateTime;
import lombok.Data;
/**
 * 按现在定下的律师表进行修改
 * 2026/4/28
 * 修改完删除这一段
 */




public class Lawyer {

    private Long lawyerId;
    private Long userId;
    private String lawFirm;
    private String bio;

    public Long getLawyerId() {
        return lawyerId;
    }

    public void setLawyerId(Long lawyerId) {
        this.lawyerId = lawyerId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getLawFirm() {
        return lawFirm;
    }

    public void setLawFirm(String lawFirm) {
        this.lawFirm = lawFirm;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public Integer getPracticeYears() {
        return practiceYears;
    }

    public void setPracticeYears(Integer practiceYears) {
        this.practiceYears = practiceYears;
    }

    public Double getRating() {
        return rating;
    }

    public void setRating(Double rating) {
        this.rating = rating;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getLicenseNo() {
        return licenseNo;
    }

    public void setLicenseNo(String licenseNo) {
        this.licenseNo = licenseNo;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    private Integer practiceYears;
    private Double rating;

    private Integer status;       // 1正常 0禁用

    private String licenseNo;
    private LocalDateTime createTime;


    // getter / setter

}