package com.bitzh.lvtu.entity;

public class LawyerProfile {

    private Long lawyerId;
    private Long userId;
    private String licenseNo;
    private String lawFirm;
    private String bio;
    private Integer status;
    private Integer practiceYears;

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

    public String getLicenseNo() {
        return licenseNo;
    }

    public void setLicenseNo(String licenseNo) {
        this.licenseNo = licenseNo;
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

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getPracticeYears() {
        return practiceYears;
    }

    public void setPracticeYears(Integer practiceYears) {
        this.practiceYears = practiceYears;
    }
}
