package ru.personal.profiles.task1;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import ru.personal.profiles.task1.service.OrderLogger;

import java.util.UUID;

@SpringBootApplication
public class ProfilesApplication {

    private static OrderLogger logger;

    public ProfilesApplication(OrderLogger logger) {
        ProfilesApplication.logger = logger;
    }

    public static void main(String[] args) {
        SpringApplication.run(ProfilesApplication.class, args);

        logger.logOrder(UUID.randomUUID().toString());
    }

}
