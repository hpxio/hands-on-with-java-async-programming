package io.appx.codelabs.how.jasyncprog.client.service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.springframework.stereotype.Service;

import org.apache.logging.log4j.util.Strings;

import lombok.extern.slf4j.Slf4j;

import io.appx.codelabs.how.jasyncprog.client.model.Transactions;
import io.appx.codelabs.how.jasyncprog.client.rest.TransactionFeignClient;

@Slf4j
@Service
public class CallableService {

  private final TransactionFeignClient feignClient;

  public CallableService(TransactionFeignClient feignClient) {
    this.feignClient = feignClient;
  }

  // E01
  public void singleThreadCallableExample() {
    /* single threaded callable example to process one merchant */
    Callable<List<Transactions>> trxCallable =
        () -> {
          log.info("Started calling Server");
          return feignClient.getSuccessTransactionsForMerchant(1001L);
        };

    try (ExecutorService exec = Executors.newSingleThreadExecutor()) {
      Future<List<Transactions>> trxFuture = exec.submit(trxCallable);
      trxFuture.get().forEach(t -> log.info("Transaction:{}", t));
    } catch (InterruptedException | ExecutionException e) {
      e.printStackTrace();
    }
  }

  // E02
  private void singleThreadCallableExampleBad(List<Long> input) {
    try (ExecutorService exec = Executors.newFixedThreadPool(1)) {
      for (Long merchantId : input) {
        Callable<List<Transactions>> trxCallable =
            () -> {
              log.info("Started called Server");
              return feignClient.getSuccessTransactionsForMerchant(merchantId);
            };
        Future<List<Transactions>> trxFuture = exec.submit(trxCallable);
        try {
          log.info("Transactions Count : {}", trxFuture.get().size());
        } catch (InterruptedException | ExecutionException e) {
          e.printStackTrace();
        }
      }
    }
  }

  // E03
  private void multiThreadCallableExampleBetter(List<Long> input) {
    List<Callable<List<Transactions>>> callableList =
        input.stream()
            .map(
                id -> {
                  Callable<List<Transactions>> c =
                      () -> {
                        log.info("Started calling Server for : {}", id);
                        return feignClient.getSuccessTransactionsForMerchant(id);
                      };
                  return c;
                })
            .toList();

    try (ExecutorService exec = Executors.newFixedThreadPool(2)) {
      List<Future<List<Transactions>>> trxFutureList;
      trxFutureList = exec.invokeAll(callableList);
      trxFutureList.forEach(
          f -> {
            try {
              log.info("Results : {}", f.get().size());
            } catch (InterruptedException | ExecutionException e) {
              log.info("Error when getting Future value!");
            }
          });
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }

  // E04
  private void multiThreadCallableExampleBetterish(List<Long> input) throws InterruptedException {
    ConcurrentHashMap<Long, List<Transactions>> result = new ConcurrentHashMap<>();
    List<Callable<Void>> trxCallableList = new ArrayList<>();

    try (ExecutorService exec = Executors.newFixedThreadPool(2)) {
      input.forEach(
          merchantId -> {
            Callable<Void> trxCallable =
                () -> {
                  log.info("Started calling Server for : {}", merchantId);
                  List<Transactions> trx =
                      feignClient.getSuccessTransactionsForMerchant(merchantId);
                  result.put(merchantId, trx);
                  return null; // bad programming practice
                };
            trxCallableList.add(trxCallable);
          });

      exec.invokeAll(trxCallableList);
      exec.shutdown();

      while (!exec.isTerminated())
        ;
    }

    result.forEach((id, trx) -> log.info("Merchant : {}, Successful : {}", id, trx.size()));
  }

  private String prepareSummary(Long merchantId, List<Transactions> transactions) {
    /*
     * generates summary with following information:
     * name of merchant, total successful transactions,
     * highest value, lowest value, sum of all transactions
     * and mean/median of all transactions.
     */
    double max, min, sum, mean, average;

    Comparator<Transactions> amountCompare = Comparator.comparing(Transactions::getAmount);
    max = transactions.stream().max(amountCompare).orElseThrow().getAmount();
    min = transactions.stream().min(amountCompare).orElseThrow().getAmount();
    sum = transactions.stream().mapToDouble(Transactions::getAmount).sum();

    return Strings.EMPTY;
  }
}
