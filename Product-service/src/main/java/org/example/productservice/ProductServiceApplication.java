package org.example.productservice;

import org.example.productservice.entities.Product;
import org.example.productservice.repositories.ProductRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class ProductServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(ProductServiceApplication.class, args);
    }

    @Bean
    CommandLineRunner initDatabase(ProductRepository productRepository) {
        return args -> {

            // Vider la base au démarrage (optionnel)
            productRepository.deleteAll();

            // Ajouter quelques produits
            productRepository.save(Product.builder()
                    .name("Laptop Dell XPS 13")
                    .description("Ultrabook performant avec écran FHD et SSD NVMe.")
                    .price(1299.99)
                    .stockQuantity(10)
                    .category("Informatique")
                    .active(true)
                    .build());

            productRepository.save(Product.builder()
                    .name("iPhone 15 Pro")
                    .description("Smartphone Apple dernière génération avec puce A17.")
                    .price(1199.99)
                    .stockQuantity(25)
                    .category("Smartphones")
                    .active(true)
                    .build());

            productRepository.save(Product.builder()
                    .name("Casque Sony WH-1000XM5")
                    .description("Casque Bluetooth avec réduction de bruit active.")
                    .price(349.99)
                    .stockQuantity(50)
                    .category("Audio")
                    .active(true)
                    .build());

            productRepository.save(Product.builder()
                    .name("Télévision Samsung 55\" OLED")
                    .description("TV OLED UHD 4K, HDR10+, ports HDMI, Smart TV.")
                    .price(899.99)
                    .stockQuantity(15)
                    .category("TV & Home Cinema")
                    .active(true)
                    .build());

            productRepository.save(Product.builder()
                    .name("Clavier mécanique Logitech G Pro")
                    .description("Clavier gaming RGB avec switches GX Blue.")
                    .price(149.99)
                    .stockQuantity(40)
                    .category("Gaming")
                    .active(true)
                    .build());

            // Log de confirmation
            productRepository.findAll().forEach(p ->
                    System.out.println("Produit inséré : " + p.getName() + " (" + p.getPrice() + "€)")
            );
        };
    }
}
