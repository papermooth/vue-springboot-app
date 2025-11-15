package com.example.demo.service;

import com.example.demo.model.Order;
import com.example.demo.model.OrderItem;
import java.util.List;
import java.util.Optional;

public interface OrderService {

    List<Order> getAllOrders();

    Optional<Order> getOrderById(Long id);

    Optional<Order> getOrderByOrderNo(String orderNo);

    List<Order> getOrdersByUserId(Long userId);

    List<Order> getOrdersByStatus(String status);

    Order createOrder(Order order, List<OrderItem> items);

    Order updateOrderStatus(Long id, String status);

    void cancelOrder(Long id);

    List<OrderItem> getOrderItemsByOrderId(Long orderId);
}