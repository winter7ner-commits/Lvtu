package com.bitzh.lvtu.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
    private Long userId;
    private String username;
    private String email;
    private Integer userType;
    private String avatarUrl;

    public static UserDTO from(com.bitzh.lvtu.entity.User user) {
        if (user == null) return null;
        UserDTO dto = new UserDTO();
        dto.setUserId(user.getUserId());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setUserType(user.getUserType());
        dto.setAvatarUrl(user.getAvatarUrl());
        return dto;
    }
}