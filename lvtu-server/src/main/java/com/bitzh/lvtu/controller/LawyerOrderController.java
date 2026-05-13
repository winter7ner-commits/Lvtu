package com.bitzh.lvtu.controller;

import com.bitzh.lvtu.common.ApiResponse;
import com.bitzh.lvtu.dto.request.AcceptOrderRequest;
import com.bitzh.lvtu.dto.request.SubmitServiceResultRequest;
import com.bitzh.lvtu.dto.response.AvailableOrderResponse;
import com.bitzh.lvtu.dto.response.MyOrderResponse;
import com.bitzh.lvtu.dto.response.OrderDetailResponse;
import com.bitzh.lvtu.dto.response.ServiceResultResponse;
import com.bitzh.lvtu.service.LawyerOrderService;
import com.bitzh.lvtu.service.ServiceResultService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Validated
@RequestMapping("/api/lawyer/orders")
public class LawyerOrderController {

    @Resource
    private LawyerOrderService lawyerOrderService;

    @Resource
    private ServiceResultService serviceResultService;

    @GetMapping("/available")
    public ApiResponse<List<AvailableOrderResponse>> listAvailableOrders(@RequestParam(required = false) Integer serviceTypeId) {
        return ApiResponse.success(lawyerOrderService.listAvailableOrders(serviceTypeId));
    }

    @GetMapping("/{orderId}")
    public ApiResponse<OrderDetailResponse> getOrderDetail(@PathVariable @NotNull Long orderId) {
        return ApiResponse.success(lawyerOrderService.getOrderDetail(orderId));
    }

    @PostMapping("/{orderId}/accept")
    public ApiResponse<Void> acceptOrder(@PathVariable @NotNull Long orderId,
                                         @Valid @RequestBody AcceptOrderRequest request) {
        // 临时联调方案：当前直接由前端传 lawyerId，接入登录态后应改为从认证上下文获取。
        lawyerOrderService.acceptOrder(orderId, request.getLawyerId());
        return ApiResponse.success("接单成功", null);
    }

    @GetMapping("/my")
    public ApiResponse<List<MyOrderResponse>> listMyOrders(@RequestParam @NotNull Long lawyerId,
                                                           @RequestParam(required = false) String status) {
        return ApiResponse.success(lawyerOrderService.listMyOrders(lawyerId, status));
    }

    @PostMapping("/{orderId}/result")
    public ApiResponse<Void> submitResult(@PathVariable @NotNull Long orderId,
                                          @Valid @RequestBody SubmitServiceResultRequest request) {
        // 临时联调方案：当前直接由前端传 lawyerId，接入登录态后应改为从认证上下文获取。
        serviceResultService.submitResult(orderId, request);
        return ApiResponse.success("提交服务结果成功", null);
    }

    @GetMapping("/{orderId}/result")
    public ApiResponse<ServiceResultResponse> getResult(@PathVariable @NotNull Long orderId,
                                                        @RequestParam @NotNull Long lawyerId) {
        // 临时联调方案：当前直接由前端传 lawyerId，接入登录态后应改为从认证上下文获取。
        return ApiResponse.success(serviceResultService.getResultByOrderId(orderId, lawyerId));
    }
}
