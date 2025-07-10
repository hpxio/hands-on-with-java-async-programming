package io.dice28.codelabs.multithreading.client.feign;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import io.dice28.codelabs.multithreading.client.model.Transactions;

@Component
@FeignClient(name = "TransactionReportCallableFeign", url = "${feign-client.url}")
public interface TransactionFeignClient {

  @GetMapping("/api/v1/merchants/merchant{merchant_id}/transactions?status=success")
  public List<Transactions> getSuccessfulTransactionByMerchant(
      @PathVariable(name = "merchant_id") long merchantId);

  @GetMapping("/api/v1/merchants/merchant{merchant_id}/transactions?status=failed")
  public List<Transactions> getFailedTransactionsByMerchant(
      @PathVariable(name = "merchant_id") long merchantId);

  @GetMapping("/api/v1/stores/store{store_id}/transactions?status=success")
  public List<Transactions> getSuccessfulTransactionsByStore(
      @PathVariable(name = "store_id") long storeId);

  @GetMapping("/api/v1/stores/store{store_id}/transactions?status=failed")
  public List<Transactions> getFailedTransactionsByStore(
      @PathVariable(name = "store_id") long storeId);
}
