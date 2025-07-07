package com.homework.rewards.api.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

//Transaction entity representing a purchase transaction
@Entity
@Table(name = "transaction")
public class Transaction implements java.io.Serializable {
    private static final long serialVersionUID = 1L;
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    @ManyToOne
    @JoinColumn(name = "customer_id")
    private Customer customer;
    @Column(name = "amount")
    private BigDecimal amount;
    @Column(name = "date")
    private LocalDate date;
    
    public Transaction() {}
    
    public Transaction(Long id, Customer customer, BigDecimal amount, LocalDate date) {
        this.id = id;
        this.customer = customer;
        this.amount = amount;
        this.date = date;
    }
    
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Customer getCustomer() {
        return customer;
    }
    
    public void setCustomer(Customer customer) {
        this.customer = customer;
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