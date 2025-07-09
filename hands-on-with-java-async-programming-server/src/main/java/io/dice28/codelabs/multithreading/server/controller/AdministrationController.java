package io.dice28.codelabs.multithreading.server.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.dice28.codelabs.multithreading.server.model.Stores;
import io.dice28.codelabs.multithreading.server.repository.DataLoaderRepository;
import lombok.extern.slf4j.Slf4j;

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
