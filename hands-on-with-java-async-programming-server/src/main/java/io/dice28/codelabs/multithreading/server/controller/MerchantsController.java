package io.dice28.codelabs.multithreading.server.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import io.dice28.codelabs.multithreading.server.model.Merchants;
import io.dice28.codelabs.multithreading.server.model.Stores;
import io.dice28.codelabs.multithreading.server.model.Transactions;
import io.dice28.codelabs.multithreading.server.repository.TransactionRepository;
import io.dice28.codelabs.multithreading.server.utils.SleepUtils;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/merchants")
public class MerchantsController {

  private final TransactionRepository repository;

  @GetMapping
  public List<Merchants> getAllMerchants() {
    log.info("Get all merchants");
    SleepUtils.defaultDelay();
    return repository.getMerchants();
  }

  @GetMapping("/merchant/{merchant_id}/stores")
  public List<Stores> getStoresByMerchant(@PathVariable(name = "merchant_id") long merchantId) {
    log.info("Get stores by merchant : {}", merchantId);
    return repository.fetchStoresByMerchant(merchantId);
  }

  @GetMapping("/merchant/{merchant_id}/transactions")
  public List<Transactions> getTransactionsByMerchant(
      @PathVariable(name = "merchant_id") long merchantId,
      @RequestParam(name = "status", required = false) String status) {
    log.info("Get transactions by merchant : {}, state : <{}>", merchantId, status);
    return repository.fetchTransactionByMerchantAndStatus(merchantId, status);
  }
}
