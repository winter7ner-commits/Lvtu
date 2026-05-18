package com.bitzh.lvtu.dto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class OriginDTO {

    private Long regionId;
    private String regionName;
    private Long parentId;
    private Integer regionLevel;
    private Integer sortOrder;
    private Integer status;
    private LocalDateTime createdTime;

    private Long value;
    private String label;
    private List<OriginDTO> children = new ArrayList<>();

    public Long getRegionId() {
        return regionId;
    }

    public void setRegionId(Long regionId) {
        this.regionId = regionId;
        this.value = regionId;
    }

    public String getRegionName() {
        return regionName;
    }

    public void setRegionName(String regionName) {
        this.regionName = regionName;
        this.label = regionName;
    }

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    public Integer getRegionLevel() {
        return regionLevel;
    }

    public void setRegionLevel(Integer regionLevel) {
        this.regionLevel = regionLevel;
    }

    public Integer getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(Integer sortOrder) {
        this.sortOrder = sortOrder;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public LocalDateTime getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(LocalDateTime createdTime) {
        this.createdTime = createdTime;
    }

    public Long getValue() {
        return value;
    }

    public void setValue(Long value) {
        this.value = value;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public List<OriginDTO> getChildren() {
        return children;
    }

    public void setChildren(List<OriginDTO> children) {
        this.children = children;
    }
}
