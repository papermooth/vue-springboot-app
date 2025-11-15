package com.example.demo.service;

import com.example.demo.model.Product;
import java.util.List;
import java.util.Optional;

public interface ProductService {

    List<Product> getAllProducts();

    Optional<Product> getProductById(Long id);

    List<Product> getProductsByCategory(String category);

    List<Product> getActiveProducts();

    List<Product> searchProducts(String keyword);

    Product saveProduct(Product product);

    void deleteProduct(Long id);

    Product updateProductStock(Long id, Integer stock);
}