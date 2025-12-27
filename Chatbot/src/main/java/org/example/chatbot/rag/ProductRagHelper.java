package org.example.chatbot.rag;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class ProductRagHelper {

    private final ObjectMapper mapper = new ObjectMapper();

    public String topKProductsJson(String productsJson, String userMessage, int k) {
        try {
            List<Map<String, Object>> products = mapper.readValue(
                    productsJson,
                    new TypeReference<List<Map<String, Object>>>() {}
            );

            String q = normalize(userMessage);

            // scoring simple sur champs fréquents
            List<Map<String, Object>> ranked = products.stream()
                    .map(p -> Map.entry(score(p, q), p))
                    .sorted((a, b) -> Integer.compare(b.getKey(), a.getKey()))
                    .filter(e -> e.getKey() > 0) // ne garder que les pertinents
                    .limit(k)
                    .map(Map.Entry::getValue)
                    .collect(Collectors.toList());

            // si rien trouvé, on envoie juste un petit sample (évite hallucinations)
            if (ranked.isEmpty()) {
                ranked = products.stream().limit(Math.min(k, products.size())).collect(Collectors.toList());
            }

            return mapper.writeValueAsString(ranked);
        } catch (Exception e) {
            // fallback: si parsing échoue, on renvoie le brut (moins fiable)
            return productsJson;
        }
    }

    private int score(Map<String, Object> p, String q) {
        String text = normalize(
                String.valueOf(p.getOrDefault("name", "")) + " " +
                        String.valueOf(p.getOrDefault("title", "")) + " " +
                        String.valueOf(p.getOrDefault("description", "")) + " " +
                        String.valueOf(p.getOrDefault("category", "")) + " " +
                        String.valueOf(p.getOrDefault("brand", ""))
        );

        int s = 0;
        for (String token : tokens(q)) {
            if (token.length() < 3) continue;
            if (text.contains(token)) s += 3;
        }

        // bonus si l’utilisateur demande prix/stock
        if (q.contains("prix") && (p.containsKey("price") || p.containsKey("prix"))) s += 2;
        if (q.contains("stock") && p.containsKey("stock")) s += 2;

        return s;
    }

    private String normalize(String s) {
        return s == null ? "" : s.toLowerCase(Locale.ROOT);
    }

    private List<String> tokens(String q) {
        return Arrays.stream(q.split("\\s+"))
                .map(t -> t.replaceAll("[^a-z0-9àâäçéèêëîïôöùûü-]", ""))
                .filter(t -> !t.isBlank())
                .collect(Collectors.toList());
    }
}
