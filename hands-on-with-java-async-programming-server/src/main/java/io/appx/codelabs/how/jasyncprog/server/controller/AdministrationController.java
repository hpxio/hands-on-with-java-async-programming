package io.appx.codelabs.how.jasyncprog.server.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

import io.appx.codelabs.how.jasyncprog.server.model.Stores;
import io.appx.codelabs.how.jasyncprog.server.repository.DataLoaderRepository;

@Slf4j
@RestController
@RequestMapping("/mgmt/v1/admin")
public class AdministrationController {

  private final DataLoaderRepository repository;

  public AdministrationController(DataLoaderRepository repository) {
    this.repository = repository;
  }

  @GetMapping("/stores/by/merchant/{merchant_id}")
  public List<Stores> getAllStoredByMerchant(@PathVariable(name = "merchant_id") long merchantId) {
    log.info("List stores by merchant Id : {}", merchantId);
    return repository.getStoresByMerchantId(merchantId);
  }
}
