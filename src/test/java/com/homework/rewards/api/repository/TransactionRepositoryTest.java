package com.homework.rewards.api.repository;

import com.homework.rewards.api.model.Customer;
import com.homework.rewards.api.model.Transaction;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class TransactionRepositoryTest {
    @Autowired
    private CustomerRepository customerRepository;
    @Autowired
    private TransactionRepository transactionRepository;

    @Test
    void testSaveAndFindTransaction() {
        Customer customer = new Customer();
        customer.setName("RepoTest");
        Customer savedCustomer = customerRepository.save(customer);
        Transaction tx = new Transaction();
        tx.setCustomer(savedCustomer);
        tx.setAmount(new BigDecimal("99.99"));
        tx.setDate(LocalDate.of(2024, 5, 1));
        Transaction savedTx = transactionRepository.save(tx);
        Optional<Transaction> found = transactionRepository.findById(savedTx.getId());
        assertTrue(found.isPresent());
        assertEquals(new BigDecimal("99.99"), found.get().getAmount());
        assertEquals(savedCustomer.getId(), found.get().getCustomer().getId());
    }
} 