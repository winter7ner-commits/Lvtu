package com.bitzh.lvtu.service.impl;

import com.bitzh.lvtu.dto.AdminRegisterRequest;
import com.bitzh.lvtu.dto.LoginRequest;
import com.bitzh.lvtu.dto.RegisterRequest;
import com.bitzh.lvtu.dto.UserVerificationDTO;
import com.bitzh.lvtu.entity.User;
import com.bitzh.lvtu.mapper.UserMapper;
import com.bitzh.lvtu.service.SystemConfigService;
import com.bitzh.lvtu.service.UserService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

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

    @Resource
    private SystemConfigService systemConfigService;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    private static final Pattern USERNAME_PATTERN = Pattern.compile("^[A-Za-z0-9_-]{3,6}$");
    private static final Pattern PASSWORD_PATTERN = Pattern.compile("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{6,16}$");
    private static final Pattern PHONE_PATTERN = Pattern.compile("^1[3-9]\\d{9}$");
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$");
    private static final Pattern ID_CARD_PATTERN = Pattern.compile("^\\d{17}[\\dXx]$");

    private static final int STATUS_FROZEN = 0;
    private static final int STATUS_NORMAL = 1;
    private static final int STATUS_BANNED = 2;
    private static final int STATUS_CANCEL_PENDING = 3;
    private static final int STATUS_CANCELLED = 4;

    @Override
    public User register(RegisterRequest request) {
        String username = normalize(request.getUsername());
        String password = request.getPassword();
        String phone = normalize(request.getPhone());
        String email = normalize(request.getEmail());

        validateUsername(username);
        validatePassword(password);
        validatePhone(phone);
        if (email != null && !EMAIL_PATTERN.matcher(email).matches()) {
            throw new IllegalArgumentException("邮箱格式不正确");
        }

        User existingUser = userMapper.selectByUsername(username);
        if (existingUser != null) {
            throw new IllegalArgumentException("用户名已被占用");
        }

        User existingPhone = userMapper.selectByPhone(phone);
        if (existingPhone != null) {
            throw new IllegalArgumentException("该手机号已被注册");
        }

        User user = new User();
        user.setUsername(username);
        user.setPasswordHash(passwordEncoder.encode(password));
        user.setPhone(phone);
        user.setEmail(email);
        user.setAvatarUrl(null);
        user.setUserType(1);
        user.setStatus(STATUS_NORMAL);
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
        User user = userMapper.selectByLoginAccount(request.getUsername().trim());
        if (user == null) {
            throw new IllegalArgumentException("用户不存在");
        }
        Integer status = user.getStatus();
        if (status != null && status == STATUS_CANCELLED) {
            throw new IllegalArgumentException("该账号已注销，无法登录");
        }
        if (status != null && status == STATUS_BANNED) {
            throw new IllegalArgumentException("该账号已被平台封禁，请联系客服");
        }
        if (status != null && status == STATUS_FROZEN) {
            throw new IllegalArgumentException("该账号已被冻结，请联系客服");
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
        validatePassword(request.getPassword());
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
        if (user != null && isUnavailableAccount(user.getStatus())) {
            return null;
        }
        return user;
    }

    @Override
    public User findById(Long userId) {
        User user = userMapper.selectById(userId);
        if (user != null && isUnavailableAccount(user.getStatus())) {
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
        validatePassword(newPassword);
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
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new IllegalArgumentException("用户不存在");
        }
        if (user.getStatus() != null && user.getStatus() == STATUS_CANCELLED) {
            throw new IllegalArgumentException("账号已注销");
        }
        int days = systemConfigService.getAccountCancelCoolingDays();
        LocalDateTime now = LocalDateTime.now();
        userMapper.updateCancelInfo(userId, STATUS_CANCEL_PENDING, now, now.plusDays(days), days);
    }

    @Override
    public void cancelDeactivation(Long userId) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new IllegalArgumentException("用户不存在");
        }
        if (user.getStatus() == null || user.getStatus() != STATUS_CANCEL_PENDING) {
            throw new IllegalArgumentException("当前账号不在注销冷静期");
        }
        userMapper.clearCancelInfo(userId);
    }

    @Override
    public UserVerificationDTO getVerification(Long userId) {
        return userMapper.selectVerificationByUserId(userId);
    }

    @Override
    public void submitVerification(Long userId, String realName, String idCardNumber,
                                   String phone, String idCardFrontUrl, String idCardBackUrl) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new IllegalArgumentException("用户不存在");
        }
        if (Boolean.TRUE.equals(user.getIsVerified())) {
            throw new IllegalArgumentException("实名认证已通过，无需重复提交");
        }
        realName = normalize(realName);
        idCardNumber = normalize(idCardNumber);
        phone = normalize(phone);
        idCardFrontUrl = normalize(idCardFrontUrl);
        idCardBackUrl = normalize(idCardBackUrl);
        if (realName == null || realName.length() < 2 || realName.length() > 20) {
            throw new IllegalArgumentException("真实姓名需为2到20个字符");
        }
        if (idCardNumber == null || !ID_CARD_PATTERN.matcher(idCardNumber).matches()) {
            throw new IllegalArgumentException("身份证号格式不正确");
        }
        validatePhone(phone);
        if (!phone.equals(user.getPhone())) {
            throw new IllegalArgumentException("实名认证手机号必须与账号绑定手机号一致");
        }
        if (idCardFrontUrl == null || idCardBackUrl == null) {
            throw new IllegalArgumentException("请上传身份证正反面照片");
        }
        userMapper.upsertVerification(userId, realName, idCardNumber.toUpperCase(), idCardFrontUrl, idCardBackUrl);
        userMapper.updateVerified(userId, false);
    }

    @Override
    public List<UserVerificationDTO> listVerifications() {
        return userMapper.selectAllVerifications();
    }

    @Override
    public void approveVerification(Long verificationId, Long reviewerId) {
        UserVerificationDTO dto = userMapper.selectVerificationById(verificationId);
        if (dto == null) {
            throw new IllegalArgumentException("实名认证申请不存在");
        }
        if (dto.getVerificationStatus() != null && dto.getVerificationStatus() == 1) {
            throw new IllegalArgumentException("该实名认证已通过");
        }
        userMapper.approveVerification(verificationId, reviewerId);
        userMapper.updateVerified(dto.getUserId(), true);
    }

    @Override
    public void rejectVerification(Long verificationId, Long reviewerId, String rejectReason) {
        rejectReason = normalize(rejectReason);
        if (rejectReason == null) {
            throw new IllegalArgumentException("驳回原因不能为空");
        }
        UserVerificationDTO dto = userMapper.selectVerificationById(verificationId);
        if (dto == null) {
            throw new IllegalArgumentException("实名认证申请不存在");
        }
        userMapper.rejectVerification(verificationId, reviewerId, rejectReason);
        userMapper.updateVerified(dto.getUserId(), false);
    }

    private void validateUsername(String username) {
        if (username == null || !USERNAME_PATTERN.matcher(username).matches()) {
            throw new IllegalArgumentException("用户名需为3到6个字符，只能包含字母、数字、下划线和连字符");
        }
    }

    private void validatePassword(String password) {
        if (password == null || !PASSWORD_PATTERN.matcher(password).matches()) {
            throw new IllegalArgumentException("密码需为6到16个字符，且同时包含数字、小写字母和大写字母");
        }
    }

    private void validatePhone(String phone) {
        if (phone == null || !PHONE_PATTERN.matcher(phone).matches()) {
            throw new IllegalArgumentException("手机号需为11位，且符合1[3-9]开头");
        }
    }

    private String normalize(String value) {
        if (value == null) return null;
        String text = value.trim();
        return text.isEmpty() ? null : text;
    }

    private boolean isUnavailableAccount(Integer status) {
        return status != null && (status == STATUS_CANCELLED || status == STATUS_BANNED || status == STATUS_FROZEN);
    }
}
