package com.homework.rewards.api.dto;

import java.math.BigDecimal;

public class TransactionResponseDto {
    private String message;
    private Long id;
    private Long customerId;
    private String customerName;
    private BigDecimal amount;
    private String date;
    private int pointsEarned;
    private String pointsCalculation;

    public TransactionResponseDto() {}

    public TransactionResponseDto(String message, Long id, Long customerId, String customerName, BigDecimal amount, String date, int pointsEarned, String pointsCalculation) {
        this.message = message;
        this.id = id;
        this.customerId = customerId;
        this.customerName = customerName;
        this.amount = amount;
        this.date = date;
        this.pointsEarned = pointsEarned;
        this.pointsCalculation = pointsCalculation;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
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

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getPointsEarned() {
        return pointsEarned;
    }

    public void setPointsEarned(int pointsEarned) {
        this.pointsEarned = pointsEarned;
    }

    public String getPointsCalculation() {
        return pointsCalculation;
    }

    public void setPointsCalculation(String pointsCalculation) {
        this.pointsCalculation = pointsCalculation;
    }
} 