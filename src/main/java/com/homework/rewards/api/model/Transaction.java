package com.homework.rewards.api.model;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Transaction entity representing a purchase transaction
 */
public class Transaction {
    
    private Long id;
    private Long customerId;
    private BigDecimal amount;
    private LocalDate date;
    
    public Transaction() {}
    
    public Transaction(Long id, Long customerId, BigDecimal amount, LocalDate date) {
        this.id = id;
        this.customerId = customerId;
        this.amount = amount;
        this.date = date;
    }
    
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Long getCustomerId() {
        return customerId;
    }
    
    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }
    
    public BigDecimal getAmount() {
        return amount;
    }
    
    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
    
    public LocalDate getDate() {
        return date;
    }
    
    public void setDate(LocalDate date) {
        this.date = date;
    }
} 