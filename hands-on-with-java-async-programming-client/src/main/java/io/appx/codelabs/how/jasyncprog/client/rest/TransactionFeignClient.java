package io.appx.codelabs.how.jasyncprog.client.rest;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import io.appx.codelabs.how.jasyncprog.client.model.Transactions;

@Component
@FeignClient(name = "TransactionReportCallableFeign", url = "${feign-client.url}")
public interface TransactionFeignClient {

  @GetMapping("/by/merchant/{merchant_id}/success")
  public List<Transactions> getSuccessTransactionsForMerchant(
      @PathVariable(name = "merchant_id") long merchantId);

  @GetMapping("/by/store/{store_id}/success")
  public List<Transactions> getFailedTransactionsForStore(
      @PathVariable(name = "store_id") long storeId);
}
