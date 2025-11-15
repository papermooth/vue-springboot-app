package com.example.demo.service.impl;

import com.example.demo.model.Product;
import com.example.demo.repository.ProductRepository;
import com.example.demo.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Override
    @Cacheable(value = "products")
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    @Override
    @Cacheable(value = "product", key = "#id")
    public Optional<Product> getProductById(Long id) {
        return productRepository.findById(id);
    }

    @Override
    @Cacheable(value = "productsByCategory", key = "#category")
    public List<Product> getProductsByCategory(String category) {
        return productRepository.findByCategory(category);
    }

    @Override
    @Cacheable(value = "activeProducts")
    public List<Product> getActiveProducts() {
        return productRepository.findByStatusTrue();
    }

    @Override
    @Cacheable(value = "productsByKeyword", key = "#keyword")
    public List<Product> searchProducts(String keyword) {
        return productRepository.findByNameContainingIgnoreCase(keyword);
    }

    @Override
    @CachePut(value = "product", key = "#product.id")
    @CacheEvict(value = "products", allEntries = true)
    public Product saveProduct(Product product) {
        return productRepository.save(product);
    }

    @Override
    @CacheEvict(value = {"product", "products", "productsByCategory", "activeProducts", "productsByKeyword"}, key = "#id", allEntries = true)
    public void deleteProduct(Long id) {
        productRepository.deleteById(id);
    }

    @Override
    @CachePut(value = "product", key = "#id")
    @CacheEvict(value = {"products", "productsByCategory", "activeProducts", "productsByKeyword"}, allEntries = true)
    public Product updateProductStock(Long id, Integer stock) {
        Optional<Product> productOptional = productRepository.findById(id);
        if (productOptional.isPresent()) {
            Product product = productOptional.get();
            product.setStock(stock);
            return productRepository.save(product);
        }
        return null;
    }
}