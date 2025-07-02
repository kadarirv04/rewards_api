package com.homework.rewards.api.controller;

import com.homework.rewards.api.model.Customer;
import com.homework.rewards.api.model.Transaction;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@RestController
public class ApiController {
    private static final List<Customer> customers = new ArrayList<>();
    private static final List<Transaction> transactions = new ArrayList<>();
    private static final AtomicLong txId = new AtomicLong(1);
    static {
        customers.add(new Customer(1L, "Alice"));
        customers.add(new Customer(2L, "Bob"));
        customers.add(new Customer(3L, "Charlie"));
        transactions.add(new Transaction(txId.getAndIncrement(), 1L, new BigDecimal("120.00"), LocalDate.now().minusMonths(2)));
        transactions.add(new Transaction(txId.getAndIncrement(), 1L, new BigDecimal("75.00"), LocalDate.now().minusMonths(1)));
        transactions.add(new Transaction(txId.getAndIncrement(), 2L, new BigDecimal("200.00"), LocalDate.now().minusMonths(2)));
        transactions.add(new Transaction(txId.getAndIncrement(), 3L, new BigDecimal("50.00"), LocalDate.now().minusMonths(1)));
    }

    @GetMapping("/rewards")
    public Map<Long, Object> getRewards() {
        Map<Long, Object> result = new HashMap<>();
        for (Customer c : customers) {
            List<Transaction> txs = transactions.stream().filter(t -> t.getCustomerId().equals(c.getId())).collect(Collectors.toList());
            Map<YearMonth, Integer> monthly = new HashMap<>();
            int total = 0;
            for (Transaction t : txs) {
                int points = calcPoints(t.getAmount());
                YearMonth ym = YearMonth.from(t.getDate());
                monthly.put(ym, monthly.getOrDefault(ym, 0) + points);
                total += points;
            }
            Map<String, Object> cust = new HashMap<>();
            cust.put("name", c.getName());
            cust.put("monthly", monthly);
            cust.put("total", total);
            result.put(c.getId(), cust);
        }
        return result;
    }

    @PostMapping("/transaction")
    public ResponseEntity<?> addTransaction(@RequestBody Map<String, Object> req) {
        Long customerId = Long.valueOf(req.get("customerId").toString());
        BigDecimal amount = new BigDecimal(req.get("amount").toString());
        LocalDate date = LocalDate.parse(req.get("date").toString());
        
        Transaction transaction = new Transaction(txId.getAndIncrement(), customerId, amount, date);
        transactions.add(transaction);
        
        int points = calcPoints(amount);
        Customer customer = customers.stream().filter(c -> c.getId().equals(customerId)).findFirst().orElse(null);
        
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Transaction added successfully");
        
        Map<String, Object> transactionInfo = new HashMap<>();
        transactionInfo.put("id", transaction.getId());
        transactionInfo.put("customerId", transaction.getCustomerId());
        transactionInfo.put("customerName", customer != null ? customer.getName() : "Unknown");
        transactionInfo.put("amount", transaction.getAmount());
        transactionInfo.put("date", transaction.getDate().toString());
        response.put("transaction", transactionInfo);
        
        response.put("pointsEarned", points);
        response.put("pointsCalculation", getPointsExplanation(amount));
        
        return ResponseEntity.ok(response);
    }

    private int calcPoints(BigDecimal amount) {
        int points = 0;
        if (amount.compareTo(new BigDecimal("100")) > 0) {
            points += (amount.intValue() - 100) * 2 + 50;
        } else if (amount.compareTo(new BigDecimal("50")) > 0) {
            points += (amount.intValue() - 50);
        }
        return points;
    }
    
    private String getPointsExplanation(BigDecimal amount) {
        if (amount.compareTo(new BigDecimal("100")) > 0) {
            int over100 = amount.intValue() - 100;
            int pointsOver100 = over100 * 2;
            int points50to100 = 50;
            return String.format("$%d over $100 = %d × 2 = %d points, $50-$100 = %d points, Total = %d points", 
                over100, over100, pointsOver100, points50to100, pointsOver100 + points50to100);
        } else if (amount.compareTo(new BigDecimal("50")) > 0) {
            int over50 = amount.intValue() - 50;
            return String.format("$%d over $50 = %d × 1 = %d points", over50, over50, over50);
        } else {
            return "Amount under $50 = 0 points";
        }
    }
} 