package org.example.productservice.Controllers;

import org.example.productservice.entities.Product;
import org.example.productservice.services.ProductService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/products")
//@CrossOrigin(origins = "http://localhost:4200")//autorise angular
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    // CREATE
    @PostMapping
    public Product create(@RequestBody Product product) {
        return productService.create(product);
    }

    // READ - all active
    @GetMapping
    public List<Product> getAllActive() {
        return productService.getAllActive();
    }

    // by id
    @GetMapping("/{id}")
    public Optional<Product> getById(@PathVariable Long id) {
        return productService.getById(id);
    }

    // UPDATE
    @PutMapping("/{id}")
    public Product update(@PathVariable Long id, @RequestBody Product updated) {
        return productService.update(id, updated);
    }

    // DELETE
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        productService.delete(id);
    }
}