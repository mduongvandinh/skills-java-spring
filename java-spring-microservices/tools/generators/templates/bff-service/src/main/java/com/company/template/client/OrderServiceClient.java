package com.company.{{SERVICE_NAME}}.client;

import com.company.{{SERVICE_NAME}}.dto.OrderDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "order-service", url = "${services.order-service.url}")
public interface OrderServiceClient {

    @GetMapping("/api/orders")
    List<OrderDto> getOrdersByUserId(@RequestParam Long userId);
}
