package com.company.{{SERVICE_NAME}};

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
@EnableCaching
public class {{SERVICE_NAME_PASCAL}}BffApplication {

    public static void main(String[] args) {
        SpringApplication.run({{SERVICE_NAME_PASCAL}}BffApplication.class, args);
    }
}
