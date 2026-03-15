package com.pricing_engine.demo.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pricing_engine.demo.model.RampSegment;
import com.pricing_engine.demo.service.BillingService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BillingRestController.class)
@DisplayName("BillingRestController Slice Tests")
class BillingRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private BillingService billingService;

    @Test
    @DisplayName("POST /calculate with single valid segment returns 200 with TCV")
    void calculate_singleValidSegment_returns200WithTCV() throws Exception {
        List<RampSegment> segments = List.of(
                RampSegment.builder()
                        .monthlyPrice(new BigDecimal("1000.00"))
                        .durationMonths(12)
                        .build()
        );
        when(billingService.calculateTotalContractValue(anyList()))
                .thenReturn(new BigDecimal("12000.00"));

        mockMvc.perform(post("/api/v1/billing/calculate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(segments)))
                .andExpect(status().isOk())
                .andExpect(content().string("12000.00"));
    }

    @Test
    @DisplayName("POST /calculate with multiple segments returns 200 with summed TCV")
    void calculate_multipleSegments_returns200WithSummedTCV() throws Exception {
        List<RampSegment> segments = List.of(
                RampSegment.builder().monthlyPrice(new BigDecimal("500.00")).durationMonths(12).build(),
                RampSegment.builder().monthlyPrice(new BigDecimal("800.00")).durationMonths(12).build()
        );
        when(billingService.calculateTotalContractValue(anyList()))
                .thenReturn(new BigDecimal("15600.00"));

        mockMvc.perform(post("/api/v1/billing/calculate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(segments)))
                .andExpect(status().isOk())
                .andExpect(content().string("15600.00"));
    }

    @Test
    @DisplayName("POST /calculate with empty list returns 200 with 0")
    void calculate_emptyList_returns200WithZero() throws Exception {
        when(billingService.calculateTotalContractValue(anyList()))
                .thenReturn(BigDecimal.ZERO);

        mockMvc.perform(post("/api/v1/billing/calculate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("[]"))
                .andExpect(status().isOk())
                .andExpect(content().string("0"));
    }

    @Test
    @DisplayName("POST /calculate delegates to service exactly once")
    void calculate_validRequest_delegatesToServiceExactlyOnce() throws Exception {
        List<RampSegment> segments = List.of(
                RampSegment.builder().monthlyPrice(new BigDecimal("100.00")).durationMonths(6).build()
        );
        when(billingService.calculateTotalContractValue(anyList()))
                .thenReturn(new BigDecimal("600.00"));

        mockMvc.perform(post("/api/v1/billing/calculate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(segments)))
                .andExpect(status().isOk());

        verify(billingService, times(1)).calculateTotalContractValue(anyList());
    }

    @Test
    @DisplayName("POST /calculate with missing body returns 400")
    void calculate_missingBody_returns400() throws Exception {
        mockMvc.perform(post("/api/v1/billing/calculate")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(billingService, never()).calculateTotalContractValue(anyList());
    }

    @Test
    @DisplayName("POST /calculate with malformed JSON returns 400")
    void calculate_malformedJson_returns400() throws Exception {
        mockMvc.perform(post("/api/v1/billing/calculate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ this is not valid json }"))
                .andExpect(status().isBadRequest());

        verify(billingService, never()).calculateTotalContractValue(anyList());
    }

    @Test
    @DisplayName("POST /calculate without Content-Type returns 415")
    void calculate_missingContentType_returns415() throws Exception {
        mockMvc.perform(post("/api/v1/billing/calculate")
                        .content("[{\"monthlyPrice\":500,\"durationMonths\":12}]"))
                .andExpect(status().isUnsupportedMediaType());
    }
}