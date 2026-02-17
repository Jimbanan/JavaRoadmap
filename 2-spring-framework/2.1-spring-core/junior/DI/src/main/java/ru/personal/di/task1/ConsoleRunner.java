package ru.personal.di.task1;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import ru.personal.di.task1.service.OrderProcessor;

@SpringBootApplication
public class ConsoleRunner implements CommandLineRunner {

    private final OrderProcessor orderProcessor;

    public ConsoleRunner(OrderProcessor orderProcessor) {
        this.orderProcessor = orderProcessor;
    }

    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(ConsoleRunner.class);
        app.setWebApplicationType(WebApplicationType.NONE);
        app.run(args);
    }

    @Override
    public void run(String... args) {
        orderProcessor.processOrder();
    }
}
