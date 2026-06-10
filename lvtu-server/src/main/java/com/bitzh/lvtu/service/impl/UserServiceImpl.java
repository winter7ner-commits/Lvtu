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
import java.util.UUID;

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

    // ... existing code ...

    @Override
    public User register(RegisterRequest request) {
        // 1. 基础参数校验
        if (request.getUsername() == null || request.getUsername().trim().isEmpty()) {
            throw new IllegalArgumentException("用户名不能为空");
        }
        if (request.getPassword() == null || request.getPassword().trim().isEmpty()) {
            throw new IllegalArgumentException("密码不能为空");
        }

        // 2. 查询用户名是否已存在（注销时用户名已释放，不会命中已注销用户）
        User existingUser = userMapper.selectByUsername(request.getUsername());
        if (existingUser != null) {
            throw new IllegalArgumentException("用户名已被占用");
        }

        // 3. 初始化 User 对象
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));

        // 4. 处理并校验手机号
        String phone = request.getPhone();
        if (phone != null && !phone.trim().isEmpty()) {
            User existingPhone = userMapper.selectByPhone(phone);
            if (existingPhone != null && (existingPhone.getStatus() == null || existingPhone.getStatus() != 0)) {
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
        LocalDateTime now = LocalDateTime.now();
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
        User existingUser = userMapper.selectByUsername(request.getUsername());
        if (existingUser != null) {
            throw new IllegalArgumentException("用户名已存在");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        
        // 处理手机号（允许使用已注销用户的手机号）
        String phone = request.getPhone();
        if (phone != null && !phone.trim().isEmpty()) {
            User existingPhone = userMapper.selectByPhone(phone);
            if (existingPhone != null && (existingPhone.getStatus() == null || existingPhone.getStatus() != 0)) {
                throw new IllegalArgumentException("该手机号已被其他用户使用");
            }
            user.setPhone(phone);
        } else {
            user.setPhone(null);
        }
        
        String email = request.getEmail();
        user.setEmail((email != null && !email.trim().isEmpty()) ? email : null);
        user.setAvatarUrl(null);
        user.setUserType(3);
        user.setAdminRole(adminRole);
        user.setStatus(1);
        user.setIsVerified(false);
        user.setAuthStatus(0);
        user.setRegion(null);
        LocalDateTime now = LocalDateTime.now();
        user.setCreatedTime(now);
        user.setUpdatedTime(now);

        int result = userMapper.insertUser(user);
        if (result != 1) {
            throw new IllegalStateException("创建用户失败");
        }
        return user;
    }

// ... existing code ...

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
        if (user.getStatus() != null && user.getStatus() == 0) {
            // 已注销用户恢复时，生成新的随机用户名，恢复手机号
            String newUsername = generateUniqueRandomUsername();
            user.setUsername(newUsername);
            user.setPasswordHash(newHash);
            user.setPhone(user.getDeactivatedPhone());
            user.setDeactivatedPhone(null);
            user.setStatus(1);
            user.setUpdatedTime(LocalDateTime.now());
            userMapper.updateUser(user);
        } else {
            userMapper.updatePassword(user.getUserId(), newHash);
        }
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
    public User updateUserProfile(Long userId, String username, String phone, String email, String region) {
        if (userId == null) {
            throw new IllegalArgumentException("用户ID不能为空");
        }
        
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new IllegalArgumentException("用户不存在");
        }
        
        // 验证用户名唯一性（如果提供了新用户名且与旧用户名不同）
        if (username != null && !username.trim().isEmpty()) {
            username = username.trim();
            if (!username.equals(user.getUsername())) {
                User existingUser = userMapper.selectByUsername(username);
                if (existingUser != null) {
                    throw new IllegalArgumentException("用户名已被占用");
                }
            }
            user.setUsername(username);
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
        if (userId == null) {
            throw new IllegalArgumentException("用户ID不能为空");
        }
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new IllegalArgumentException("用户不存在");
        }
        // 释放用户名和手机号：注销后原用户名和手机号可供新用户注册使用
        // 手机号暂存到 deactivated_phone，用于恢复账号时验证身份
        String deactivatedUsername = "_deactivated_" + System.currentTimeMillis();
        user.setDeactivatedPhone(user.getPhone());
        user.setUsername(deactivatedUsername);
        user.setPhone(null);
        user.setStatus(0);
        user.setUpdatedTime(LocalDateTime.now());
        userMapper.updateUser(user);
    }

    @Override
    public User restoreUser(Long userId) {
        if (userId == null) {
            throw new IllegalArgumentException("用户ID不能为空");
        }
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new IllegalArgumentException("用户不存在");
        }
        if (user.getStatus() == null || user.getStatus() != 0) {
            throw new IllegalArgumentException("该账户不是已注销状态，无法恢复");
        }

        String newUsername = generateUniqueRandomUsername();
        user.setUsername(newUsername);
        // 恢复手机号：从 deactivatedPhone 取回
        user.setPhone(user.getDeactivatedPhone());
        user.setDeactivatedPhone(null);
        user.setStatus(1);
        user.setUpdatedTime(LocalDateTime.now());
        userMapper.updateUser(user);
        return user;
    }

    @Override
    public java.util.List<User> findDeactivatedUsersByPhone(String phone) {
        if (phone == null || phone.trim().isEmpty()) {
            throw new IllegalArgumentException("手机号不能为空");
        }
        return userMapper.selectDeactivatedUsersByPhone(phone);
    }

    @Override
    public User findActiveUserByPhone(String phone) {
        if (phone == null || phone.trim().isEmpty()) {
            throw new IllegalArgumentException("手机号不能为空");
        }
        return userMapper.selectActiveUserByPhone(phone);
    }

    @Override
    public User selfRestoreAccount(Long userId, boolean confirmPhoneTransfer) {
        if (userId == null) {
            throw new IllegalArgumentException("用户ID不能为空");
        }

        // 1. 查找要恢复的已注销账号
        User deactivatedUser = userMapper.selectById(userId);
        if (deactivatedUser == null) {
            throw new IllegalArgumentException("用户不存在");
        }
        if (deactivatedUser.getStatus() == null || deactivatedUser.getStatus() != 0) {
            throw new IllegalArgumentException("该账号不是已注销状态，无法恢复");
        }

        // 从 deactivatedPhone 取回原手机号
        String phone = deactivatedUser.getDeactivatedPhone();

        // 2. 检查手机号冲突
        if (phone != null && !phone.trim().isEmpty()) {
            User activeUser = userMapper.selectActiveUserByPhone(phone);
            if (activeUser != null) {
                // 手机号已被其他活跃账号绑定
                if (!confirmPhoneTransfer) {
                    throw new IllegalArgumentException("手机号冲突：该手机号当前已绑定至活跃账号（用户名：" + activeUser.getUsername() + "），恢复此账号将把手机号转移至该账号。请确认是否继续。");
                }
                // 用户已确认，解绑活跃账号的手机号
                userMapper.unbindPhone(activeUser.getUserId());
            }
        }

        // 3. 恢复账号：生成新的随机用户名，绑定手机号，清空deactivatedPhone，激活状态
        String newUsername = generateUniqueRandomUsername();
        deactivatedUser.setUsername(newUsername);
        deactivatedUser.setPhone(phone);
        deactivatedUser.setDeactivatedPhone(null);
        deactivatedUser.setStatus(1);
        deactivatedUser.setUpdatedTime(LocalDateTime.now());
        userMapper.updateUser(deactivatedUser);

        return deactivatedUser;
    }

    private String generateUniqueRandomUsername() {
        String username;
        int attempts = 0;
        do {
            username = "user_" + UUID.randomUUID().toString().replace("-", "").substring(0, 8);
            attempts++;
            if (attempts > 10) {
                username = "user_" + System.currentTimeMillis();
            }
        } while (userMapper.selectByUsername(username) != null);
        return username;
    }
}
