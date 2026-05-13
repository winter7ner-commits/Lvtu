package com.bitzh.lvtu.controller;

import com.bitzh.lvtu.common.ApiResponse;
import com.bitzh.lvtu.dto.UserDTO;
import com.bitzh.lvtu.dto.UserVerificationDTO;
import com.bitzh.lvtu.entity.User;
import com.bitzh.lvtu.mapper.UserMapper;
import com.bitzh.lvtu.service.UserService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
@CrossOrigin(origins = "*")
public class UserController {

    @Resource
    private UserService userService;

    @Resource
    private UserMapper userMapper;

    @GetMapping("/info")
    public ApiResponse<UserDTO> info(@RequestParam Long userId) {
        User user = userService.findById(userId);
        return ApiResponse.success(UserDTO.from(user));
    }

    @GetMapping("/verification")
    public ApiResponse<UserVerificationDTO> verification(@RequestParam Long userId) {
        return ApiResponse.success(userMapper.selectVerificationByUserId(userId));
    }
}
