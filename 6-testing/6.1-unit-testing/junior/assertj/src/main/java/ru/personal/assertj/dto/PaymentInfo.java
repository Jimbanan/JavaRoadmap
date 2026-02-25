package ru.personal.assertj.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class PaymentInfo {
    private String transactionId;
    private String cardLastFour;
    private double amount;
    private LocalDateTime paidAt;
    private boolean success;
}
