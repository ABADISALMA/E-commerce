package org.example.productservice.Controllers;

import org.example.productservice.entities.Product;
import org.example.productservice.services.ProductService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.beans.factory.annotation.Value;


import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/products")
public class ProductController {

    private final ProductService productService;
    @Value("${file.upload-dir:./uploads}")
    private String uploadDir;



    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    // ✅ CREATE - Admin et SuperAdmin seulement
    @PostMapping
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_SUPERADMIN')")
    public Product create(@RequestParam("name") String name,
                          @RequestParam("price") double price,
                          @RequestParam("category") String category,
                          @RequestParam("description") String description,
                          @RequestParam("stock") int stock,
                          @RequestParam("image") MultipartFile imageFile) throws IOException {

        String fileName = System.currentTimeMillis() + "_" +
                StringUtils.cleanPath(imageFile.getOriginalFilename());

        Path uploadPath = Paths.get(uploadDir);

        Files.createDirectories(uploadPath);

        Path filePath = uploadPath.resolve(fileName);
        Files.copy(imageFile.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        String imageUrl = "/uploads/" + fileName;

        Product product = new Product();
        product.setName(name);
        product.setPrice(price);
        product.setCategory(category);
        product.setDescription(description);
        product.setImage(imageUrl);
        product.setStockQuantity(stock); // ✅ adapte si ton champ s'appelle différemment

        return productService.create(product);
    }



    // ✅ READ - Tous les utilisateurs authentifiés
    @GetMapping
    public List<Product> getAllActive() {
        return productService.getAllActive();
    }

    @GetMapping("/{id}")
    public Optional<Product> getById(@PathVariable Long id) {
        return productService.getById(id);
    }

    @PutMapping(value = "/{id}/with-image", consumes = "multipart/form-data")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_SUPERADMIN')")
    public Product updateWithImage(
            @PathVariable Long id,
            @RequestParam("name") String name,
            @RequestParam("price") double price,
            @RequestParam("category") String category,
            @RequestParam("description") String description,
            @RequestParam("stock") int stock,
            @RequestParam(value = "image", required = false) MultipartFile imageFile
    ) throws IOException {

        Product p = productService.getById(id).orElseThrow();

        p.setName(name);
        p.setPrice(price);
        p.setCategory(category);
        p.setDescription(description);
        p.setStockQuantity(stock);

        if (imageFile != null && !imageFile.isEmpty()) {
            String fileName = System.currentTimeMillis() + "_" +
                    StringUtils.cleanPath(imageFile.getOriginalFilename());

            Path uploadPath = Paths.get(uploadDir);
            Files.createDirectories(uploadPath);

            Path filePath = uploadPath.resolve(fileName);
            Files.copy(imageFile.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            p.setImage("/uploads/" + fileName);
        }

        return productService.update(id, p);
    }


    // ✅ DELETE - Admin et SuperAdmin seulement
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_SUPERADMIN')")
    public void delete(@PathVariable Long id) {
        productService.delete(id);
    }
}