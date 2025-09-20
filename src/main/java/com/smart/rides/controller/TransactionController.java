package com.smart.rides.controller;

import com.smart.rides.entity.Transaction;
import com.smart.rides.repository.TransactionRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/transactions")
@CrossOrigin(origins = {"http://localhost:3000"})
public class TransactionController {

    private final TransactionRepository transactionRepository;

    public TransactionController(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    @GetMapping("/history")
    public ResponseEntity<List<Transaction>> getTransactionHistory(@RequestParam Long userId) {
        List<Transaction> transactions = transactionRepository.findByUserIdOrderByTransactionDateDesc(userId);
        return ResponseEntity.ok(transactions);
    }
}
