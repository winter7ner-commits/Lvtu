package com.bitzh.lvtu.mapper;

import com.bitzh.lvtu.entity.User;
import com.bitzh.lvtu.dto.UserVerificationDTO;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface UserMapper {

    User selectByUsername(@Param("username") String username);

    User selectByEmail(@Param("email") String email);

    User selectByPhone(@Param("phone") String phone);

    User selectByLoginAccount(@Param("account") String account);

    User selectById(@Param("userId") Long userId);

    UserVerificationDTO selectVerificationByUserId(@Param("userId") Long userId);

    List<UserVerificationDTO> selectAllVerifications();

    int upsertVerification(@Param("userId") Long userId,
                           @Param("realName") String realName,
                           @Param("idCardNumber") String idCardNumber,
                           @Param("idCardFrontUrl") String idCardFrontUrl,
                           @Param("idCardBackUrl") String idCardBackUrl);

    int approveVerification(@Param("verificationId") Long verificationId, @Param("reviewerId") Long reviewerId);

    int rejectVerification(@Param("verificationId") Long verificationId,
                           @Param("reviewerId") Long reviewerId,
                           @Param("rejectReason") String rejectReason);

    UserVerificationDTO selectVerificationById(@Param("verificationId") Long verificationId);

    int insertUser(User user);

    int updatePassword(@Param("userId") Long userId, @Param("passwordHash") String passwordHash);

    int updateStatus(@Param("userId") Long userId, @Param("status") Integer status);

    int updateCancelInfo(@Param("userId") Long userId,
                         @Param("status") Integer status,
                         @Param("cancelRequestedAt") LocalDateTime cancelRequestedAt,
                         @Param("cancelEffectiveAt") LocalDateTime cancelEffectiveAt,
                         @Param("cancelCoolingDays") Integer cancelCoolingDays);

    int clearCancelInfo(@Param("userId") Long userId);

    int updateVerified(@Param("userId") Long userId, @Param("isVerified") Boolean isVerified);

    int updateUser(User user);

    // 以下方法对应 UserMapper.xml 中已实现，但原接口遗漏的 SQL 操作
    
    int updateAuthStatus(@Param("userId") Long userId, @Param("authStatus") Integer authStatus);

    int updateUserType(@Param("userId") Long userId, @Param("userType") Integer userType);

    int updateUserProfile(@Param("userId") Long userId, @Param("phone") String phone, 
                          @Param("email") String email, @Param("region") String region);

    int deleteUser(@Param("userId") Long userId);
}
