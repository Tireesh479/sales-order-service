package com.verizon.sales.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "customer-service")
public interface CustomerClient {

    @GetMapping("/api/service1/customers/email/{email}")
    Long getCustomerIdByEmail(@PathVariable("email") String email);

    @GetMapping("/api/service1/customers/{id}")
    String getCustomerById(@PathVariable("id") Long id);
}