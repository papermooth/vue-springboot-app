package com.example.demo.repository;

import com.example.demo.model.Order;
import com.example.demo.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    Optional<Order> findByOrderNo(String orderNo);

    List<Order> findByUser(User user);

    List<Order> findByStatus(String status);

    List<Order> findByUserIdOrderByCreatedAtDesc(Long userId);
}