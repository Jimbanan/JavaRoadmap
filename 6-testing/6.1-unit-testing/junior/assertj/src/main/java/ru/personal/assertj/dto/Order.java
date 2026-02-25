package ru.personal.assertj.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
public class Order {
    private String id;
    private String customerEmail;
    private List<OrderItem> items = new ArrayList<>();
    private OrderStatus status;
    private LocalDateTime createdAt;
    private double totalAmount;
}
