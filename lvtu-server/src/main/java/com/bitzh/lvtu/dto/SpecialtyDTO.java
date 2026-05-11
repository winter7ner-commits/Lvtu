package com.bitzh.lvtu.dto;

/**
 * 按现在定下的律师表进行修改
 * 2026/4/28
 * 修改完删除这一段
 */

public class SpecialtyDTO {

    private Integer id;
    private Integer specialtyId;
    private String name;

    public SpecialtyDTO() {
    }

    public SpecialtyDTO(Integer id, String name) {
        this.id = id;
        this.specialtyId = id;
        this.name = name;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getSpecialtyId() {
        return specialtyId;
    }

    public void setSpecialtyId(Integer specialtyId) {
        this.specialtyId = specialtyId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
