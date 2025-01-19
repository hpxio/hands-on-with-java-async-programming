package io.appx.codelabs.how.jasyncprog.client.service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Supplier;

import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

import io.appx.codelabs.how.jasyncprog.client.model.Transactions;
import io.appx.codelabs.how.jasyncprog.client.rest.TransactionFeignClient;

@Slf4j
@Service
public class CompletableService {

  private final TransactionFeignClient feignClient;

  public CompletableService(TransactionFeignClient feignClient) {
    this.feignClient = feignClient;
  }

  // E07 // Getting records for single merchant, threads from ForkJoinPool
  public void getSuccessfulTransactionsByMerchants(List<Long> merchants) {
    Supplier<List<Transactions>> trxSupplier =
        () -> {
          Long first = merchants.getFirst();
          log.info("Collecting transactions for merchant : {}", first);
          return feignClient.getSuccessTransactionsForMerchant(first);
        };

    /* simplest way to create, invoke then collect result from CF */
    CompletableFuture<List<Transactions>> cf = CompletableFuture.supplyAsync(trxSupplier);
    List<Transactions> transactions = cf.join();
    log.info("Transactions Count : {}", transactions.size());
  }

  // E07A Same example, but Threads created from an Executor
  public void getSuccessfulTransactionsByMerchantsA(List<Long> merchants) {
    try (ExecutorService exec = Executors.newSingleThreadExecutor()) {
      CompletableFuture<List<Transactions>> futureResult =
          CompletableFuture.supplyAsync(
              () -> {
                Long first = merchants.getFirst();
                log.info("Collecting transactions for merchant : {}", first);
                return feignClient.getSuccessTransactionsForMerchant(first);
              },
              exec);

      /* invoke CF, wait for result then print the statement - all in one line */
      futureResult.thenAccept((trx) -> log.info("Transactions Count : {}", trx.size()));
    }
  }

  // E08
  public void getSuccessfulTransactionsByMerchantsAll(List<Long> merchants) {
    try (ExecutorService exec = Executors.newFixedThreadPool(2)) {
      List<CompletableFuture<List<Transactions>>> futures = new ArrayList<>();
      merchants.forEach(
          merchant ->
              futures.add(
                  CompletableFuture.supplyAsync(
                      () -> {
                        log.info("Collecting Transactions for : {}", merchant);
                        return feignClient.getSuccessTransactionsForMerchant(merchant);
                      },
                      exec)));

      /* combine all futures into one to invoke in parallel */
      CompletableFuture<Void> allFutures =
          CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));

      /* start all futures calling join() and add exception handler */
      CompletableFuture<List<List<Transactions>>> startedFutures =
          allFutures
              .thenApply(v -> futures.stream().map(CompletableFuture::join).toList())
              .exceptionally(
                  ex -> {
                    log.error("Error during result collection : {}", ex.getMessage());
                    return null;
                  });

      /* wait for all futures to complete then print results from each future */
      startedFutures.thenAccept(
          res -> res.forEach(count -> log.info("Counter : {}", count.size())));
    }
  }
}
