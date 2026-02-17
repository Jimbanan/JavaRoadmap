package ru.personal.di.task2;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import ru.personal.di.task2.service.AdminService;

@SpringBootApplication
public class ConsoleRunner implements CommandLineRunner {

    private final AdminService adminService;

    public ConsoleRunner(AdminService adminService) {
        this.adminService = adminService;
    }

    public static void main(String[] args) {
        SpringApplication config = new SpringApplication(ConsoleRunner.class);
        config.setWebApplicationType(WebApplicationType.NONE);
        config.run(args);
    }

    @Override
    public void run(String... args) {
        for (int i = 0; i < 10; i++) {
            adminService.execute();
        }
    }
}
