package io.appx.codelabs.how.jasyncprog.client;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.util.Assert;

import lombok.extern.slf4j.Slf4j;

import io.appx.codelabs.how.jasyncprog.client.model.Transactions;
import io.appx.codelabs.how.jasyncprog.client.service.RunnableService;

@Slf4j
@EnableFeignClients
@SpringBootApplication
public class ClientApplication implements CommandLineRunner {

  @Autowired private RunnableService runnableService;

  public static void main(String[] args) {
    SpringApplication.run(ClientApplication.class, args);
  }

  @Override
  public void run(String... args) throws Exception {
    List<Long> stores = List.of(5000L, 5002L, 5003L, 5004L);
    Map<Long, List<Transactions>> result = runnableService.getFailedTransactionsPerStore(stores);

    Assert.notEmpty(result, "Expected non empty map!");
    log.info("Application boot-up complete!");
  }
}
