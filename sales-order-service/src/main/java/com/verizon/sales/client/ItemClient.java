package com.verizon.sales.client;

import com.verizon.sales.dto.ItemResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "item-service")
public interface ItemClient {

    @GetMapping("/items/{id}")
    String getItemById(@PathVariable("id") Long id);

    @GetMapping("/items/name/{name}")
    ItemResponse getItemByName(@PathVariable("name") String name);
}
