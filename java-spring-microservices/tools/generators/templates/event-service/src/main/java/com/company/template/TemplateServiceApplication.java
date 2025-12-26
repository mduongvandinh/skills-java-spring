package com.company.{{SERVICE_NAME}};

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.kafka.annotation.EnableKafka;

@SpringBootApplication
@EnableKafka
public class {{SERVICE_NAME_PASCAL}}ServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run({{SERVICE_NAME_PASCAL}}ServiceApplication.class, args);
    }
}
