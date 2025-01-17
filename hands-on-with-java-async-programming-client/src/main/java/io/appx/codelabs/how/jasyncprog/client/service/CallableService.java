package io.appx.codelabs.how.jasyncprog.client.service;

import io.appx.codelabs.how.jasyncprog.client.model.Transactions;
import io.appx.codelabs.how.jasyncprog.client.rest.TransactionFeignClient;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@Slf4j
@Service
public class CallableService implements CommandLineRunner {

    private static final boolean SHOULD_EXEC = true;
    private final TransactionFeignClient feignClient;

    public CallableService(TransactionFeignClient feignClient) {
        this.feignClient = feignClient;
    }

    public static void main(String[] args) {
        SpringApplication.run(CallableService.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        List<Long> input = List.of(1001L, 2001L, 3001L, 4001L);

        if (SHOULD_EXEC) {

        } else {
            /* uses single thread and submits only 1 task at a time */
            singleThreadCallableExample();

            /*
             * This example demonstrates a poor usage of Executor
             * and Callable, where the Executor is shut down
             * after submitting each task, preventing concurrent
             * execution and defeating the purpose of using
             * an Executor in the first place.
             */
            singleThreadCallableExampleBad(input);

            multiThreadCallableExampleBetter(input);

            multiThreadCallableExampleBetterish(input);
        }

        Thread.sleep(5000l);
        System.exit(0);
    }

    private void multiThreadCallableExampleBetterish(List<Long> input) throws InterruptedException {
        /* Map to hold result from each thread */
        ConcurrentHashMap<Long, List<Transactions>> result = new ConcurrentHashMap<>();

        /*
         * Collection of callables based on number of input merchants.
         * This type of implementation is useful if we want to process
         * each input separately. Like here, for each merchantId we want
         * to call RestAPI separately. But notice, this usecase is more
         * suitable for a Runnable thread.
         */
        List<Callable<Void>> trxCallableList = new ArrayList<>();

        try (ExecutorService exec = Executors.newFixedThreadPool(2)) {

            /* Create callables for each merchantId and add result to result map */
            input.forEach(merchantId -> {
                Callable<Void> trxCallable = () -> {
                    log.info("Started calling Server for : {}", merchantId);
                    List<Transactions> trx = feignClient.getSuccessTransactionsForMerchant(merchantId);
                    result.put(merchantId, trx);
                    return null; // bad programming practice
                };
                trxCallableList.add(trxCallable);
            });

            /*
             * Submits all tasks to the ExecutorService at once using
             * invokeAll, which allows for more efficient execution
             * compared to submitting tasks individually. This is
             * because invokeAll can batch tasks together and reduce
             * the overhead of thread creation and synchronization.
             * Additionally, using invokeAll allows us to shut down
             * the ExecutorService immediately after submitting all
             * tasks, which helps to prevent new tasks from being
             * submitted and ensures that the program can exit
             * cleanly once all tasks have completed.
             */
            exec.invokeAll(trxCallableList);
            exec.shutdown();

            while (!exec.isTerminated()) {
                /*
                 * do nothing, let all thread complete
                 * this is necessary since we do not have
                 * instance of future. shutdown() is called
                 * to stop any new threads entring executor.
                 */
            }
        }

        /*
         * Print results once all data is collected. There is better way to
         * prepare results and that too can be done in a separate thread. An
         * example is added in ABC1234 method.
         */
        result.forEach((id, trx) -> log.info("Merchant : {}, Successful : {}", id, trx.size()));
    }

    private void multiThreadCallableExampleBetter(List<Long> input) {
        List<Callable<List<Transactions>>> callableList = input.stream().map(id -> {
            Callable<List<Transactions>> c = () -> {
                log.info("Started calling Server for : {}", id);
                return feignClient.getSuccessTransactionsForMerchant(id);
            };
            return c;
        }).toList();

        try (ExecutorService exec = Executors.newFixedThreadPool(2)) {
            List<Future<List<Transactions>>> trxFutureList;
            trxFutureList = exec.invokeAll(callableList);
            trxFutureList.forEach(f -> {
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

    private void singleThreadCallableExampleBad(List<Long> input) {
        try (ExecutorService exec = Executors.newFixedThreadPool(1)) {
            for (Long merchantId : input) {
                Callable<List<Transactions>> trxCallable = () -> {
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

    private void singleThreadCallableExample() {
        /* single threaded callable example to process one merchant */
        Callable<List<Transactions>> trxCallable = () -> {
            log.info("Started calling Server");
            return feignClient.getSuccessTransactionsForMerchant(1001l);
        };

        ExecutorService exec = Executors.newSingleThreadExecutor();
        try (exec) {
            Future<List<Transactions>> trxFuture = exec.submit(trxCallable);
            trxFuture.get().forEach(t -> log.info("Transaction:{}", t));
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
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
