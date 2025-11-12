package com.verizon.sales.dto;

import com.verizon.sales.entity.OrderLineItem;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SalesOrderResponse {

    private Long orderId;
    private Long customerId;
    private List<OrderLineItem> items;
    private double totalAmount;
}
