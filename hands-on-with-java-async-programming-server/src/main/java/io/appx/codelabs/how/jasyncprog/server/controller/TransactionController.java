package io.appx.codelabs.how.jasyncprog.server.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.appx.codelabs.how.jasyncprog.server.model.Transactions;
import io.appx.codelabs.how.jasyncprog.server.repository.DataLoaderRepository;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/v1/transactions")
public class TransactionController {

    private static final int DEFAULT_DELAY = 2500;
    private final DataLoaderRepository repository;

    public TransactionController(DataLoaderRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public List<Transactions> getAllTransactions() {
        log.info("Get all transactions");
        this.delay(DEFAULT_DELAY);
        return repository.getTransactions();
    }

    @GetMapping("/by/store/{store_id}")
    public List<Transactions> getTransactionsByStoreId(
            @PathVariable(name = "store_id") long storeId) {
        log.info("Lookup transactions by Store Id : {}", storeId);
        this.delay(DEFAULT_DELAY);
        return repository.getTransactionsByStoreId(storeId);
    }

    @GetMapping("/by/store/{store_id}/success")
    public List<Transactions> getSuccessTransactionsByStore(
            @PathVariable(name = "store_id") long storeId) {
        log.info("Lookup successful transactions by Store Id : {}", storeId);
        this.delay(DEFAULT_DELAY);
        return repository.getSuccessTransactionsByStoreId(storeId);
    }

    @GetMapping("/by/store/{store_id}/failed")
    public List<Transactions> getFailedTransactionsByStore(
            @PathVariable(name = "store_id") long storeId) {
        log.info("Lookup failed transactions by Store Id : {}", storeId);
        this.delay(DEFAULT_DELAY);
        return repository.getFailedTransactionsByStoreId(storeId);
    }

    @GetMapping("/by/merchant/{merchant_id}")
    public List<Transactions> getTransactionsByMerchant(
            @PathVariable(name = "merchant_id") long merchantId) {
        log.info("Lookup transactions by Merchant Id : {}", merchantId);
        this.delay(DEFAULT_DELAY);
        return repository.getAllTransactionByMerchantid(merchantId);
    }

    @GetMapping("/by/merchant/{merchant_id}/failed")
    public List<Transactions> getFailedTransactionsByMerchant(
            @PathVariable(name = "merchant_id") long merchantId) {
        log.info("Lookup failed transactions by Merchant Id : {}", merchantId);
        this.delay(DEFAULT_DELAY);
        return repository.getFailedTransactionByMerchantid(merchantId);
    }

    @GetMapping("/by/merchant/{merchant_id}/success")
    public List<Transactions> getSuccessTransactionsByMerchant(
            @PathVariable(name = "merchant_id") long merchantId) {
        log.info("Lookup successful transactions by Merchant Id : {}", merchantId);
        this.delay(DEFAULT_DELAY);
        return repository.getSuccessTransactionByMerchantid(merchantId);
    }

    private void delay(long delayTimeInMilliSeconds) {
        try {
            Thread.sleep(delayTimeInMilliSeconds);
        } catch (InterruptedException e) {
            log.error("Thread sleep() call failed!");
        }
    }
}
