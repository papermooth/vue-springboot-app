package com.example.demo.service.impl;

import com.example.demo.model.Order;
import com.example.demo.model.OrderItem;
import com.example.demo.model.Product;
import com.example.demo.repository.OrderItemRepository;
import com.example.demo.repository.OrderRepository;
import com.example.demo.repository.ProductRepository;
import com.example.demo.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private ProductRepository productRepository;

    @Override
    @Cacheable(value = "orders")
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    @Override
    @Cacheable(value = "order", key = "#id")
    public Optional<Order> getOrderById(Long id) {
        return orderRepository.findById(id);
    }

    @Override
    public Optional<Order> getOrderByOrderNo(String orderNo) {
        return orderRepository.findByOrderNo(orderNo);
    }

    @Override
    @Cacheable(value = "userOrders", key = "#userId")
    public List<Order> getOrdersByUserId(Long userId) {
        return orderRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    @Override
    @Cacheable(value = "ordersByStatus", key = "#status")
    public List<Order> getOrdersByStatus(String status) {
        return orderRepository.findByStatus(status);
    }

    @Override
    @Transactional
    @CacheEvict(value = {"orders", "userOrders", "ordersByStatus"}, allEntries = true)
    public Order createOrder(Order order, List<OrderItem> items) {
        // 生成订单号
        String orderNo = generateOrderNo();
        order.setOrderNo(orderNo);
        order.setStatus("PENDING");
        order.setCreatedAt(new Date());
        order.setUpdatedAt(new Date());

        // 保存订单
        Order savedOrder = orderRepository.save(order);

        // 保存订单项并更新库存
        for (OrderItem item : items) {
            item.setOrder(savedOrder);
            
            // 检查库存
            Product product = productRepository.findById(item.getProduct().getId())
                    .orElseThrow(() -> new RuntimeException("Product not found"));
            
            if (product.getStock() < item.getQuantity()) {
                throw new RuntimeException("Insufficient stock for product: " + product.getName());
            }
            
            // 更新库存
            product.setStock(product.getStock() - item.getQuantity());
            productRepository.save(product);
            
            orderItemRepository.save(item);
        }

        return savedOrder;
    }

    @Override
    @CachePut(value = "order", key = "#id")
    public Order updateOrderStatus(Long id, String status) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        
        order.setStatus(status);
        order.setUpdatedAt(new Date());
        
        return orderRepository.save(order);
    }

    @Override
    @Transactional
    @CacheEvict(value = {"order", "orders", "userOrders", "ordersByStatus"}, key = "#id", allEntries = true)
    public void cancelOrder(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        
        // 只有待付款订单可以取消
        if ("PENDING".equals(order.getStatus())) {
            order.setStatus("CANCELLED");
            order.setUpdatedAt(new Date());
            orderRepository.save(order);
            
            // 恢复库存
            List<OrderItem> items = orderItemRepository.findByOrder(order);
            for (OrderItem item : items) {
                Product product = productRepository.findById(item.getProduct().getId()).get();
                product.setStock(product.getStock() + item.getQuantity());
                productRepository.save(product);
            }
        } else {
            throw new RuntimeException("Only PENDING orders can be cancelled");
        }
    }

    @Override
    @Cacheable(value = "orderItems", key = "#orderId")
    public List<OrderItem> getOrderItemsByOrderId(Long orderId) {
        return orderItemRepository.findByOrderId(orderId);
    }

    private String generateOrderNo() {
        return "ORD" + UUID.randomUUID().toString().replace("-", "").substring(0, 10).toUpperCase();
    }
}