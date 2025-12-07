package org.example.productservice.Controllers;

import org.example.productservice.entities.Product;
import org.example.productservice.services.ProductService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    // ✅ CREATE - Admin et SuperAdmin seulement
    @PostMapping
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_SUPERADMIN')")
    public Product create(@RequestBody Product product) {
        return productService.create(product);
    }

    // ✅ READ - Tous les utilisateurs authentifiés
    @GetMapping
    @PreAuthorize("hasAnyAuthority('ROLE_USER', 'ROLE_ADMIN', 'ROLE_SUPERADMIN')")
    public List<Product> getAllActive() {
        return productService.getAllActive();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_USER', 'ROLE_ADMIN', 'ROLE_SUPERADMIN')")
    public Optional<Product> getById(@PathVariable Long id) {
        return productService.getById(id);
    }

    // ✅ UPDATE - Admin et SuperAdmin seulement
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_SUPERADMIN')")
    public Product update(@PathVariable Long id, @RequestBody Product updated) {
        return productService.update(id, updated);
    }

    // ✅ DELETE - Admin et SuperAdmin seulement
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_SUPERADMIN')")
    public void delete(@PathVariable Long id) {
        productService.delete(id);
    }
}