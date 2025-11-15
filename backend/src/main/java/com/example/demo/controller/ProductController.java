package com.example.demo.controller;

import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.model.Product;
import com.example.demo.service.ProductService;
import com.example.demo.util.ResponseUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    @Autowired
    private ProductService productService;

    @GetMapping
    public ResponseEntity<?> getAllProducts() {
        List<Product> products = productService.getAllProducts();
        return ResponseUtil.success(products);
    }

    @GetMapping("/active")
    public ResponseEntity<?> getActiveProducts() {
        List<Product> products = productService.getActiveProducts();
        return ResponseUtil.success(products);
    }

    @GetMapping("/category/{category}")
    public ResponseEntity<?> getProductsByCategory(@PathVariable String category) {
        List<Product> products = productService.getProductsByCategory(category);
        return ResponseUtil.success(products);
    }

    @GetMapping("/search")
    public ResponseEntity<?> searchProducts(@RequestParam String keyword) {
        List<Product> products = productService.searchProducts(keyword);
        return ResponseUtil.success(products);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getProductById(@PathVariable Long id) {
        Product product = productService.getProductById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
        return ResponseUtil.success(product);
    }

    @PostMapping
    public ResponseEntity<?> createProduct(@RequestBody Product product) {
        Product savedProduct = productService.saveProduct(product);
        return ResponseUtil.success(savedProduct);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateProduct(@PathVariable Long id, @RequestBody Product product) {
        Product existingProduct = productService.getProductById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
        
        product.setId(id);
        Product updatedProduct = productService.saveProduct(product);
        return ResponseUtil.success(updatedProduct);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteProduct(@PathVariable Long id) {
        Product existingProduct = productService.getProductById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
        
        productService.deleteProduct(id);
        return ResponseUtil.success();
    }

    @PutMapping("/{id}/stock")
    public ResponseEntity<?> updateProductStock(@PathVariable Long id, @RequestParam Integer stock) {
        Product updatedProduct = productService.updateProductStock(id, stock);
        if (updatedProduct == null) {
            throw new ResourceNotFoundException("Product not found with id: " + id);
        }
        return ResponseUtil.success(updatedProduct);
    }
}