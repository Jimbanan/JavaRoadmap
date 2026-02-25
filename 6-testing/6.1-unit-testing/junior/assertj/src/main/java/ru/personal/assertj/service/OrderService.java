package ru.personal.assertj.service;

import ru.personal.assertj.dto.Order;
import ru.personal.assertj.dto.OrderItem;
import ru.personal.assertj.dto.OrderStatus;
import ru.personal.assertj.dto.PaymentInfo;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

public class OrderService {
    private final Map<String, Order> orders = new HashMap<>();
    private final AtomicLong transactionCounter = new AtomicLong(1000);
    private final AtomicLong orderCounter = new AtomicLong(1);

    /**
     * Создает новый заказ
     */
    public Order createOrder(String customerEmail, List<OrderItem> items) {
        if (items == null || items.isEmpty()) {
            throw new IllegalArgumentException("Заказ должен содержать хотя бы один товар");
        }

        if (customerEmail == null || customerEmail.trim().isEmpty()) {
            throw new IllegalArgumentException("Email клиента не может быть пустым");
        }

        Order order = new Order();
        order.setId(generateOrderId());
        order.setCustomerEmail(customerEmail);
        order.setItems(new ArrayList<>(items));
        order.setStatus(OrderStatus.NEW);
        order.setCreatedAt(LocalDateTime.now());

        double total = calculateTotal(order);
        order.setTotalAmount(total);

        orders.put(order.getId(), order);
        return order;
    }

    /**
     * Рассчитывает общую стоимость заказа с учетом скидок
     */
    public double calculateTotal(Order order) {
        if (order == null || order.getItems() == null) {
            return 0.0;
        }

        return order.getItems().stream()
                .mapToDouble(item -> {
                    double itemPrice = item.getPrice() * item.getQuantity();
                    double discountAmount = itemPrice * item.getDiscount();
                    return itemPrice - discountAmount;
                })
                .sum();
    }

    /**
     * Обрабатывает оплату заказа
     */
    public PaymentInfo processPayment(String orderId, String cardNumber) {
        Order order = getOrder(orderId);
        if (order == null) {
            throw new IllegalArgumentException("Заказ не найден");
        }

        if (order.getStatus() != OrderStatus.NEW) {
            throw new IllegalStateException("Заказ уже оплачен или отменен");
        }

        // Валидация номера карты (упрощенно)
        if (cardNumber == null || cardNumber.length() < 4) {
            throw new IllegalArgumentException("Неверный номер карты");
        }

        PaymentInfo payment = new PaymentInfo();
        payment.setTransactionId(generateTransactionId());
        payment.setCardLastFour(cardNumber.substring(cardNumber.length() - 4));
        payment.setAmount(order.getTotalAmount());
        payment.setPaidAt(LocalDateTime.now());
        payment.setSuccess(true);

        // Обновляем статус заказа
        order.setStatus(OrderStatus.PAID);
        orders.put(orderId, order);

        return payment;
    }

    /**
     * Отменяет заказ
     */
    public void cancelOrder(String orderId) {
        Order order = getOrder(orderId);

        if (order.getStatus() == OrderStatus.PAID) {
            throw new IllegalStateException("Нельзя отменить оплаченный заказ");
        }

        order.setStatus(OrderStatus.CANCELLED);
        orders.put(orderId, order);
    }

    /**
     * Возвращает заказ по ID
     */
    public Order getOrder(String orderId) {
        return orders.get(orderId);
    }

    /**
     * Находит заказы в указанном диапазоне дат
     */
    public List<Order> findOrdersByDateRange(LocalDateTime start, LocalDateTime end) {
        if (start == null || end == null) {
            throw new IllegalArgumentException("Даты начала и конца периода должны быть указаны");
        }

        if (start.isAfter(end)) {
            throw new IllegalArgumentException("Дата начала не может быть позже даты конца");
        }

        return orders.values().stream()
                .filter(order -> {
                    LocalDateTime createdAt = order.getCreatedAt();
                    return createdAt != null &&
                            !createdAt.isBefore(start) &&
                            !createdAt.isAfter(end);
                })
                .sorted(Comparator.comparing(Order::getCreatedAt))
                .collect(Collectors.toList());
    }


    private String generateOrderId() {
        return "ORD-" + String.format("%06d", orderCounter.getAndIncrement());
    }

    private String generateTransactionId() {
        return "TXN" + System.currentTimeMillis() + "-" + transactionCounter.getAndIncrement();
    }
}