package com.bitzh.lvtu.service;

import com.bitzh.lvtu.dto.request.SubmitServiceResultRequest;
import com.bitzh.lvtu.dto.request.UpdateServiceResultRequest;
import com.bitzh.lvtu.dto.response.ServiceResultResponse;

public interface ServiceResultService {

    void submitResult(Long orderId, SubmitServiceResultRequest request);

    ServiceResultResponse getResultByOrderId(Long orderId, Long lawyerId);

    void updateResult(Long resultId, UpdateServiceResultRequest request);
}
