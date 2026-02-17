package ru.personal.di.task1.service.impl;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import ru.personal.di.task1.service.NotificationService;

@Primary
@Service("emailNotificationServiceImpl")
public class EmailNotificationServiceImpl implements NotificationService {

    @Override
    public void notification() {
        System.out.println("Email notification");
    }
}
