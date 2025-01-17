package io.appx.codelabs.how.jasyncprog.server.repository;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.appx.codelabs.how.jasyncprog.server.model.Merchants;
import io.appx.codelabs.how.jasyncprog.server.model.Stores;
import io.appx.codelabs.how.jasyncprog.server.model.Transactions;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Repository
public class DataLoaderRepository {

    private static final String SUCCESS_STATUS = "success";
    private static final String FAILED_STATUS = "failed";

    @Value("classpath:data/transactions.json")
    private String transactionsPath;

    @Value("classpath:data/merchants.json")
    private String merchantsPath;

    @Value("classpath:data/stores.json")
    private String storesPath;

    @Getter
    private List<Transactions> transactions;

    @Getter
    private List<Merchants> merchants;

    @Getter
    private List<Stores> stores;

    @PostConstruct
    private void load() {
        ObjectMapper mapper = new ObjectMapper();
        try {
            File transactionsFile = new File(transactionsPath);
            File merchantsFile = new File(merchantsPath);
            File storesFile = new File(storesPath);

            if (transactionsFile.exists() && merchantsFile.exists() && storesFile.exists()) {
                transactions = mapper.readValue(transactionsFile, new TypeReference<>() {
                });
                merchants = mapper.readValue(merchantsFile, new TypeReference<>() {
                });
                stores = mapper.readValue(storesFile, new TypeReference<>() {
                });
            } else {
                log.warn("No seed data found!");
            }
        } catch (IOException e) {
            log.error("Cannot load seed data : {}", e.getMessage());
        }
    }

    public List<Transactions> getTransactionsByStoreId(long storeId) {
        return this.transactions.stream().filter(m -> m.getStoreId() == storeId).toList();
    }

    public List<Stores> getStoresByMerchantId(long merchantId) {
        return this.stores.stream().filter(s -> s.getMerchantId() == merchantId).toList();
    }

    public List<Transactions> getFailedTransactionsByStoreId(long storeId) {
        return this.transactions.stream()
                .filter(trx -> trx.getStoreId() == storeId &&
                        trx.getStatus().equalsIgnoreCase(FAILED_STATUS))
                .toList();
    }

    public List<Transactions> getSuccessTransactionsByStoreId(long storeId) {
        return this.transactions.stream()
                .filter(trx -> trx.getStoreId() == storeId &&
                        trx.getStatus().equalsIgnoreCase(SUCCESS_STATUS))
                .toList();
    }

    /* Get all transactions under a given merchant */
    public List<Transactions> getAllTransactionByMerchantid(long merchantId) {
        /* first find if the merchant exists, if yes then find stores */
        if (this.merchants.stream().anyMatch(m -> m.getId() == merchantId)) {
            return this.transactions.stream()
                    .filter(t -> this.stores.stream()
                            .anyMatch(s -> s.getMerchantId() == merchantId && s.getId() == t.getStoreId()))
                    .toList();
        } else {
            log.error("Merchant not found!");
            return Collections.emptyList();
        }
    }

    /* Get all successful transactions under a given merchant */
    public List<Transactions> getSuccessTransactionByMerchantid(long merchantId) {
        if (this.merchants.stream().anyMatch(m -> m.getId() == merchantId)) {
            return this.transactions.stream()
                    .filter(t -> this.stores.stream()
                            .anyMatch(s -> s.getMerchantId() == merchantId && s.getId() == t.getStoreId()
                                    && t.getStatus().equalsIgnoreCase(SUCCESS_STATUS)))
                    .toList();
        } else {
            log.error("Merchant not found!");
            return Collections.emptyList();
        }
    }

    /* Get all successful transactions under a given merchant */
    public List<Transactions> getFailedTransactionByMerchantid(long merchantId) {
        if (this.merchants.stream().anyMatch(m -> m.getId() == merchantId)) {
            return this.transactions.stream()
                    .filter(t -> this.stores.stream()
                            .anyMatch(s -> s.getMerchantId() == merchantId && s.getId() == t.getStoreId()
                                    && t.getStatus().equalsIgnoreCase(FAILED_STATUS)))
                    .toList();
        } else {
            log.error("Merchant not found!");
            return Collections.emptyList();
        }
    }
}
