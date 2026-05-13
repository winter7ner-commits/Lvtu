
package com.bitzh.lvtu.mapper;

import com.bitzh.lvtu.dto.ApplicationDTO;
import com.bitzh.lvtu.entity.Application;
import org.apache.ibatis.annotations.Param;
import java.util.List;

public interface ApplicationMapper {

    // 提交申请
    int insert(Application application);
    ApplicationDTO selectByApplicationId(@Param("applicationId") Long applicationId);
    // 查询某用户的最新申请状态（用户查自己的）
    ApplicationDTO selectByUserId(@Param("userId") Long userId);

    // 查询所有待审核申请（管理员用）
    List<ApplicationDTO> selectPendingList();
    List<ApplicationDTO> selectAll();
    // 审核通过
    int approveApplication(@Param("applicationId") Long applicationId);

    // 审核驳回
    int rejectApplication(@Param("applicationId") Long applicationId,
                          @Param("rejectReason") String rejectReason);
}