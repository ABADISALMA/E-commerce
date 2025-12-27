package org.example.chatbot.rag;



import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class PaymentRagHelper {

    private final ObjectMapper mapper = new ObjectMapper();

    public String topKPaymentsJson(String paymentsJson, String userMessage, int k) {
        try {
            List<Map<String, Object>> payments = mapper.readValue(
                    paymentsJson,
                    new TypeReference<List<Map<String, Object>>>() {}
            );

            String q = normalize(userMessage);

            List<Map<String, Object>> ranked = payments.stream()
                    .map(p -> Map.entry(score(p, q), p))
                    .sorted((a, b) -> Integer.compare(b.getKey(), a.getKey()))
                    .filter(e -> e.getKey() > 0)
                    .limit(k)
                    .map(Map.Entry::getValue)
                    .collect(Collectors.toList());

            if (ranked.isEmpty()) {
                ranked = payments.stream().limit(Math.min(k, payments.size())).collect(Collectors.toList());
            }

            return mapper.writeValueAsString(ranked);
        } catch (Exception e) {
            return paymentsJson;
        }
    }

    private int score(Map<String, Object> p, String q) {
        String text = normalize(
                String.valueOf(p.getOrDefault("id", "")) + " " +
                        String.valueOf(p.getOrDefault("status", "")) + " " +
                        String.valueOf(p.getOrDefault("amount", "")) + " " +
                        String.valueOf(p.getOrDefault("currency", "")) + " " +
                        String.valueOf(p.getOrDefault("method", "")) + " " +
                        String.valueOf(p.getOrDefault("createdAt", "")) + " " +
                        String.valueOf(p.getOrDefault("invoice", "")) + " " +
                        String.valueOf(p.getOrDefault("facture", ""))
        );

        int s = 0;
        for (String token : tokens(q)) {
            if (token.length() < 2) continue;
            if (text.contains(token)) s += 3;
        }

        if (q.contains("facture") && (text.contains("invoice") || text.contains("facture"))) s += 2;
        if (q.contains("rembours") && text.contains("rembours")) s += 2;

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

