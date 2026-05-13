package com.bitzh.lvtu.controller;

import com.bitzh.lvtu.dto.response.AvailableOrderResponse;
import com.bitzh.lvtu.exception.GlobalExceptionHandler;
import com.bitzh.lvtu.service.LawyerOrderService;
import com.bitzh.lvtu.service.ServiceResultService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.MediaType;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class LawyerOrderControllerTest {

    private MockMvc mockMvc;
    private LawyerOrderService lawyerOrderService;

    @BeforeEach
    void setUp() {
        lawyerOrderService = Mockito.mock(LawyerOrderService.class);
        ServiceResultService serviceResultService = Mockito.mock(ServiceResultService.class);

        LawyerOrderController controller = new LawyerOrderController();
        ReflectionTestUtils.setField(controller, "lawyerOrderService", lawyerOrderService);
        ReflectionTestUtils.setField(controller, "serviceResultService", serviceResultService);

        LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
        validator.afterPropertiesSet();

        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .setValidator(validator)
                .build();
    }

    @Test
    void shouldReturnUnifiedApiResponseForAvailableOrders() throws Exception {
        AvailableOrderResponse response = new AvailableOrderResponse();
        response.setOrderId(800001L);
        response.setUserId(500001L);
        response.setServiceTypeId(105);
        response.setTotalAmount(new BigDecimal("199.00"));
        response.setStatus("待接单");
        response.setCreatedTime(LocalDateTime.of(2026, 5, 1, 10, 0));

        Mockito.when(lawyerOrderService.listAvailableOrders(null)).thenReturn(List.of(response));

        mockMvc.perform(get("/api/lawyer/orders/available"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("success"))
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.data[0].orderId").value(800001));
    }

    @Test
    void shouldHandleValidationFailureWithUnifiedResponse() throws Exception {
        mockMvc.perform(post("/api/lawyer/orders/800001/accept")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.message").value("参数验证失败"))
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.errors[0].field").value("lawyerId"));
    }
}
