package ru.personal.di.task1.service;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class OrderProcessor {

    private final NotificationService notificationService;

    public OrderProcessor(@Qualifier("smsNotificationServiceImpl") NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    public void processOrder() {
        notificationService.notification();
    }
}
