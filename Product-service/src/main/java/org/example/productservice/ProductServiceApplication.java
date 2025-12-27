package org.example.productservice;

import org.example.productservice.entities.Product;
import org.example.productservice.repositories.ProductRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.List;

@SpringBootApplication
public class ProductServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(ProductServiceApplication.class, args);
    }
    @Bean
    CommandLineRunner initDatabase(ProductRepository productRepository) {
        return args -> {

            // ✅ Si la DB a déjà des produits, on ne touche à rien
            if (productRepository.count() > 0) {
                System.out.println("ℹ️ Produits déjà présents, aucune suppression / insertion.");
                return;
            }

            var products = List.of(
                    Product.builder()
                            .name("iPhone 15 Pro Max")
                            .description("Le dernier smartphone Apple avec puce A17 Pro, écran Super Retina XDR 6,7 pouces et système de caméra pro avancé.")
                            .price(1479.00)
                            .image("https://picsum.photos/seed/iphone15/400/400")
                            .category("Smartphones")
                            .stockQuantity(25)
                            .rating(4.8)
                            .reviews(1250)
                            .active(true)
                            .build(),

                    Product.builder()
                            .name("Samsung Galaxy S24 Ultra")
                            .description("Smartphone premium avec S Pen intégré, écran Dynamic AMOLED 2X et Galaxy AI.")
                            .price(1369.00)
                            .image("https://picsum.photos/seed/galaxy24/400/400")
                            .category("Smartphones")
                            .stockQuantity(30)
                            .rating(4.7)
                            .reviews(890)
                            .active(true)
                            .build(),

                    Product.builder()
                            .name("MacBook Pro 16\" M3 Max")
                            .description("Ordinateur portable professionnel avec puce M3 Max, 36 Go de RAM et écran Liquid Retina XDR.")
                            .price(4199.00)
                            .image("https://picsum.photos/seed/macbook16/400/400")
                            .category("Ordinateurs")
                            .stockQuantity(15)
                            .rating(4.9)
                            .reviews(560)
                            .active(true)
                            .build(),

                    Product.builder()
                            .name("Dell XPS 15")
                            .description("Ultrabook Windows avec écran OLED 3.5K, processeur Intel Core i9 et 32 Go de RAM.")
                            .price(2299.00)
                            .image("https://picsum.photos/seed/xps15/400/400")
                            .category("Ordinateurs")
                            .stockQuantity(20)
                            .rating(4.6)
                            .reviews(420)
                            .active(true)
                            .build(),

                    Product.builder()
                            .name("Sony WH-1000XM5")
                            .description("Casque audio sans fil avec réduction de bruit adaptative leader du marché et 30h d'autonomie.")
                            .price(379.00)
                            .image("https://picsum.photos/seed/sonywh/400/400")
                            .category("Audio")
                            .stockQuantity(50)
                            .rating(4.8)
                            .reviews(2100)
                            .active(true)
                            .build(),

                    Product.builder()
                            .name("Apple AirPods Pro 2")
                            .description("Écouteurs sans fil avec réduction de bruit active, audio spatial et étui MagSafe.")
                            .price(279.00)
                            .image("https://picsum.photos/seed/airpods/400/400")
                            .category("Audio")
                            .stockQuantity(100)
                            .rating(4.7)
                            .reviews(3500)
                            .active(true)
                            .build(),

                    Product.builder()
                            .name("iPad Pro 12.9\" M2")
                            .description("Tablette professionnelle avec puce M2, écran Liquid Retina XDR et compatibilité Apple Pencil 2.")
                            .price(1329.00)
                            .image("https://picsum.photos/seed/ipadpro/400/400")
                            .category("Tablettes")
                            .stockQuantity(35)
                            .rating(4.8)
                            .reviews(780)
                            .active(true)
                            .build(),

                    Product.builder()
                            .name("Samsung Galaxy Tab S9 Ultra")
                            .description("Tablette Android premium avec écran Super AMOLED 14.6 pouces et S Pen inclus.")
                            .price(1199.00)
                            .image("https://picsum.photos/seed/tabs9/400/400")
                            .category("Tablettes")
                            .stockQuantity(25)
                            .rating(4.6)
                            .reviews(450)
                            .active(true)
                            .build(),

                    Product.builder()
                            .name("PlayStation 5")
                            .description("Console de jeu nouvelle génération avec SSD ultra-rapide et manette DualSense.")
                            .price(549.00)
                            .image("https://picsum.photos/seed/ps5/400/400")
                            .category("Gaming")
                            .stockQuantity(20)
                            .rating(4.9)
                            .reviews(5600)
                            .active(true)
                            .build(),

                    Product.builder()
                            .name("Xbox Series X")
                            .description("Console de jeu la plus puissante de Microsoft avec 12 téraflops et Game Pass inclus.")
                            .price(499.00)
                            .image("https://picsum.photos/seed/xbox/400/400")
                            .category("Gaming")
                            .stockQuantity(30)
                            .rating(4.8)
                            .reviews(4200)
                            .active(true)
                            .build(),

                    Product.builder()
                            .name("Nintendo Switch OLED")
                            .description("Console portable avec écran OLED 7 pouces et possibilité de jouer en mode TV.")
                            .price(349.00)
                            .image("https://picsum.photos/seed/switch/400/400")
                            .category("Gaming")
                            .stockQuantity(45)
                            .rating(4.7)
                            .reviews(3800)
                            .active(true)
                            .build(),

                    Product.builder()
                            .name("Apple Watch Ultra 2")
                            .description("Montre connectée robuste avec GPS de précision, autonomie de 36h et écran Always-On.")
                            .price(899.00)
                            .image("https://picsum.photos/seed/watchultra/400/400")
                            .category("Montres")
                            .stockQuantity(40)
                            .rating(4.8)
                            .reviews(890)
                            .active(true)
                            .build(),

                    Product.builder()
                            .name("Samsung Galaxy Watch 6 Classic")
                            .description("Montre connectée élégante avec lunette rotative et suivi santé avancé.")
                            .price(419.00)
                            .image("https://picsum.photos/seed/galaxywatch/400/400")
                            .category("Montres")
                            .stockQuantity(55)
                            .rating(4.5)
                            .reviews(620)
                            .active(true)
                            .build(),

                    Product.builder()
                            .name("LG OLED C3 65\"")
                            .description("Téléviseur OLED 4K avec processeur α9 Gen6 AI, Dolby Vision et 4 ports HDMI 2.1.")
                            .price(1799.00)
                            .image("https://picsum.photos/seed/lgoled/400/400")
                            .category("TV")
                            .stockQuantity(12)
                            .rating(4.9)
                            .reviews(1100)
                            .active(true)
                            .build(),

                    Product.builder()
                            .name("Samsung QN90C Neo QLED 55\"")
                            .description("Téléviseur Neo QLED 4K avec technologie Quantum Matrix et Gaming Hub.")
                            .price(1299.00)
                            .image("https://picsum.photos/seed/samsungtv/400/400")
                            .category("TV")
                            .stockQuantity(18)
                            .rating(4.7)
                            .reviews(750)
                            .active(true)
                            .build(),

                    Product.builder()
                            .name("Canon EOS R6 Mark II")
                            .description("Appareil photo hybride plein format avec stabilisation 8 stops et vidéo 4K 60fps.")
                            .price(2899.00)
                            .image("https://picsum.photos/seed/canonr6/400/400")
                            .category("Photo")
                            .stockQuantity(10)
                            .rating(4.8)
                            .reviews(340)
                            .active(true)
                            .build(),

                    Product.builder()
                            .name("Sony A7 IV")
                            .description("Appareil photo hybride polyvalent avec capteur 33 MP et autofocus intelligent.")
                            .price(2499.00)
                            .image("https://picsum.photos/seed/sonya7/400/400")
                            .category("Photo")
                            .stockQuantity(8)
                            .rating(4.9)
                            .reviews(520)
                            .active(true)
                            .build(),

                    Product.builder()
                            .name("Dyson V15 Detect")
                            .description("Aspirateur sans fil avec laser révélateur de poussière et écran LCD intelligent.")
                            .price(699.00)
                            .image("https://picsum.photos/seed/dysonv15/400/400")
                            .category("Maison")
                            .stockQuantity(35)
                            .rating(4.7)
                            .reviews(1800)
                            .active(true)
                            .build()
            );

            productRepository.saveAll(products);

            productRepository.findAll().forEach(p ->
                    System.out.println("Produit inséré : " + p.getName() + " (" + p.getPrice() + "€)")
            );
        };
    }

}
