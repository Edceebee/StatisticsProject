package com.challenge.Statistics.controller;

import com.challenge.Statistics.models.ClearCache;
import com.challenge.Statistics.models.Transaction;

import com.challenge.Statistics.service.TransactionService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;

@RestController
public class TransactionController {

    @Autowired
    TransactionService transactionService;

    @PostMapping("/transactions")
    public ResponseEntity<?> createTransactions(@RequestBody Transaction transaction) {

        long current = Instant.now().toEpochMilli();
        boolean added = transactionService.createTransaction(transaction, current);
        if (added) {
            return new ResponseEntity<>(HttpStatus.CREATED);
        }
        else {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

    }

    @GetMapping("/statistics")
    public ResponseEntity<?> getStatistics() {
        long current = Instant.now().toEpochMilli();
        return new ResponseEntity<>(transactionService.getStatistics(current), HttpStatus.OK);
    }

    @DeleteMapping("/transactions")
    @ResponseBody
    public ResponseEntity<?> deleteTransaction(@RequestBody ClearCache cache) {
        boolean deleted = transactionService.deleteTransactions(cache);
        return new ResponseEntity<>(deleted, HttpStatus.NO_CONTENT);
    }
}
