package com.homework.rewards.api.model;

import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.*;

class TransactionTest {
    @Test
    void testConstructorAndGettersSetters() {
        Customer customer = new Customer(1L, "Test");
        Transaction tx = new Transaction(1L, customer, new BigDecimal("100.00"), LocalDate.of(2024, 5, 1));
        assertEquals(1L, tx.getId());
        assertEquals(customer, tx.getCustomer());
        assertEquals(new BigDecimal("100.00"), tx.getAmount());
        assertEquals(LocalDate.of(2024, 5, 1), tx.getDate());
        tx.setId(2L);
        tx.setCustomer(new Customer(2L, "Other"));
        tx.setAmount(new BigDecimal("200.00"));
        tx.setDate(LocalDate.of(2024, 6, 1));
        assertEquals(2L, tx.getId());
        assertEquals(new BigDecimal("200.00"), tx.getAmount());
        assertEquals(LocalDate.of(2024, 6, 1), tx.getDate());
    }
} 