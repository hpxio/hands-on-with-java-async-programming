package io.dice28.codelabs.multithreading.client.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Service;

import io.dice28.codelabs.multithreading.client.model.Transactions;
import io.dice28.codelabs.multithreading.client.rest.TransactionFeignClient;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class RunnableService {

  private final TransactionFeignClient feignClient;

  public RunnableService(TransactionFeignClient feignClient) {
    this.feignClient = feignClient;
  }

  // E05
  public Map<Long, List<Transactions>> getFailedTransactionsPerStoreSynchronous(
      List<Long> storeIds) {
    Map<Long, List<Transactions>> result = new HashMap<>();
    for (Long storeId : storeIds) {
      log.info("Fetching data for store : {}", storeId);
      List<Transactions> failed = feignClient.getFailedTransactionsForStore(storeId);
      log.info("Store : <{}> : Count : <{}>", storeId, failed.size());
      result.put(storeId, failed);
    }
    return result;
  }

  // E06
  public Map<Long, List<Transactions>> getFailedTransactionsPerStoreAsynchronous(
      List<Long> storeIds) {
    final Map<Long, List<Transactions>> result = new ConcurrentHashMap<>();
    for (Long storeId : storeIds) {
      Runnable runnable =
          () -> {
            log.info("Fetching data for store : {}", storeId);
            List<Transactions> failed = feignClient.getFailedTransactionsForStore(storeId);
            log.info("Store : <{}> : Count : <{}>", storeId, failed.size());
            result.put(storeId, failed);
          };

      Thread t = new Thread(runnable, "Thread-" + storeId);
      t.start();
    }

    try {
      /* required to stop for all threads to complete their working */
      Thread.sleep(4500L);
    } catch (InterruptedException e) {
      log.error("Error waiting for thread to complete!");
    }
    return result;
  }
}
