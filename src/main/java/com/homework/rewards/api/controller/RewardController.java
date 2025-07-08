package com.homework.rewards.api.controller;

import com.homework.rewards.api.service.RewardService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class RewardController {
    private final RewardService rewardService;

    @Autowired
    public RewardController(RewardService rewardService) {
        this.rewardService = rewardService;
    }

    @GetMapping("/rewards")
    public Map<Long, Object> getAllCustomerRewards() {
        return rewardService.getAllCustomerRewards();
    }

    @PostMapping("/transactions")
    public ResponseEntity<?> createTransaction(@RequestBody Map<String, Object> transactionRequest) {
        Map<String, Object> response = rewardService.createTransaction(transactionRequest);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/rewards/last-three-months")
    public Map<Long, Object> getLastThreeMonthsRewards() {
        return rewardService.getLastThreeMonthsRewards();
    }

    @GetMapping("/rewards/{customerId}/{yearMonth}")
    public Map<String, Object> getCustomerMonthRewards(@PathVariable Long customerId, @PathVariable String yearMonth) {
        return rewardService.getCustomerMonthRewards(customerId, yearMonth);
    }
} 