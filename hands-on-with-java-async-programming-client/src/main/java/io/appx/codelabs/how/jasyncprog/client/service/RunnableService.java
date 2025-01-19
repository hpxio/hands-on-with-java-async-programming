package io.appx.codelabs.how.jasyncprog.client.service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

import io.appx.codelabs.how.jasyncprog.client.model.Transactions;
import io.appx.codelabs.how.jasyncprog.client.rest.TransactionFeignClient;

@Slf4j
@Service
public class RunnableService {

  private final TransactionFeignClient feignClient;

  public RunnableService(TransactionFeignClient feignClient) {
    this.feignClient = feignClient;
  }

  // E05
  public Map<Long, List<Transactions>> getFailedTransactionsPerStore(List<Long> storeIds) {
    final Map<Long, List<Transactions>> result = new ConcurrentHashMap<>();
    for (long s : storeIds) {
      Runnable r =
          () -> {
            List<Transactions> failed = feignClient.getFailedTransactionsForStore(s);
            log.info("Store : <{}> : Count : <{}>", s, failed.size());
            result.put(s, failed);
          };

      Thread t = new Thread(r, "Thread-" + s);
      t.start();
    }

    try {
      Thread.sleep(3000L);
    } catch (InterruptedException e) {
      log.error("Error waiting for thread to complete!");
    }
    return result;
  }

  // E06
  private void getFailedTransactionPerStoreWithoutThreads(List<Long> storeIds) {
    long startTime = System.nanoTime();
    for (long s : storeIds) {
      List<Transactions> failed = feignClient.getFailedTransactionsForStore(s);
      log.info("Store : <{}> : Count : <{}>", s, failed.size());
    }
    long endTime = System.nanoTime();
    log.info("Time Taken : {}", (endTime - startTime) / 1000000);
  }
}
