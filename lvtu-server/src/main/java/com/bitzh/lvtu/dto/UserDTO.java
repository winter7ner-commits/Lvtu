package com.bitzh.lvtu.dto;

import lombok.Data;

@Data
public class UserDTO {
    private Long id;
    private String username;
    private String name;
    private String email;
    private String avatar;
}