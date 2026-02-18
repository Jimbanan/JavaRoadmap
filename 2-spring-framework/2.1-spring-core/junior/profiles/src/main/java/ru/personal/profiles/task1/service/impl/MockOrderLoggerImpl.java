package ru.personal.profiles.task1.service.impl;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import ru.personal.profiles.task1.service.OrderLogger;

@Profile("default")
@Service
public class MockOrderLoggerImpl implements OrderLogger {

    @Override
    public void logOrder(String orderId) {
        System.out.println("MockOrderLogger logOrder");
    }

}
