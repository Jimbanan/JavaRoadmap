package ru.personal.di.task2.service;

import org.springframework.stereotype.Service;
import ru.personal.di.task2.component.CustomLogger;

@Service
public class AdminService {

    /**
     * Время не меняется, потому что Spring внедряет зависимость (бин CustomLogger) в синглтон-бин (AdminService) только один раз — в момент создания синглтона.
     * Хотя CustomLogger объявлен как prototype, Spring не создает новый экземпляр при каждом вызове метода AdminService.
     */
    private final CustomLogger customLogger;

    public AdminService(CustomLogger customLogger) {
        this.customLogger = customLogger;
    }

    public void execute() {
        System.out.println(AdminService.class.getSimpleName() + ".execute()");
        customLogger.log();
    }

}
