package ru.personal.di.task2.component;

import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@Scope("prototype")
public class CustomLogger {

    private LocalDateTime dateTime;

    @PostConstruct
    private void init() {
        dateTime = LocalDateTime.now();
        System.out.println("CustomLogger.init. Time: " + dateTime);
    }

    public void log() {
        System.out.println("CustomLogger.log [" + this.hashCode() + "]: " + dateTime);
    }

}
