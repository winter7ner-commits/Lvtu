package com.bitzh.lvtu.controller;

import com.bitzh.lvtu.common.ApiResponse;
import com.bitzh.lvtu.dto.LawyerDTO;
import com.bitzh.lvtu.service.LawyerService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

/**
 * 按现在定下的律师表进行修改
 * 2026/4/28
 * 修改完删除这一段
 */

import java.util.List;

@RestController
@RequestMapping("/lawyer")
public class LawyerController {

    @Resource
    private LawyerService service;

    // 查询律师列表（可筛选）
    @GetMapping("/list")
    public List<LawyerDTO> list(@RequestParam(required = false) String specialty) {
        return service.list(specialty);
    }

    /**
     * 获取评分最高的律师列表（热门推荐）
     * @param limit 返回的律师数量，默认3个
     * @return 按评分从高到低排序的律师列表
     */
    @GetMapping("/top-rated")
    public ApiResponse<List<LawyerDTO>> getTopRatedLawyers(
            @RequestParam(defaultValue = "3") Integer limit) {
        List<LawyerDTO> lawyers = service.getTopRatedLawyers(limit);
        return ApiResponse.success(lawyers);
    }

    // 待审核列表（管理员）
    @GetMapping("/audit/list")
    public List<LawyerDTO> pending() {
        return service.pendingList();
    }

    // 审核通过
    @PostMapping("/audit/pass")
    public String pass(@RequestParam Long lawyerId) {
        service.approve(lawyerId);
        return "审核通过";
    }

    // 审核拒绝
    @PostMapping("/audit/reject")
    public String reject(@RequestParam Long lawyerId,
                         @RequestParam String remark) {
        service.reject(lawyerId, remark);
        return "已拒绝";
    }
}