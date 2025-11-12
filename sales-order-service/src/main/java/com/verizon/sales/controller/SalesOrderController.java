package com.verizon.sales.controller;

import com.verizon.sales.client.CustomerClient;
import com.verizon.sales.client.ItemClient;
import com.verizon.sales.dto.SalesOrderRequest;
import com.verizon.sales.dto.SalesOrderResponse;
import com.verizon.sales.entity.SalesOrder;
import com.verizon.sales.repository.SalesOrderRepository;
import com.verizon.sales.service.SalesOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/service3/orders")
@RequiredArgsConstructor
public class SalesOrderController {

    private final SalesOrderService salesOrderService;
    private final SalesOrderRepository salesOrderRepository;
    private final CustomerClient customerClient;
    private final ItemClient itemClient;

    //  Return only Order ID
    @PostMapping
    public ResponseEntity<Long> createOrder(@RequestBody SalesOrderRequest salesOrderRequest) {
        SalesOrder savedOrder = salesOrderService.saveOrder(salesOrderRequest);
        return ResponseEntity.ok(savedOrder.getId());
    }

    // ✅ GET: All orders (raw list)
    @GetMapping
    public List<SalesOrder> getAllOrders() {
        return salesOrderService.getAllOrders();
    }

   /* // Single order with line items (DTO response)
    @GetMapping("/{id}")
    public ResponseEntity<SalesOrderResponse> getOrderById(@PathVariable Long id) {
        SalesOrderResponse response = salesOrderService.getSalesOrderWithItems(id);
        if (response == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(response);
    }

    */

    @GetMapping("/{id}/dto")
    public ResponseEntity<SalesOrderResponse> getOrderWithItems(@PathVariable Long id) {
        SalesOrderResponse response = salesOrderService.getSalesOrderWithItems(id);
        if (response == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(response);
    }

    // ✅ DELETE: Delete by ID
    @DeleteMapping("/{id}")
    public void deleteOrder(@PathVariable Long id) {
        salesOrderService.deleteOrder(id);
    }
    // ✅ PUT: Update existing order
    @PutMapping("/{id}")
    public ResponseEntity<SalesOrder> updateOrder(@PathVariable Long id, @RequestBody SalesOrder updatedOrder) {
        return salesOrderRepository.findById(id)
                .map(existingOrder -> {
                    existingOrder.setCustomerId(updatedOrder.getCustomerId());
                    existingOrder.setQuantity(updatedOrder.getQuantity());
                    SalesOrder saved = salesOrderRepository.save(existingOrder);
                    return ResponseEntity.ok(saved);
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
    // (Optional) BONUS DETAILS ENDPOINT - REMOVE IF NOT NEEDED
    @GetMapping("/{id}")
    public String getOrderDetails(@PathVariable Long id) {
        SalesOrder order = salesOrderService.getOrderById(id);
        if (order == null) return "Order not found.";
        String customer = customerClient.getCustomerById(order.getCustomerId());
        String item = itemClient.getItemById(order.getId());
        return "Customer: " + customer + " | Item: " + item + " | Quantity: " + order.getQuantity();
    }
}
