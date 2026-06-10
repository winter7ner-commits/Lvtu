package com.bitzh.lvtu.controller;

import com.bitzh.lvtu.common.ApiResponse;
import com.bitzh.lvtu.dto.request.ConfirmOrderRequest;
import com.bitzh.lvtu.dto.request.RequestRevisionRequest;
import com.bitzh.lvtu.dto.response.ServiceResultResponse;
import com.bitzh.lvtu.dto.response.SettlementResponse;
import com.bitzh.lvtu.service.AdminPermissionService;
import com.bitzh.lvtu.service.ServiceResultService;
import com.bitzh.lvtu.service.SettlementService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Validated
@RequestMapping("/api")
public class SettlementController {

    @Resource
    private SettlementService settlementService;

    @Resource
    private ServiceResultService serviceResultService;

    @Resource
    private AdminPermissionService adminPermissionService;

    @PostMapping("/orders/{orderId}/confirm")
    public ApiResponse<Void> confirmOrder(@PathVariable @NotNull Long orderId,
                                          @Valid @RequestBody ConfirmOrderRequest request) {
        settlementService.confirmOrder(orderId, request.getUserId());
        return ApiResponse.success("订单确认完成成功", null);
    }

    @PostMapping("/orders/{orderId}/revision")
    public ApiResponse<ServiceResultResponse> requestRevision(@PathVariable @NotNull Long orderId,
                                                              @Valid @RequestBody RequestRevisionRequest request) {
        return ApiResponse.success("修改申请已提交", serviceResultService.requestRevision(orderId, request));
    }

    @GetMapping("/orders/{orderId}/result")
    public ApiResponse<ServiceResultResponse> getOrderResultForUser(@PathVariable @NotNull Long orderId,
                                                                    @RequestParam @NotNull Long userId) {
        return ApiResponse.success(serviceResultService.getResultByOrderIdForUser(orderId, userId));
    }

    @GetMapping("/settlements/pending")
    public ApiResponse<List<SettlementResponse>> listPendingSettlements(@RequestHeader(value = "Authorization", required = false) String authorization) {
        adminPermissionService.requireAdmin(authorization, "SUPER_ADMIN");
        return ApiResponse.success(settlementService.listPendingSettlements());
    }

    @PutMapping("/settlements/{settlementId}/pay")
    public ApiResponse<Void> paySettlement(@RequestHeader(value = "Authorization", required = false) String authorization,
                                           @PathVariable @NotNull Long settlementId) {
        adminPermissionService.requireAdmin(authorization, "SUPER_ADMIN");
        settlementService.paySettlement(settlementId);
        return ApiResponse.success("结算成功", null);
    }
}
