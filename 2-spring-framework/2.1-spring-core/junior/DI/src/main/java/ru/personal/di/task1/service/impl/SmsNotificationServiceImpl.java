package ru.personal.di.task1.service.impl;

import org.springframework.stereotype.Service;
import ru.personal.di.task1.service.NotificationService;

@Service("smsNotificationServiceImpl")
public class SmsNotificationServiceImpl implements NotificationService {

    @Override
    public void notification() {
        System.out.println("Sms notification");
    }
}
