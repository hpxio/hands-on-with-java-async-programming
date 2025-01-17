package io.appx.codelabs.how.jasyncprog.client.service;

import io.appx.codelabs.how.jasyncprog.client.model.Transactions;
import io.appx.codelabs.how.jasyncprog.client.rest.TransactionFeignClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class RunnableService {

    private final TransactionFeignClient feignClient;

    public RunnableService(TransactionFeignClient feignClient) {
        this.feignClient = feignClient;
    }

    public void run() {
        List<Long> input = List.of(5000L, 5001L, 5002L, 5003L, 5004L);

        getFailedTransactionPerStoreUsingThreads(input);
        getFailedTransactionPerStoreWithoutThreads(input);
    }

    private void getFailedTransactionPerStoreUsingThreads(List<Long> storeIds) {
        for (long s : storeIds) {
            Runnable r = () -> {
                List<Transactions> failed = feignClient.getFailedTransactionsForStore(s);
                log.info("Store : <{}> : Count : <{}>", s, failed.size());
            };
            Thread t = new Thread(r, "Thread-" + s);
            t.start();
        }

        try {
            Thread.sleep(100L);
        } catch (InterruptedException e) {
            log.error("Thread join failed : {}", e.getMessage());
        }
    }

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
