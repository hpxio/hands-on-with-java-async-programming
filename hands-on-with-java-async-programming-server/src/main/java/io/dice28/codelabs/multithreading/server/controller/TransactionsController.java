package io.dice28.codelabs.multithreading.server.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import io.dice28.codelabs.multithreading.server.model.Transactions;
import io.dice28.codelabs.multithreading.server.repository.TransactionRepository;
import io.dice28.codelabs.multithreading.server.utils.SleepUtils;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/transactions")
public class TransactionsController {

  private TransactionRepository repository;

  @GetMapping
  public List<Transactions> getTransactions() {
    log.info("Get all transactions");
    SleepUtils.longDelay();
    return repository.getTransactions();
  }
}
