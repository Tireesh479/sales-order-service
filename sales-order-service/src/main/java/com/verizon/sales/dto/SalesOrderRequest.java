package com.verizon.sales.dto;

import lombok.Data;
import java.util.List;

@Data
public class SalesOrderRequest {
    private String email;
    private List<String> itemNames;
}
