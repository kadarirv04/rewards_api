package com.homework.rewards.api.controller;

import com.homework.rewards.api.dto.RewardsSummaryResponseDto;
import com.homework.rewards.api.dto.ResponseDto;
import com.homework.rewards.api.dto.TransactionResponseDto;
import com.homework.rewards.api.exception.NotFoundException;
import com.homework.rewards.api.service.RewardService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(RewardController.class)
class RewardControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private RewardService rewardService;

    private TransactionResponseDto transactionResponse;

    @BeforeEach
    void setUp() {
        transactionResponse = new TransactionResponseDto();
        transactionResponse.setMessage("Transaction added successfully");
        transactionResponse.setPointsEarned(90);
        transactionResponse.setPointsCalculation("$20 over $100 = 20 × 2 = 40 points, $50-$100 = 50 points, Total = 90 points");
    }

    @Test
    void testGetAllCustomerRewardsSuccess() throws Exception {
        when(rewardService.getAllCustomerRewards()).thenReturn(new RewardsSummaryResponseDto());
        mockMvc.perform(get("/api/rewards"))
                .andExpect(status().isOk());
    }

    @Test
    void testCreateTransactionSuccess() throws Exception {
        when(rewardService.createTransaction(any(Map.class)))
                .thenReturn(transactionResponse);
        Map<String, Object> request = new HashMap<>();
        request.put("customerId", 1L);
        request.put("amount", 120.00);
        request.put("date", "2024-07-01");
        mockMvc.perform(post("/api/transactions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new com.fasterxml.jackson.databind.ObjectMapper().writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Transaction added successfully"))
                .andExpect(jsonPath("$.pointsEarned").value(90));
    }

    @Test
    void testCreateTransactionCustomerNotFound() throws Exception {
        when(rewardService.createTransaction(any(Map.class)))
                .thenThrow(new NotFoundException("Customer not found with ID: 99"));
        Map<String, Object> request = new HashMap<>();
        request.put("customerId", 99L);
        request.put("amount", 120.00);
        request.put("date", "2024-07-01");
        mockMvc.perform(post("/api/transactions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new com.fasterxml.jackson.databind.ObjectMapper().writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Not Found"));
    }

    @Test
    void testCreateTransactionInvalidAmount() throws Exception {
        when(rewardService.createTransaction(any(Map.class)))
                .thenThrow(new IllegalArgumentException("Amount must be a positive number"));
        Map<String, Object> request = new HashMap<>();
        request.put("customerId", 1L);
        request.put("amount", -10.00);
        request.put("date", "2024-07-01");
        mockMvc.perform(post("/api/transactions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new com.fasterxml.jackson.databind.ObjectMapper().writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Bad Request"));
    }

    @Test
    void testCreateTransactionMalformedJson() throws Exception {
        String malformedJson = "{\"customerId\": 1, \"amount\": 120.00, \"date\": 2024-07-01}"; // missing quotes around date value
        mockMvc.perform(post("/api/transactions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(malformedJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Malformed JSON request"));
    }

    @Test
    void testGetLastThreeMonthsRewards() throws Exception {
        when(rewardService.getLastThreeMonthsRewards()).thenReturn(new RewardsSummaryResponseDto());
        mockMvc.perform(get("/api/rewards/last-three-months"))
                .andExpect(status().isOk());
    }

    @Test
    void testGetCustomerMonthRewards() throws Exception {
        when(rewardService.getCustomerMonthRewards(anyLong(), anyString())).thenReturn(new ResponseDto());
        mockMvc.perform(get("/api/rewards/1/2024-05"))
                .andExpect(status().isOk());
    }
} 