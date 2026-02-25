package ru.personal.assertj.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.personal.assertj.dto.Order;
import ru.personal.assertj.dto.OrderItem;
import ru.personal.assertj.dto.OrderStatus;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

class OrderServiceLegacyTest {
    private OrderService orderService;
    private Order testOrder;

    @BeforeEach
    void setUp() {
        orderService = new OrderService();

        var item1 = new OrderItem("P1", "Laptop", 1, 1000.0, 0.1);
        var item2 = new OrderItem("P2", "Mouse", 2, 25.0, 0.0);

        testOrder = orderService.createOrder(
                "customer@example.com",
                Arrays.asList(item1, item2)
        );
    }

    @Test
    void orderCreation_whenDataCorrect_thenOrderCreated() {
        assertThat(testOrder)
                .isNotNull()
                .satisfies(order -> {
                    assertThat(order.getId())
                            .isNotNull();
                    assertThat(order.getCustomerEmail())
                            .isNotNull()
                            .isEqualTo("customer@example.com");
                    assertThat(order.getStatus())
                            .isNotNull()
                            .isEqualTo(OrderStatus.NEW);
                    assertThat(order.getCreatedAt())
                            .isNotNull()
                            .isBefore(LocalDateTime.now());
                });
    }

    @Test
    void calculateTotal_whenDataCorrect_thenReturnCorrectTotal() {
        //given
        //when
        double total = orderService.calculateTotal(testOrder);
        //then
        assertThat(total)
                .isEqualTo(950.0)
                .isEqualTo(testOrder.getTotalAmount());
    }

    @Test
    void calculateTotal_whenOrderIsNull_thenReturnZero() {
        //given
        //when
        double total = orderService.calculateTotal(null);
        //then
        assertThat(total)
                .isZero();
    }

    @Test
    void calculateTotal_whenOrderItemsIsNull_thenReturnZero() {
        //given
        testOrder.setItems(null);
        //when
        double total = orderService.calculateTotal(testOrder);
        //then
        assertThat(total)
                .isZero();
    }

    @Test
    void orderItemsValidation_whenDataCorrect_thenReturnItems() {
        //given
        var items = testOrder.getItems();
        //when
        //then
        assertThat(items)
                .isNotNull()
                .hasSize(2)
                .satisfies(orderItem -> {
                    assertThat(orderItem.get(0))
                            .isNotNull()
                            .extracting(
                                    OrderItem::getProductId,
                                    OrderItem::getProductName,
                                    OrderItem::getQuantity,
                                    OrderItem::getPrice,
                                    OrderItem::getDiscount
                            )
                            .containsExactly(
                                    "P1",
                                    "Laptop",
                                    1,
                                    1000.0,
                                    0.1
                            );
                    assertThat(orderItem.get(1))
                            .isNotNull()
                            .extracting(
                                    OrderItem::getProductId,
                                    OrderItem::getQuantity,
                                    OrderItem::getPrice
                            )
                            .containsExactly(
                                    "P2",
                                    2,
                                    25.0
                            );
                });
    }

    @Test
    void processPayment_whenDataCorrect_thenUpdateOrderStatus() {
        //given
        var payment = orderService.processPayment(testOrder.getId(), "4111111111111111");
        var order = orderService.getOrder(testOrder.getId());
        //when
        //then
        assertThat(payment)
                .isNotNull()
                .satisfies(paymentInfo -> {

                    assertThat(paymentInfo.getTransactionId())
                            .isNotNull()
                            .startsWith("TXN");

                    assertThat(paymentInfo.getCardLastFour())
                            .isNotNull()
                            .isEqualTo("1111");

                    assertThat(paymentInfo.getAmount())
                            .isEqualTo(950.0);

                    assertThat(paymentInfo.getPaidAt())
                            .isNotNull();

                    assertThat(paymentInfo.isSuccess()).isTrue();

                });

        assertThat(order)
                .isNotNull()
                .extracting(Order::getStatus)
                .isEqualTo(OrderStatus.PAID);
    }

    @Test
    void processPayment_whenOrderNotFind_thenException() {
        //given
        //when
        //then
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> orderService.processPayment(UUID.randomUUID().toString(), "4111111111111111"))
                .withMessage("Заказ не найден");
    }

    @Test
    void processPayment_whenOrderNotNew_thenException() {
        //given
        testOrder.setStatus(OrderStatus.DELIVERED);
        //when
        //then
        assertThatExceptionOfType(IllegalStateException.class)
                .isThrownBy(() -> orderService.processPayment(testOrder.getId(), "4111111111111111"))
                .withMessage("Заказ уже оплачен или отменен");
    }

    @Test
    void processPayment_whenCardNumberNotValid_thenException() {
        //given
        //when
        //then
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> orderService.processPayment(testOrder.getId(), null))
                .withMessage("Неверный номер карты");
    }

    @Test
    void processPayment_whenCancelPaymentOrder_thenException() {
        //given
        //when
        orderService.processPayment(testOrder.getId(), "4111111111111111");
        //then
        assertThatExceptionOfType(IllegalStateException.class)
                .isThrownBy(() -> orderService.cancelOrder(testOrder.getId()))
                .withMessage("Нельзя отменить оплаченный заказ");
    }

    @Test
    void createOrder_whenItemsIsEmpty_thenException() {
        //given
        //when
        //then
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> orderService.createOrder("customer@example.com", Collections.emptyList()))
                .withMessage("Заказ должен содержать хотя бы один товар");
    }

    @Test
    void createOrder_whenEmailIsNull_thenException() {
        //given
        //when
        //then
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> orderService.createOrder(null, List.of(new OrderItem(), new OrderItem())))
                .withMessage("Email клиента не может быть пустым");
    }

    @Test
    void findOrdersByDateRange_whenDataCorrect_thenReturnOrders() {
        //given
        //when
        var orders = orderService.findOrdersByDateRange(
                LocalDateTime.now().minusDays(1),
                LocalDateTime.now().plusDays(1)
        );
        //then
        assertThat(orders)
                .isNotEmpty()
                .allSatisfy(order -> {
                    assertThat(order)
                            .isNotNull();

                    assertThat(order.getId())
                            .isNotNull();

                    assertThat(order.getCreatedAt())
                            .isAfter(LocalDateTime.now().minusDays(2));

                    assertThat(order.getTotalAmount())
                            .isGreaterThan(0.0);

                });
    }

    @Test
    void findOrdersByDateRange_whenStartIsNull_thenException() {
        //given
        //when
        //then
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> orderService.findOrdersByDateRange(null, LocalDateTime.now()))
                .withMessage("Даты начала и конца периода должны быть указаны");
    }

    @Test
    void findOrdersByDateRange_whenStartIsAfterEnd_thenException() {
        //given
        //when
        //then
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> orderService.findOrdersByDateRange(LocalDateTime.now().plusDays(1), LocalDateTime.now()))
                .withMessage("Дата начала не может быть позже даты конца");
    }

    @Test
    void cancelOrder_whenDataCorrect_thenReturnCancelOrder() {
        //given
        //when
        orderService.cancelOrder(testOrder.getId());
        var order = orderService.getOrder(testOrder.getId());
        //then
        assertThat(order)
                .isNotNull()
                .extracting(Order::getStatus)
                .isEqualTo(OrderStatus.CANCELLED);
    }
}