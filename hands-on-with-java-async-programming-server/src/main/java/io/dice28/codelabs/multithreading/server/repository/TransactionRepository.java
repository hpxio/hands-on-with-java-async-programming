package io.dice28.codelabs.multithreading.server.repository;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

import jakarta.annotation.PostConstruct;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Repository;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import io.dice28.codelabs.multithreading.server.model.Merchants;
import io.dice28.codelabs.multithreading.server.model.Stores;
import io.dice28.codelabs.multithreading.server.model.Transactions;
import io.dice28.codelabs.multithreading.server.utils.SleepUtils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

@Slf4j
@Repository
public class TransactionRepository {

  private static final String SUCCESS_STATUS = "success";

  private static final String FAILED_STATUS = "failed";

  @Getter private List<Transactions> transactions;

  @Getter private List<Merchants> merchants;

  @Getter private List<Stores> stores;

  @PostConstruct
  private void load() {
    ObjectMapper mapper = new ObjectMapper();
    try {
      File transactionsFile = new ClassPathResource("data/transactions.json").getFile();
      File merchantsFile = new ClassPathResource("data/merchants.json").getFile();
      File storesFile = new ClassPathResource("data/stores.json").getFile();

      if (transactionsFile.exists() && merchantsFile.exists() && storesFile.exists()) {
        transactions = mapper.readValue(transactionsFile, new TypeReference<>() {});
        merchants = mapper.readValue(merchantsFile, new TypeReference<>() {});
        stores = mapper.readValue(storesFile, new TypeReference<>() {});
      } else {
        log.error("No seed data found!");
      }
    } catch (IOException e) {
      log.error("Cannot load seed data : {}", e.getMessage());
    }
  }

  public List<Transactions> fetchTransactionByStoreAndStatus(long storeId, String status) {
    return switch (status) {
      case SUCCESS_STATUS, FAILED_STATUS -> {
        SleepUtils.defaultDelay();
        yield filterTransactionByStoreIdAndStatus(storeId, status);
      }
      default -> {
        SleepUtils.longDelay();
        yield filterTransactionByStoreId(storeId);
      }
    };
  }

  public List<Transactions> fetchTransactionByMerchantAndStatus(long merchantId, String status) {
    return switch (status) {
      case SUCCESS_STATUS, FAILED_STATUS -> {
        SleepUtils.longDelay();
        yield filterTransactionByMerchantIdAndStatus(merchantId, status);
      }
      default -> {
        SleepUtils.longDelay();
        yield filterTransactionByMerchantId(merchantId);
      }
    };
  }

  /**
   * Get all transactions filtered by Store
   *
   * @param storeId Store identifier to filter records
   * @return List<Transactions> List of transactions for matching stores
   */
  private List<Transactions> filterTransactionByStoreId(long storeId) {
    return this.transactions.stream().filter(m -> m.getStoreId() == storeId).toList();
  }

  /**
   * Get all transactions filtered by Store & Status
   *
   * @param storeId Store identifier to filter records
   * @return List<Transactions> List of transactions for matching stores
   */
  private List<Transactions> filterTransactionByStoreIdAndStatus(long storeId, String status) {
    return this.transactions.stream()
        .filter(trx -> trx.getStoreId() == storeId && trx.getStatus().equalsIgnoreCase(status))
        .toList();
  }

  /**
   * Get all transactions for a given merchant
   *
   * @param merchantId Merchant identifier to filter records
   * @return List<Transactions> List of transactions for matching stores
   */
  private List<Transactions> filterTransactionByMerchantId(long merchantId) {
    /* first find if the merchant exists, if yes then find stores */
    if (this.merchants.stream().anyMatch(m -> m.getId() == merchantId)) {
      return this.transactions.stream()
          .filter(
              t ->
                  this.stores.stream()
                      .anyMatch(
                          s -> s.getMerchantId() == merchantId && s.getId() == t.getStoreId()))
          .toList();
    } else {
      log.error("Merchant not found!");
      return Collections.emptyList();
    }
  }

  private List<Transactions> filterTransactionByMerchantIdAndStatus(
      long merchantId, String status) {
    if (this.merchants.stream().anyMatch(m -> m.getId() == merchantId)) {
      return this.transactions.stream()
          .filter(
              t ->
                  this.stores.stream()
                      .anyMatch(
                          s ->
                              s.getMerchantId() == merchantId
                                  && s.getId() == t.getStoreId()
                                  && t.getStatus().equalsIgnoreCase(status)))
          .toList();
    } else {
      log.error("Merchant not found!");
      return Collections.emptyList();
    }
  }

  /**
   * Get all stores filtered by Merchant
   *
   * @param merchantId Merchant identifier to filter records
   * @return List<Stores> List of stores for matching merchants
   */
  public List<Stores> fetchStoresByMerchant(long merchantId) {
    SleepUtils.longDelay();
    return this.stores.stream().filter(s -> s.getMerchantId() == merchantId).toList();
  }
}
