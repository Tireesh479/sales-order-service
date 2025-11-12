package com.verizon.sales.dto;

import lombok.Data;

@Data
public class ItemResponse {
    private Long id;
    private String name;
    private double price;
}
