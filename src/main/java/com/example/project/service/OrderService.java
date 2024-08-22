package com.example.project.service;

import com.example.project.model.entity.Order;
import java.util.List;
import java.util.Optional;

public interface OrderService {
    Order createOrder(Order order);
    List<Order> findAllOrders();
    Optional<Order> findOrderById(Long id);
    Order updateOrder(Long id, Order updatedOrder);
    void deleteOrder(Long id);
}
