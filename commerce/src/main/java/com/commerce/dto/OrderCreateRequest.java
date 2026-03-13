package com.commerce.dto;

import java.util.List;

public record OrderCreateRequest(Long memberId, List<OrderItem> orderItems) {
    public record OrderItem(Long productId, int quantity) {
    }
}
