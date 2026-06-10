package com.bitzh.lvtu.service.impl;

import com.bitzh.lvtu.dto.AdminRegisterRequest;
import com.bitzh.lvtu.dto.LoginRequest;
import com.bitzh.lvtu.dto.RegisterRequest;
import com.bitzh.lvtu.entity.User;
import com.bitzh.lvtu.mapper.UserMapper;
import com.bitzh.lvtu.service.UserService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;
import java.time.LocalDateTime;
import java.util.Set;

@Service
public class UserServiceImpl implements UserService {

    private static final Set<String> ADMIN_ROLES = Set.of(
            "SUPER_ADMIN",
            "CERT_AUDITOR",
            "OPERATOR",
            "CUSTOMER_SERVICE"
    );

    @Resource
    private UserMapper userMapper;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Override
    public User register(RegisterRequest request) {
        // 1. 基础参数校验
        if (request.getUsername() == null || request.getUsername().trim().isEmpty()) {
            throw new IllegalArgumentException("用户名不能为空");
        }
        if (request.getPassword() == null || request.getPassword().trim().isEmpty()) {
            throw new IllegalArgumentException("密码不能为空");
        }

        // 2. 查询用户名是否已存在（若已注销则允许重新注册）
        User existingUser = userMapper.selectByUsername(request.getUsername());
        if (existingUser != null && (existingUser.getStatus() == null || existingUser.getStatus() != 0)) {
            throw new IllegalArgumentException("用户名已被占用");
        }

        // 3. 初始化 User 对象（解决原代码中 user 和 userId 未定义的问题）
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));

        // 4. 处理并校验手机号（解决原代码中变量重复定义和逻辑错位问题）
        String phone = request.getPhone();
        if (phone != null && !phone.trim().isEmpty()) {
            User existingPhone = userMapper.selectByPhone(phone);
            // 新注册用户此时 userId 为 null，若手机号已存在则必定冲突
            if (existingPhone != null && !existingPhone.getUserId().equals(user.getUserId())) {
                throw new IllegalArgumentException("该手机号已被其他用户使用");
            }
            user.setPhone(phone);
        } else {
            user.setPhone(null);
        }

        // 5. 处理其他属性
        String email = request.getEmail();
        user.setEmail((email != null && !email.trim().isEmpty()) ? email : null);
        user.setAvatarUrl(null);
        user.setUserType(1);
        user.setStatus(1);
        user.setIsVerified(false);
        user.setAuthStatus(0);
        user.setRegion(null);
        LocalDateTime now = LocalDateTime.now(); // 获取当前时间
        user.setCreatedTime(now);
        user.setUpdatedTime(now);

        int result = userMapper.insertUser(user);
        if (result != 1) {
            throw new IllegalStateException("创建用户失败");
        }
        return user;
    }

    @Override
    public User adminRegister(AdminRegisterRequest request) {
        if (request.getUsername() == null || request.getUsername().trim().isEmpty()) {
            throw new IllegalArgumentException("用户名不能为空");
        }
        if (request.getPassword() == null || request.getPassword().trim().isEmpty()) {
            throw new IllegalArgumentException("密码不能为空");
        }
        String adminRole = request.getAdminRole();
        if (adminRole == null || adminRole.trim().isEmpty()) {
            adminRole = "OPERATOR";
        }
        adminRole = adminRole.trim().toUpperCase();
        if (!ADMIN_ROLES.contains(adminRole)) {
            throw new IllegalArgumentException("后台角色无效");
        }
        if (userMapper.selectByUsername(request.getUsername()) != null) {
            throw new IllegalArgumentException("用户名已存在");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        String phone = request.getPhone();
        user.setPhone((phone != null && !phone.trim().isEmpty()) ? phone : null);
        String email = request.getEmail();
        user.setEmail((email != null && !email.trim().isEmpty()) ? email : null);
        user.setAvatarUrl(null);
        user.setUserType(3);
        user.setAdminRole(adminRole);
        user.setStatus(1);
        user.setIsVerified(false);
        user.setAuthStatus(0);
        user.setRegion(null);
        LocalDateTime now = LocalDateTime.now(); // 获取当前时间
        user.setCreatedTime(now);
        user.setUpdatedTime(now);

        int result = userMapper.insertUser(user);
        if (result != 1) {
            throw new IllegalStateException("创建用户失败");
        }
        return user;
    }

    @Override
    public User login(LoginRequest request) {
        if (request.getUsername() == null || request.getPassword() == null) {
            throw new IllegalArgumentException("用户名和密码不能为空");
        }
        User user = userMapper.selectByUsername(request.getUsername());
        if (user == null) {
            throw new IllegalArgumentException("用户不存在");
        }
        if (user.getStatus() != null && user.getStatus() == 0) {
            throw new IllegalArgumentException("该账号已注销");
        }
        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new IllegalArgumentException("密码错误");
        }
        return user;
    }

    @Override
    public User adminLogin(LoginRequest request) {
        User user = login(request);
        if (user != null && user.getUserType() != null && user.getUserType() == 3) {
            return user;
        }
        throw new IllegalArgumentException("无管理员权限");
    }

    @Override
    public void resetPassword(RegisterRequest request) {
        if (request.getUsername() == null || request.getPassword() == null) {
            throw new IllegalArgumentException("用户名和新密码不能为空");
        }
        User user = userMapper.selectByUsername(request.getUsername());
        if (user == null) {
            throw new IllegalArgumentException("用户不存在");
        }
        String newHash = passwordEncoder.encode(request.getPassword());
        userMapper.updatePassword(user.getUserId(), newHash);
        userMapper.updateStatus(user.getUserId(), 1);
    }

    @Override
    public User findByUsername(String username) {
        User user = userMapper.selectByUsername(username);
        if (user != null && user.getStatus() != null && user.getStatus() == 0) {
            return null;
        }
        return user;
    }

    @Override
    public User findById(Long userId) {
        User user = userMapper.selectById(userId);
        if (user != null && user.getStatus() != null && user.getStatus() == 0) {
            return null;
        }
        return user;
    }

    @Override
    public void changePassword(String username, String oldPassword, String newPassword) {
        if (username == null || oldPassword == null || newPassword == null) {
            throw new IllegalArgumentException("参数不能为空");
        }
        if (oldPassword.equals(newPassword)) {
            throw new IllegalArgumentException("新密码不能与旧密码一致");
        }
        User user = userMapper.selectByUsername(username);
        if (user == null) {
            throw new IllegalArgumentException("用户不存在");
        }
        if (!passwordEncoder.matches(oldPassword, user.getPasswordHash())) {
            throw new IllegalArgumentException("旧密码错误");
        }
        String newHash = passwordEncoder.encode(newPassword);
        userMapper.updatePassword(user.getUserId(), newHash);
    }

    @Override
    public User updateUserProfile(Long userId, String phone, String email, String region) {
        if (userId == null) {
            throw new IllegalArgumentException("用户ID不能为空");
        }
        
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new IllegalArgumentException("用户不存在");
        }
        
        // 【新增逻辑】清洗数据：将纯空格或空字符串转换为 null
        if (phone != null && phone.trim().isEmpty()) {
            phone = null;
        }
        if (email != null && email.trim().isEmpty()) {
            email = null;
        }
        if (region != null && region.trim().isEmpty()) {
            region = null;
        }

        // 更新实体对象属性
        user.setPhone(phone);
        user.setEmail(email);
        user.setRegion(region);
        LocalDateTime now = LocalDateTime.now();
        user.setUpdatedTime(now);

        int result = userMapper.updateUser(user);
        if (result != 1) {
            throw new IllegalStateException("更新用户信息失败");
        }
        
        return user;
    }

    @Override
    public void updateUser(User user) {
        if (user == null || user.getUserId() == null) {
            throw new IllegalArgumentException("用户或用户ID不能为空");
        }
        user.setUpdatedTime(LocalDateTime.now());
        int result = userMapper.updateUser(user);
        if (result != 1) {
            throw new IllegalStateException("更新用户信息失败");
        }
    }

    @Override
    public void deactivateUser(Long userId) {
        // 物理删除：直接从数据库中删除该用户记录
        userMapper.deleteUser(userId);
    }
}
