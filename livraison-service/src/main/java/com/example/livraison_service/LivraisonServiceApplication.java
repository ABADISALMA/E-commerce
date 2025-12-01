package com.example.livraison_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
public class LivraisonServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(LivraisonServiceApplication.class, args);
	}

}
