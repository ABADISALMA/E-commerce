package org.example.chatbot.rag;



import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class OrderRagHelper {

    private final ObjectMapper mapper = new ObjectMapper();

    public String topKOrdersJson(String ordersJson, String userMessage, int k) {
        try {
            // Si l'API retourne un objet wrapper, tu peux adapter ici.
            // Exemple actuel: on suppose que c'est une LISTE JSON.
            List<Map<String, Object>> orders = mapper.readValue(
                    ordersJson,
                    new TypeReference<List<Map<String, Object>>>() {}
            );

            String q = normalize(userMessage);

            List<Map<String, Object>> ranked = orders.stream()
                    .map(o -> Map.entry(score(o, q), o))
                    .sorted((a, b) -> Integer.compare(b.getKey(), a.getKey()))
                    .filter(e -> e.getKey() > 0)
                    .limit(k)
                    .map(Map.Entry::getValue)
                    .collect(Collectors.toList());

            // si rien match, on renvoie les plus récentes (si champ date existe) sinon un sample
            if (ranked.isEmpty()) {
                ranked = orders.stream()
                        .sorted((a, b) -> compareDatesDesc(a, b))
                        .limit(Math.min(k, orders.size()))
                        .collect(Collectors.toList());
            }

            return mapper.writeValueAsString(ranked);
        } catch (Exception e) {
            return ordersJson;
        }
    }

    private int score(Map<String, Object> o, String q) {
        String text = normalize(
                String.valueOf(o.getOrDefault("id", "")) + " " +
                        String.valueOf(o.getOrDefault("orderId", "")) + " " +
                        String.valueOf(o.getOrDefault("status", "")) + " " +
                        String.valueOf(o.getOrDefault("trackingNumber", "")) + " " +
                        String.valueOf(o.getOrDefault("tracking", "")) + " " +
                        String.valueOf(o.getOrDefault("createdAt", "")) + " " +
                        String.valueOf(o.getOrDefault("date", ""))
        );

        int s = 0;
        for (String token : tokens(q)) {
            if (token.length() < 2) continue;
            if (text.contains(token)) s += 3;
        }

        // bonus si on parle d'état
        if (q.contains("livr") && text.contains("livr")) s += 2;
        if (q.contains("annul") && text.contains("annul")) s += 2;
        if (q.contains("en cours") && text.contains("en cours")) s += 2;

        return s;
    }

    // compare desc sur champs date/createdAt si possible
    private int compareDatesDesc(Map<String, Object> a, Map<String, Object> b) {
        String da = String.valueOf(a.getOrDefault("createdAt", a.getOrDefault("date", "")));
        String db = String.valueOf(b.getOrDefault("createdAt", b.getOrDefault("date", "")));
        return db.compareTo(da); // simple lexicographic, OK si ISO-8601
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

