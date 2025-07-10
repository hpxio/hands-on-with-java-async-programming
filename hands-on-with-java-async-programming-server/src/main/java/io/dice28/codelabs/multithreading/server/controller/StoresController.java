package io.dice28.codelabs.multithreading.server.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import io.dice28.codelabs.multithreading.server.model.Stores;
import io.dice28.codelabs.multithreading.server.model.Transactions;
import io.dice28.codelabs.multithreading.server.repository.TransactionRepository;
import io.dice28.codelabs.multithreading.server.utils.SleepUtils;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/stores")
public class StoresController {

  private final TransactionRepository repository;

  @GetMapping
  public List<Stores> getAllStores() {
    log.info("Get all stores");
    SleepUtils.defaultDelay();
    return repository.getStores();
  }

  @GetMapping("/store/{store_id}/transactions")
  public List<Transactions> getTransactionsByStoreIdAndState(
      @PathVariable(name = "store_id") long storeId,
      @RequestParam(name = "status", required = false) String status) {
    log.info("Get transactions by store : {}, state : <{}>", storeId, status);
    return repository.fetchTransactionByStoreAndStatus(storeId, status);
  }
}
