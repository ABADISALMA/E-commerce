package org.example.productservice.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "products")
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Column(length = 1000)
    private String description;

    private Double price;

    private Integer stockQuantity;

    private String category;

    private Boolean active;

    // ✅ nouveaux champs (comme ton mockProducts)
    private String image;

    private Double rating;

    private Integer reviews;

    public void setStock(int i) {
    }
}
