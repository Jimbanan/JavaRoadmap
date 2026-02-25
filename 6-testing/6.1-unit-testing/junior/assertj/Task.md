
### Контекст
Ты работаешь в финтех-стартапе. Команда переходит на современные практики тестирования и приняла решение отказаться от стандартных JUnit Assertions в пользу более выразительных утверждений. Твоя задача — переписать существующие тесты на **AssertJ** (как современный стандарт).

### Бизнес-требования
У нас есть система обработки заказов. Сущности:

```java
// Упрощенная модель для задания
public class Order {
    private String id;
    private String customerEmail;
    private List<OrderItem> items = new ArrayList<>();
    private OrderStatus status;
    private LocalDateTime createdAt;
    private double totalAmount;
    
    // геттеры/сеттеры...
}

public class OrderItem {
    private String productId;
    private String productName;
    private int quantity;
    private double price;
    private double discount; // 0.0 - 1.0
}

public enum OrderStatus {
    NEW, PAID, SHIPPED, DELIVERED, CANCELLED
}

public class PaymentInfo {
    private String transactionId;
    private String cardLastFour;
    private double amount;
    private LocalDateTime paidAt;
    private boolean success;
}
```

```java
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
```

### Рефакторинг на AssertJ

У тебя есть устаревший тестовый класс, использующий JUnit Assertions. Перепиши его полностью на AssertJ, сохраняя ту же логику проверок.

**Устаревший тест (код для рефакторинга):**
```java
class OrderServiceLegacyTest {
    private OrderService orderService;
    private Order testOrder;
    
    @BeforeEach
    void setUp() {
        orderService = new OrderService();
        
        OrderItem item1 = new OrderItem("P1", "Laptop", 1, 1000.0, 0.1);
        OrderItem item2 = new OrderItem("P2", "Mouse", 2, 25.0, 0.0);
        
        testOrder = orderService.createOrder(
            "customer@example.com",
            Arrays.asList(item1, item2)
        );
    }
    
    @Test
    void testOrderCreation() {
        assertNotNull(testOrder);
        assertNotNull(testOrder.getId());
        assertEquals("customer@example.com", testOrder.getCustomerEmail());
        assertEquals(OrderStatus.NEW, testOrder.getStatus());
        assertNotNull(testOrder.getCreatedAt());
        assertTrue(testOrder.getCreatedAt().isBefore(LocalDateTime.now()));
    }
    
    @Test
    void testOrderTotalCalculation() {
        double total = orderService.calculateTotal(testOrder);
        
        // Laptop: 1000 * 0.9 = 900
        // Mouse: 25 * 2 = 50
        // Итого: 950
        assertEquals(950.0, total, 0.001);
        assertEquals(950.0, testOrder.getTotalAmount(), 0.001);
    }
    
    @Test
    void testOrderItemsValidation() {
        List<OrderItem> items = testOrder.getItems();
        
        assertNotNull(items);
        assertEquals(2, items.size());
        
        OrderItem laptop = items.get(0);
        assertEquals("P1", laptop.getProductId());
        assertEquals("Laptop", laptop.getProductName());
        assertEquals(1, laptop.getQuantity());
        assertEquals(1000.0, laptop.getPrice());
        assertEquals(0.1, laptop.getDiscount());
        
        OrderItem mouse = items.get(1);
        assertEquals("P2", mouse.getProductId());
        assertEquals(2, mouse.getQuantity());
        assertEquals(25.0, mouse.getPrice());
    }
    
    @Test
    void testPaymentProcessing() {
        PaymentInfo payment = orderService.processPayment(testOrder.getId(), "4111111111111111");
        
        assertNotNull(payment);
        assertNotNull(payment.getTransactionId());
        assertTrue(payment.getTransactionId().startsWith("TXN"));
        assertEquals("1111", payment.getCardLastFour());
        assertEquals(950.0, payment.getAmount());
        assertTrue(payment.isSuccess());
        assertNotNull(payment.getPaidAt());
        
        // После оплаты статус заказа должен измениться
        assertNotNull(orderService.getOrder(testOrder.getId()));
        assertEquals(OrderStatus.PAID, orderService.getOrder(testOrder.getId()).getStatus());
    }
    
    @Test
    void testOrderCancellationValidation() {
        // Пытаемся отменить оплаченный заказ
        orderService.processPayment(testOrder.getId(), "4111111111111111");
        
        Exception exception = assertThrows(IllegalStateException.class, () -> {
            orderService.cancelOrder(testOrder.getId());
        });
        
        assertEquals("Нельзя отменить оплаченный заказ", exception.getMessage());
    }
    
    @Test
    void testEmptyOrderCreation() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            orderService.createOrder("customer@example.com", Collections.emptyList());
        });
        
        assertEquals("Заказ должен содержать хотя бы один товар", exception.getMessage());
    }
    
    @Test
    void testBulkOrderValidation() {
        List<Order> orders = orderService.findOrdersByDateRange(
            LocalDateTime.now().minusDays(1),
            LocalDateTime.now().plusDays(1)
        );
        
        assertNotNull(orders);
        assertFalse(orders.isEmpty());
        
        // Проверяем, что все заказы в периоде
        for (Order order : orders) {
            assertNotNull(order);
            assertNotNull(order.getId());
            assertTrue(order.getCreatedAt().isAfter(LocalDateTime.now().minusDays(2)));
            assertTrue(order.getTotalAmount() > 0);
        }
    }
}
```

