package com.homework.rewards.api.dto;

import java.util.Map;

public class ResponseDto {
    private Long customerId;
    private String customerName;
    private Map<String, Integer> monthly;
    private int total;

    public ResponseDto() {}

    public ResponseDto(Long customerId, String customerName, Map<String, Integer> monthly, int total) {
        this.customerId = customerId;
        this.customerName = customerName;
        this.monthly = monthly;
        this.total = total;
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

    public Map<String, Integer> getMonthly() {
        return monthly;
    }

    public void setMonthly(Map<String, Integer> monthly) {
        this.monthly = monthly;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }
} 