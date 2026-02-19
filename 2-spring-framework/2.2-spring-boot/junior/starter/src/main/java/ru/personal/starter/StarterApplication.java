package ru.personal.starter;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class StarterApplication {

    public static void main(String[] args) {
        SpringApplication.run(StarterApplication.class, args);
    }

    /**
     * В разделе Positive matches (положительные совпадения) вы найдете блок, посвященный DispatcherServletAutoConfiguration. Выглядеть он будет примерно так :
     *
     * text
     * Positive matches:
     *     ..........
     *     DispatcherServletAutoConfiguration matched:
     *       - @ConditionalOnWebApplication (required) found StandardServletEnvironment (OnWebApplicationCondition)
     *       - @ConditionalOnClass found required class 'org.springframework.web.servlet.DispatcherServlet' (OnClassCondition)
     * Объяснение условий
     * <p>
     * Строки из отчета четко указывают, почему Spring Boot решил настроить DispatcherServlet:
     *
     * @ConditionalOnClass found required class 'org.springframework.web.servlet.DispatcherServlet'
     * Это главное условие. Spring Boot проверяет, есть ли в classpath (в наборе библиотек проекта) класс DispatcherServlet. Откуда он там берется? Этот класс является частью Spring MVC, который автоматически подтягивается в проект благодаря стартеру spring-boot-starter-web. Раз класс найден — условие совпало (matched), и автоконфигурация может продолжаться.
     * @ConditionalOnWebApplication (required) found StandardServletEnvironment
     * Этим условием Spring Boot проверяет, что мы создаем именно веб-приложение (а не консольное или пакетное). Наличие StandardServletEnvironment в контексте приложения служит для него подтверждением, что это SERVLET-тип веб-приложения (в отличие от реактивного). Условие также совпало.
     */

}
