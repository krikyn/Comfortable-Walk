package com.netcracker.routebuilder;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(scanBasePackages = "com.netcracker")
@EnableJpaRepositories(basePackages = {"com.netcracker.*"})
@EntityScan("com.netcracker.*")
@EnableScheduling
public class RouteBuilderApplication {
    public static void main(String[] args) {

        //включение визуального интерфейса, для тестирования потенциальных карт
        new SpringApplicationBuilder(RouteBuilderApplication.class).headless(false).run(args);

        //вариант для сервера
        //SpringApplication.run(RouteBuilderApplication.class, args).;
    }
}
