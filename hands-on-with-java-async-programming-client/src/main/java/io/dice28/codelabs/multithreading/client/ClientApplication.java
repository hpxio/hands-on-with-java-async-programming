package io.dice28.codelabs.multithreading.client;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.ConfigurableApplicationContext;

import lombok.extern.slf4j.Slf4j;

import io.dice28.codelabs.multithreading.client.service.CompletableService;
import io.dice28.codelabs.multithreading.client.service.RunnableService;

@Slf4j
@EnableFeignClients
@SpringBootApplication
public class ClientApplication implements CommandLineRunner {

  private static final String TEST_CODE = "E08";

  @Autowired private RunnableService runnableService;

  @Autowired private CompletableService completableService;

  public static void main(String[] args) {
    ConfigurableApplicationContext context = SpringApplication.run(ClientApplication.class, args);
  }

  @Override
  public void run(String... args) throws Exception {
    long startTime, stopTime;
    List<Long> stores = List.of(5000L, 5001L, 5002L, 5003L, 5004L);
    List<Long> merchants = List.of(1001L, 2001L, 3001L, 4001L);

    startTime = System.nanoTime();
    switch (TEST_CODE) {
      case "E05" -> scenarioUnderTestE05(stores);
      case "E06" -> scenarioUnderTestE06(stores);
      case "E07" -> scenarioUnderTestE07(merchants);
      case "E08" -> scenarioUnderTestE08(merchants);
    }
    stopTime = System.nanoTime();
    log.info("Time Taken : {}", (stopTime - startTime) / 1000000);
  }

  private void scenarioUnderTestE05(List<Long> stores) {
    if ("E05".equalsIgnoreCase(TEST_CODE))
      runnableService.getFailedTransactionsPerStoreSynchronous(stores);
  }

  private void scenarioUnderTestE06(List<Long> stores) {
    if ("E06".equalsIgnoreCase(TEST_CODE))
      runnableService.getFailedTransactionsPerStoreAsynchronous(stores);
  }

  private void scenarioUnderTestE07(List<Long> merchants) {
    if ("E07".equalsIgnoreCase(TEST_CODE)) {
      completableService.getSuccessfulTransactionsByMerchants(merchants);
      completableService.getSuccessfulTransactionsByMerchantsA(merchants);
    }
  }

  private void scenarioUnderTestE08(List<Long> merchants) {
    if ("E08".equalsIgnoreCase(TEST_CODE))
      completableService.getSuccessfulTransactionsByMerchantsAll(merchants);
  }
}
