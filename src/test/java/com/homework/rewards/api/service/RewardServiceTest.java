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
        assertEquals(90, rewardService.calculateRewardPoints(new BigDecimal("120")));
        assertEquals(10, rewardService.calculateRewardPoints(new BigDecimal("60")));
        assertEquals(0, rewardService.calculateRewardPoints(new BigDecimal("40")));
    }

    @Test
    void testCreateTransactionSuccess() {
        Map<String, Object> result = rewardService.createTransaction(customer.getId(), new BigDecimal("120.00"), LocalDate.now());
        assertEquals("Transaction added successfully", result.get("message"));
        assertTrue(result.containsKey("transaction"));
        assertEquals(90, result.get("pointsEarned"));
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
        Map<Long, Object> result = rewardService.getAllCustomerRewards();
        assertTrue(result.containsKey(customer.getId()));
    }

    @Test
    void testGetLastThreeMonthsRewards() {
        Map<Long, Object> result = rewardService.getLastThreeMonthsRewards();
        assertTrue(result.containsKey(customer.getId()));
    }

    @Test
    void testGetCustomerMonthRewards() {
        String yearMonth = LocalDate.now().getYear() + "-" + String.format("%02d", LocalDate.now().getMonthValue());
        Map<String, Object> result = rewardService.getCustomerMonthRewards(customer.getId(), yearMonth);
        assertEquals(customer.getId(), result.get("customerId"));
        assertEquals(yearMonth, result.get("yearMonth"));
    }
} 