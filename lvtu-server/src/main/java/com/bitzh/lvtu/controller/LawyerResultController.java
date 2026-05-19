package com.bitzh.lvtu.controller;

import com.bitzh.lvtu.common.ApiResponse;
import com.bitzh.lvtu.dto.request.UpdateServiceResultRequest;
import com.bitzh.lvtu.service.ServiceResultService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@Validated
@RequestMapping("/api/lawyer/results")
public class LawyerResultController {

    @Resource
    private ServiceResultService serviceResultService;

    @PutMapping("/{resultId}")
    public ApiResponse<Void> updateResult(@PathVariable @NotNull Long resultId,
                                          @Valid @RequestBody UpdateServiceResultRequest request) {
        serviceResultService.updateResult(resultId, request);
        return ApiResponse.success("修改服务结果成功", null);
    }
}
