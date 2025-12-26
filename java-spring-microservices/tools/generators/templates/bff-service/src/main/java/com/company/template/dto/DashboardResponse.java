package com.company.{{SERVICE_NAME}}.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardResponse {
    private UserDto user;
    private List<OrderDto> recentOrders;
    private String errorMessage;
}
