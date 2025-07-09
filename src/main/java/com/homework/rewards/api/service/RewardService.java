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
import com.homework.rewards.api.dto.ResponseDto;
import com.homework.rewards.api.dto.RewardsSummaryResponseDto;
import com.homework.rewards.api.dto.TransactionResponseDto;
import java.time.format.DateTimeParseException;

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
        BigDecimal points = BigDecimal.ZERO;
        BigDecimal hundred = new BigDecimal("100");
        BigDecimal fifty = new BigDecimal("50");
        if (amount.compareTo(hundred) > 0) {
            BigDecimal over100 = amount.subtract(hundred);
            points = points.add(over100.multiply(new BigDecimal("2")));
            points = points.add(fifty); // $50 between 50 and 100 always gets 1x points
        } else if (amount.compareTo(fifty) > 0) {
            BigDecimal over50 = amount.subtract(fifty);
            points = points.add(over50);
        }
        return points.intValue();
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

    // Helper to build ResponseDto
    private ResponseDto buildResponseDto(Customer customer, List<Transaction> transactions, Set<YearMonth> filterMonths) {
        if (transactions == null) transactions = Collections.emptyList();
        List<Transaction> filtered = transactions.stream()
            .filter(t -> t.getCustomer().getId().equals(customer.getId()) && (filterMonths == null || filterMonths.contains(YearMonth.from(t.getDate()))))
            .collect(Collectors.toList());
        Map<String, Integer> monthly = filtered.stream().collect(Collectors.groupingBy(
            t -> t.getDate().getYear() + "-" + String.format("%02d", t.getDate().getMonthValue()),
            Collectors.summingInt(t -> calculateRewardPoints(t.getAmount()))
        ));
        int total = filtered.stream().mapToInt(t -> calculateRewardPoints(t.getAmount())).sum();
        return new ResponseDto(customer.getId(), customer.getName(), monthly, total);
    }

    // Helper to fetch and build rewards summary for all customers with optional month filter
    private RewardsSummaryResponseDto buildRewardsSummaryForCustomers(Set<YearMonth> filterMonths) {
        List<Customer> customers = customerRepository.findAll();
        List<Transaction> transactions = transactionRepository.findAll();
        if (transactions == null) transactions = Collections.emptyList();
        Map<Long, ResponseDto> result = new HashMap<>();
        for (Customer c : customers) {
            result.put(c.getId(), buildResponseDto(c, transactions, filterMonths));
        }
        return new RewardsSummaryResponseDto(result);
    }

    // Helper to validate and parse date
    private LocalDate validateAndParseDate(Object dateObj) {
        if (dateObj == null) throw new IllegalArgumentException("Date must not be null");
        LocalDate date;
        try {
            date = LocalDate.parse(dateObj.toString());
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Date must be in ISO format (yyyy-MM-dd)");
        }
        if (date.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("Date cannot be in the future");
        }
        if (date.isBefore(LocalDate.of(2000, 1, 1))) {
            throw new IllegalArgumentException("Date cannot be before 2000-01-01");
        }
        return date;
    }

    public RewardsSummaryResponseDto getAllCustomerRewards() {
        return buildRewardsSummaryForCustomers(null);
    }

    public RewardsSummaryResponseDto getLastThreeMonthsRewards() {
        YearMonth now = YearMonth.now();
        Set<YearMonth> lastThree = new HashSet<>(Arrays.asList(now, now.minusMonths(1), now.minusMonths(2)));
        return buildRewardsSummaryForCustomers(lastThree);
    }

    public ResponseDto getCustomerMonthRewards(Long customerId, String yearMonth) {
        Customer customer = customerRepository.findById(customerId).orElseThrow(() -> new NotFoundException("Customer not found with ID: " + customerId));
        List<Transaction> transactions = transactionRepository.findAll();
        if (transactions == null) transactions = Collections.emptyList();
        Set<YearMonth> filter = Collections.singleton(YearMonth.parse(yearMonth));
        return buildResponseDto(customer, transactions, filter);
    }

    public TransactionResponseDto createTransaction(Map<String, Object> transactionRequest) {
        if (transactionRequest == null) throw new IllegalArgumentException("Transaction request must not be null");
        Object customerIdObj = transactionRequest.get("customerId");
        Object amountObj = transactionRequest.get("amount");
        Object dateObj = transactionRequest.get("date");
        if (customerIdObj == null) throw new IllegalArgumentException("Customer ID must not be null");
        if (amountObj == null) throw new IllegalArgumentException("Amount must not be null");
        LocalDate date = validateAndParseDate(dateObj);
        Long customerId = Long.valueOf(customerIdObj.toString());
        BigDecimal amount = new BigDecimal(amountObj.toString());
        return createTransaction(customerId, amount, date);
    }

    public TransactionResponseDto createTransaction(Long customerId, BigDecimal amount, LocalDate date) {
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
        return new TransactionResponseDto(
            "Transaction added successfully",
            transaction.getId(),
            customer.getId(),
            customer.getName(),
            transaction.getAmount(),
            transaction.getDate().toString(),
            points,
            getRewardPointsExplanation(amount)
        );
    }
} 