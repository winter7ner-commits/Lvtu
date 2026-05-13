package com.bitzh.lvtu.controller;

import com.bitzh.lvtu.common.ApiResponse;
import com.bitzh.lvtu.dto.request.ConfirmOrderRequest;
import com.bitzh.lvtu.dto.response.SettlementResponse;
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

    @PostMapping("/orders/{orderId}/confirm")
    public ApiResponse<Void> confirmOrder(@PathVariable @NotNull Long orderId,
                                          @Valid @RequestBody ConfirmOrderRequest request) {
        settlementService.confirmOrder(orderId, request.getUserId());
        return ApiResponse.success("订单确认完成成功", null);
    }

    @GetMapping("/settlements/pending")
    public ApiResponse<List<SettlementResponse>> listPendingSettlements() {
        return ApiResponse.success(settlementService.listPendingSettlements());
    }

    @PutMapping("/settlements/{settlementId}/pay")
    public ApiResponse<Void> paySettlement(@PathVariable @NotNull Long settlementId) {
        settlementService.paySettlement(settlementId);
        return ApiResponse.success("结算成功", null);
    }
}
