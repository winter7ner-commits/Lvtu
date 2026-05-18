package com.bitzh.lvtu.service;

import com.bitzh.lvtu.dto.request.SubmitServiceResultRequest;
import com.bitzh.lvtu.dto.request.UpdateServiceResultRequest;
import com.bitzh.lvtu.dto.response.ServiceResultResponse;

public interface ServiceResultService {

    void submitResult(Long orderId, SubmitServiceResultRequest request);

    ServiceResultResponse getResultByOrderId(Long orderId, Long lawyerId);

    ServiceResultResponse getResultByOrderIdForUser(Long orderId, Long userId);

    void updateResult(Long resultId, UpdateServiceResultRequest request);
}
