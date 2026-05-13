package com.bitzh.lvtu.controller;

import com.bitzh.lvtu.dto.response.SettlementResponse;
import com.bitzh.lvtu.exception.GlobalExceptionHandler;
import com.bitzh.lvtu.service.SettlementService;
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

class SettlementControllerTest {

    private MockMvc mockMvc;
    private SettlementService settlementService;

    @BeforeEach
    void setUp() {
        settlementService = Mockito.mock(SettlementService.class);
        SettlementController controller = new SettlementController();
        ReflectionTestUtils.setField(controller, "settlementService", settlementService);

        LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
        validator.afterPropertiesSet();

        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .setValidator(validator)
                .build();
    }

    @Test
    void shouldReturnPendingSettlementsWithUnifiedResponse() throws Exception {
        SettlementResponse response = new SettlementResponse();
        response.setId(920001L);
        response.setOrderId(800005L);
        response.setLawyerId(700001L);
        response.setAmount(new BigDecimal("129.00"));
        response.setStatus(0);
        response.setCreatedAt(LocalDateTime.of(2026, 4, 29, 16, 15));

        Mockito.when(settlementService.listPendingSettlements()).thenReturn(List.of(response));

        mockMvc.perform(get("/api/settlements/pending"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("success"))
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.data[0].orderId").value(800005));
    }

    @Test
    void shouldHandleValidationFailureForConfirmOrder() throws Exception {
        mockMvc.perform(post("/api/orders/800004/confirm")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.message").value("参数验证失败"))
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.errors[0].field").value("userId"));
    }
}
