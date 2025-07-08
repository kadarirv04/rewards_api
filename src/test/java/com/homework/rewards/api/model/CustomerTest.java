package com.homework.rewards.api.model;

import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import static org.junit.jupiter.api.Assertions.*;

class CustomerTest {
    @Test
    void testConstructorAndGettersSetters() {
        Customer customer = new Customer(1L, "Test Name");
        assertEquals(1L, customer.getId());
        assertEquals("Test Name", customer.getName());
        customer.setId(2L);
        customer.setName("New Name");
        assertEquals(2L, customer.getId());
        assertEquals("New Name", customer.getName());
    }

    @Test
    void testTransactionsList() {
        Customer customer = new Customer();
        assertNotNull(customer.getTransactions());
        assertTrue(customer.getTransactions().isEmpty());
        ArrayList<Transaction> txs = new ArrayList<>();
        Transaction t = new Transaction();
        txs.add(t);
        customer.setTransactions(txs);
        assertEquals(1, customer.getTransactions().size());
    }
} 