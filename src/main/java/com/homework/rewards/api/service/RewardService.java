package com.homework.rewards.api.service;

import com.homework.rewards.api.model.Customer;
import com.homework.rewards.api.model.Transaction;
import com.homework.rewards.api.repository.CustomerRepository;
import com.homework.rewards.api.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.*;
import java.util.stream.Collectors;
import com.homework.rewards.api.exception.NotFoundException;

@Service
public class RewardService {
    private final CustomerRepository customerRepository;
    private final TransactionRepository transactionRepository;

    @Autowired
    public RewardService(CustomerRepository customerRepository, TransactionRepository transactionRepository) {
        this.customerRepository = customerRepository;
        this.transactionRepository = transactionRepository;
    }

    public int calculateRewardPoints(BigDecimal amount) {
        int points = 0;
        if (amount.compareTo(new BigDecimal("100")) > 0) {
            points += (amount.intValue() - 100) * 2 + 50;
        } else if (amount.compareTo(new BigDecimal("50")) > 0) {
            points += (amount.intValue() - 50);
        }
        return points;
    }

    public String getRewardPointsExplanation(BigDecimal amount) {
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

    // Single aggregation method for all reward summaries
    private Map<String, Object> aggregateRewards(Customer customer, List<Transaction> transactions, Set<YearMonth> filterMonths) {
        List<Transaction> filtered = transactions.stream()
            .filter(t -> t.getCustomer().getId().equals(customer.getId()) && (filterMonths == null || filterMonths.contains(YearMonth.from(t.getDate()))))
            .collect(Collectors.toList());
        Map<String, Integer> monthly = filtered.stream().collect(Collectors.groupingBy(
            t -> t.getDate().getYear() + "-" + String.format("%02d", t.getDate().getMonthValue()),
            Collectors.summingInt(t -> calculateRewardPoints(t.getAmount()))
        ));
        int total = filtered.stream().mapToInt(t -> calculateRewardPoints(t.getAmount())).sum();
        Map<String, Object> summary = new HashMap<>();
        summary.put("name", customer.getName());
        summary.put("monthly", monthly);
        summary.put("total", total);
        return summary;
    }

    public Map<Long, Object> getAllCustomerRewards() {
        List<Customer> customers = customerRepository.findAll();
        List<Transaction> transactions = transactionRepository.findAll();
        Map<Long, Object> result = new HashMap<>();
        for (Customer c : customers) {
            result.put(c.getId(), aggregateRewards(c, transactions, null));
        }
        return result;
    }

    public Map<Long, Object> getLastThreeMonthsRewards() {
        List<Customer> customers = customerRepository.findAll();
        List<Transaction> transactions = transactionRepository.findAll();
        YearMonth now = YearMonth.now();
        Set<YearMonth> lastThree = new HashSet<>(Arrays.asList(now, now.minusMonths(1), now.minusMonths(2)));
        Map<Long, Object> result = new HashMap<>();
        for (Customer c : customers) {
            result.put(c.getId(), aggregateRewards(c, transactions, lastThree));
        }
        return result;
    }

    public Map<String, Object> getCustomerMonthRewards(Long customerId, String yearMonth) {
        Customer customer = customerRepository.findById(customerId).orElseThrow(() -> new NotFoundException("Customer not found with ID: " + customerId));
        List<Transaction> transactions = transactionRepository.findAll();
        int points = transactions.stream()
            .filter(t -> t.getCustomer().getId().equals(customerId) && (t.getDate().getYear() + "-" + String.format("%02d", t.getDate().getMonthValue())).equals(yearMonth))
            .mapToInt(t -> calculateRewardPoints(t.getAmount())).sum();
        Map<String, Object> result = new HashMap<>();
        result.put("customerId", customerId);
        result.put("customerName", customer.getName());
        result.put("yearMonth", yearMonth);
        result.put("points", points);
        return result;
    }

    public Map<String, Object> createTransaction(Map<String, Object> transactionRequest) {
        if (transactionRequest == null) throw new IllegalArgumentException("Transaction request must not be null");
        Object customerIdObj = transactionRequest.get("customerId");
        Object amountObj = transactionRequest.get("amount");
        Object dateObj = transactionRequest.get("date");
        if (customerIdObj == null) throw new IllegalArgumentException("Customer ID must not be null");
        if (amountObj == null) throw new IllegalArgumentException("Amount must not be null");
        if (dateObj == null) throw new IllegalArgumentException("Date must not be null");
        Long customerId = Long.valueOf(customerIdObj.toString());
        BigDecimal amount = new BigDecimal(amountObj.toString());
        LocalDate date = LocalDate.parse(dateObj.toString());
        return createTransaction(customerId, amount, date);
    }

    public Map<String, Object> createTransaction(Long customerId, BigDecimal amount, LocalDate date) {
        if (customerId == null) throw new IllegalArgumentException("Customer ID must not be null");
        if (amount == null || amount.compareTo(BigDecimal.ZERO) < 0) throw new IllegalArgumentException("Amount must be a positive number");
        if (date == null) throw new IllegalArgumentException("Date must not be null");
        Customer customer = customerRepository.findById(customerId).orElseThrow(() -> new NotFoundException("Customer not found with ID: " + customerId));
        Transaction transaction = new Transaction();
        transaction.setCustomer(customer);
        transaction.setAmount(amount);
        transaction.setDate(date);
        transactionRepository.save(transaction);
        int points = calculateRewardPoints(amount);
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Transaction added successfully");
        Map<String, Object> transactionInfo = new HashMap<>();
        transactionInfo.put("id", transaction.getId());
        transactionInfo.put("customerId", customer.getId());
        transactionInfo.put("customerName", customer.getName());
        transactionInfo.put("amount", transaction.getAmount());
        transactionInfo.put("date", transaction.getDate().toString());
        response.put("transaction", transactionInfo);
        response.put("pointsEarned", points);
        response.put("pointsCalculation", getRewardPointsExplanation(amount));
        return response;
    }
} 