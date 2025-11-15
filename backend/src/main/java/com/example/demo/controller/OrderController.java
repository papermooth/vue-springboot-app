package com.example.demo.controller;

import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.model.Order;
import com.example.demo.model.OrderItem;
import com.example.demo.model.Product;
import com.example.demo.model.User;
import com.example.demo.service.OrderService;
import com.example.demo.util.ResponseUtil;
import java.math.BigDecimal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @GetMapping
    public ResponseEntity<?> getAllOrders() {
        List<Order> orders = orderService.getAllOrders();
        return ResponseUtil.success(orders);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getOrdersByUserId(@PathVariable Long userId) {
        List<Order> orders = orderService.getOrdersByUserId(userId);
        return ResponseUtil.success(orders);
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<?> getOrdersByStatus(@PathVariable String status) {
        List<Order> orders = orderService.getOrdersByStatus(status);
        return ResponseUtil.success(orders);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getOrderById(@PathVariable Long id) {
        Order order = orderService.getOrderById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + id));
        
        // 获取订单项
        List<OrderItem> items = orderService.getOrderItemsByOrderId(id);
        Map<String, Object> response = new HashMap<>();
        response.put("order", order);
        response.put("items", items);
        
        return ResponseUtil.success(response);
    }

    @GetMapping("/orderNo/{orderNo}")
    public ResponseEntity<?> getOrderByOrderNo(@PathVariable String orderNo) {
        Order order = orderService.getOrderByOrderNo(orderNo)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with orderNo: " + orderNo));
        
        // 获取订单项
        List<OrderItem> items = orderService.getOrderItemsByOrderId(order.getId());
        Map<String, Object> response = new HashMap<>();
        response.put("order", order);
        response.put("items", items);
        
        return ResponseUtil.success(response);
    }

    @PostMapping
    public ResponseEntity<?> createOrder(@RequestBody Map<String, Object> request) {
        try {
            Order order = new Order();
            
            // 从请求中提取订单信息并设置
            if (request.containsKey("userId")) {
                User user = new User();
                user.setId(Long.valueOf(request.get("userId").toString()));
                order.setUser(user);
            }
            
            if (request.containsKey("totalAmount")) {
                order.setTotalAmount(new BigDecimal(request.get("totalAmount").toString()));
            }
            
            if (request.containsKey("totalQuantity")) {
                order.setTotalQuantity(Integer.valueOf(request.get("totalQuantity").toString()));
            }
            
            if (request.containsKey("status")) {
                order.setStatus(request.get("status").toString());
            }
            
            if (request.containsKey("shippingAddress")) {
                order.setShippingAddress(request.get("shippingAddress").toString());
            }
            
            if (request.containsKey("paymentMethod")) {
                order.setPaymentMethod(request.get("paymentMethod").toString());
            }
            
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> itemsData = (List<Map<String, Object>>) request.get("items");
            List<OrderItem> items = itemsData.stream().map(data -> {
                OrderItem item = new OrderItem();
                
                // 从数据中提取订单项信息并设置
                if (data.containsKey("quantity")) {
                    item.setQuantity(Integer.valueOf(data.get("quantity").toString()));
                }
                
                if (data.containsKey("price")) {
                    item.setPrice(new BigDecimal(data.get("price").toString()));
                }
                
                // 计算subtotal（price * quantity）
                if (item.getPrice() != null && item.getQuantity() != null) {
                    item.setSubtotal(item.getPrice().multiply(new BigDecimal(item.getQuantity())));
                }
                
                // 支持两种方式：嵌套的product对象或直接的productId字段
                Product product = new Product();
                if (data.containsKey("productId")) {
                    // 处理直接的productId字段
                    product.setId(Long.valueOf(data.get("productId").toString()));
                } else if (data.containsKey("product") && data.get("product") instanceof Map) {
                    // 处理嵌套的product对象
                    @SuppressWarnings("unchecked")
                    Map<String, Object> productData = (Map<String, Object>) data.get("product");
                    if (productData.containsKey("id")) {
                        product.setId(Long.valueOf(productData.get("id").toString()));
                    }
                }
                item.setProduct(product);
                
                // 设置产品名称，可以从请求中获取或使用默认值
                if (data.containsKey("productName")) {
                    item.setProductName(data.get("productName").toString());
                } else {
                    item.setProductName("Unknown Product");
                }
                
                return item;
            }).toList();
            
            Order createdOrder = orderService.createOrder(order, items);
            return ResponseUtil.success(createdOrder);
        } catch (Exception e) {
            return ResponseUtil.badRequest(e.getMessage());
        }
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<?> updateOrderStatus(@PathVariable Long id, @RequestParam String status) {
        try {
            Order updatedOrder = orderService.updateOrderStatus(id, status);
            return ResponseUtil.success(updatedOrder);
        } catch (Exception e) {
            return ResponseUtil.badRequest(e.getMessage());
        }
    }

    @PutMapping("/{id}/cancel")
    public ResponseEntity<?> cancelOrder(@PathVariable Long id) {
        try {
            orderService.cancelOrder(id);
            return ResponseUtil.success();
        } catch (Exception e) {
            return ResponseUtil.badRequest(e.getMessage());
        }
    }

    @GetMapping("/{id}/items")
    public ResponseEntity<?> getOrderItems(@PathVariable Long id) {
        List<OrderItem> items = orderService.getOrderItemsByOrderId(id);
        return ResponseUtil.success(items);
    }
}