package com.homework.rewards.api.service;

import com.homework.rewards.api.exception.NotFoundException;
import com.homework.rewards.api.model.Customer;
import com.homework.rewards.api.model.Transaction;
import com.homework.rewards.api.repository.CustomerRepository;
import com.homework.rewards.api.repository.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;
import com.homework.rewards.api.dto.RewardsSummaryResponseDto;
import com.homework.rewards.api.dto.ResponseDto;
import com.homework.rewards.api.dto.TransactionResponseDto;
import com.homework.rewards.api.util.RewardCalculationUtil;

@SpringBootTest
class RewardServiceTest {
    @Autowired
    private RewardService rewardService;
    @Autowired
    private CustomerRepository customerRepository;
    @Autowired
    private TransactionRepository transactionRepository;

    private Customer customer;
    private Transaction transaction;

    @BeforeEach
    void setUp() {
        transactionRepository.deleteAll();
        customerRepository.deleteAll();
        customer = new Customer();
        customer.setName("Test User");
        customer = customerRepository.save(customer);
        transaction = new Transaction();
        transaction.setCustomer(customer);
        transaction.setAmount(new BigDecimal("120.00"));
        transaction.setDate(LocalDate.now());
        transaction = transactionRepository.save(transaction);
    }

    @Test
    void testCalculateRewardPoints() {
        assertEquals(90, RewardCalculationUtil.calculateRewardPoints(new BigDecimal("120")));
        assertEquals(10, RewardCalculationUtil.calculateRewardPoints(new BigDecimal("60")));
        assertEquals(0, RewardCalculationUtil.calculateRewardPoints(new BigDecimal("40")));
    }

    @Test
    void testCreateTransactionSuccess() {
        TransactionResponseDto result = rewardService.createTransaction(customer.getId(), new BigDecimal("120.00"), LocalDate.now());
        assertEquals("Transaction added successfully", result.getMessage());
        assertEquals(90, result.getPointsEarned());
    }

    @Test
    void testCreateTransactionCustomerNotFound() {
        assertThrows(NotFoundException.class, () ->
            rewardService.createTransaction(9999L, new BigDecimal("100.00"), LocalDate.now()));
    }

    @Test
    void testCreateTransactionInvalidAmount() {
        assertThrows(IllegalArgumentException.class, () ->
            rewardService.createTransaction(customer.getId(), new BigDecimal("-10.00"), LocalDate.now()));
    }

    @Test
    void testCreateTransactionNullDate() {
        assertThrows(IllegalArgumentException.class, () ->
            rewardService.createTransaction(customer.getId(), new BigDecimal("100.00"), null));
    }

    @Test
    void testGetAllCustomerRewards() {
        RewardsSummaryResponseDto result = rewardService.getAllCustomerRewards();
        assertTrue(result.getRewards().containsKey(customer.getId()));
    }

    @Test
    void testGetLastThreeMonthsRewards() {
        RewardsSummaryResponseDto result = rewardService.getLastThreeMonthsRewards();
        assertTrue(result.getRewards().containsKey(customer.getId()));
    }

    @Test
    void testGetCustomerMonthRewards() {
        String yearMonth = LocalDate.now().getYear() + "-" + String.format("%02d", LocalDate.now().getMonthValue());
        ResponseDto result = rewardService.getCustomerMonthRewards(customer.getId(), yearMonth);
        assertEquals(customer.getId(), result.getCustomerId());
        assertTrue(result.getMonthly().containsKey(yearMonth));
    }
} 