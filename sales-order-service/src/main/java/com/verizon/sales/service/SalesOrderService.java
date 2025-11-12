package com.verizon.sales.service;

import com.verizon.sales.client.CustomerClient;
import com.verizon.sales.client.ItemClient;
import com.verizon.sales.dto.ItemResponse;
import com.verizon.sales.dto.SalesOrderRequest;
import com.verizon.sales.dto.SalesOrderResponse;
import com.verizon.sales.entity.OrderLineItem;
import com.verizon.sales.entity.SalesOrder;
import com.verizon.sales.repository.OrderLineItemRepository;
import com.verizon.sales.repository.SalesOrderRepository;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SalesOrderService {

    private final SalesOrderRepository salesOrderRepository;
    private final OrderLineItemRepository orderLineItemRepository;
    private final CustomerClient customerClient;
    private final ItemClient itemClient;

    @CircuitBreaker(name = "customerServiceCB", fallbackMethod = "fallbackCustomerId")
    public SalesOrder saveOrder(SalesOrderRequest request) {
        Long customerId = customerClient.getCustomerIdByEmail(request.getEmail());

        SalesOrder salesOrder = new SalesOrder();
        salesOrder.setCustomerId(customerId);

        List<OrderLineItem> lineItems = new ArrayList<>();
        double total = 0.0;

        for (String itemName : request.getItemNames()) {
            ItemResponse item = itemClient.getItemByName(itemName);

            OrderLineItem lineItem = new OrderLineItem();
            lineItem.setItemId(item.getId());
            lineItem.setItemPrice(item.getPrice());
            lineItem.setQuantity(1);
            lineItem.setSalesOrder(salesOrder);

            total += item.getPrice();
            lineItems.add(lineItem);
        }

        salesOrder.setOrderLineItems(lineItems);
        salesOrder.setQuantity(total);

        return salesOrderRepository.save(salesOrder);
    }

    @CircuitBreaker(name = "itemServiceCB", fallbackMethod = "fallbackSaveOrder")

    public SalesOrder fallbackCustomerId(SalesOrderRequest request, Throwable t) {
        System.out.println("⚠️ Fallback triggered for customerClient: " + t.getMessage());

        SalesOrder salesOrder = new SalesOrder();
        salesOrder.setCustomerId(-1L); // default/fake ID

        OrderLineItem dummyItem = new OrderLineItem();
        dummyItem.setItemId(-1L);
        dummyItem.setItemPrice(0.0);
        dummyItem.setQuantity(0);
        dummyItem.setSalesOrder(salesOrder);

        salesOrder.setOrderLineItems(Collections.singletonList(dummyItem));
        salesOrder.setQuantity(0.0);

        return salesOrder;
    }

    // 🔁 Fallback for itemClient call
    public SalesOrder fallbackSaveOrder(SalesOrderRequest request, Throwable t) {
        System.out.println("⚠️ Fallback triggered for itemClient: " + t.getMessage());

        SalesOrder salesOrder = new SalesOrder();
        salesOrder.setCustomerId(-1L); // fallback customer

        OrderLineItem dummyItem = new OrderLineItem();
        dummyItem.setItemId(-1L);
        dummyItem.setItemPrice(0.0);
        dummyItem.setQuantity(0);
        dummyItem.setSalesOrder(salesOrder);

        salesOrder.setOrderLineItems(Collections.singletonList(dummyItem));
        salesOrder.setQuantity(0.0);

        return salesOrder;
    }

    public SalesOrderResponse getSalesOrderWithItems(Long id) {
        SalesOrder order = salesOrderRepository.findById(id).orElse(null);
        if (order == null) return null;

        SalesOrderResponse response = new SalesOrderResponse();
        response.setOrderId(order.getId());
        response.setCustomerId(order.getCustomerId());
        response.setItems(order.getOrderLineItems());
        response.setTotalAmount(order.getQuantity());

        return response;
    }

    public List<SalesOrder> getAllOrders() {
        return salesOrderRepository.findAll();
    }

    public SalesOrder getOrderById(Long id) {
        return salesOrderRepository.findById(id).orElse(null);
    }

    public void deleteOrder(Long id) {
        salesOrderRepository.deleteById(id);
    }

    public SalesOrder updateOrder(Long id, SalesOrder updatedOrder) {
        SalesOrder existingOrder = salesOrderRepository.findById(id).orElse(null);
        if (existingOrder == null) return null;

        existingOrder.setCustomerId(updatedOrder.getCustomerId());
        existingOrder.setQuantity(updatedOrder.getQuantity());

        return salesOrderRepository.save(existingOrder);
    }
}
