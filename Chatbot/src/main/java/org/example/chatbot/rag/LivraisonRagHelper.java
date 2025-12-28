package org.example.chatbot.rag;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class LivraisonRagHelper {

    private final ObjectMapper mapper = new ObjectMapper();

    public String topKLivraisonsJson(String livraisonJson, String userMessage, int k) {
        try {
            // Selon ton API, ça peut être un objet (1 livraison) ou une liste.
            // Ici: si c'est une liste, on filtre; si c'est un objet, on renvoie tel quel.
            if (livraisonJson.trim().startsWith("[")) {
                List<Map<String, Object>> livraisons = mapper.readValue(
                        livraisonJson,
                        new TypeReference<List<Map<String, Object>>>() {}
                );

                String q = normalize(userMessage);

                List<Map<String, Object>> ranked = livraisons.stream()
                        .map(l -> Map.entry(score(l, q), l))
                        .sorted((a, b) -> Integer.compare(b.getKey(), a.getKey()))
                        .filter(e -> e.getKey() > 0)
                        .limit(k)
                        .map(Map.Entry::getValue)
                        .collect(Collectors.toList());

                if (ranked.isEmpty()) {
                    ranked = livraisons.stream().limit(Math.min(k, livraisons.size())).collect(Collectors.toList());
                }

                return mapper.writeValueAsString(ranked);
            }

            // objet unique
            return livraisonJson;

        } catch (Exception e) {
            return livraisonJson;
        }
    }

    private int score(Map<String, Object> l, String q) {
        String text = normalize(
                String.valueOf(l.getOrDefault("trackingNumber", "")) + " " +
                        String.valueOf(l.getOrDefault("tracking", "")) + " " +
                        String.valueOf(l.getOrDefault("status", "")) + " " +
                        String.valueOf(l.getOrDefault("carrier", "")) + " " +
                        String.valueOf(l.getOrDefault("updatedAt", "")) + " " +
                        String.valueOf(l.getOrDefault("estimatedDelivery", ""))
        );

        int s = 0;
        for (String token : tokens(q)) {
            if (token.length() < 2) continue;
            if (text.contains(token)) s += 3;
        }

        if (q.contains("retard") && (text.contains("retard") || text.contains("late"))) s += 2;
        if (q.contains("livr") && text.contains("livr")) s += 2;

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

