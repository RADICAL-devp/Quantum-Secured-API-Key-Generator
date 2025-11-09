package com.example.quantumkey;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.reactive.function.client.ExchangeStrategies;

import java.security.MessageDigest;
import java.util.Base64;

@Service
public class QuantumKeyService {

    private final WebClient webClient;

    // The URL of your Python QRNG service endpoint
    private static final String QRNG_URL = "http://localhost:5000/qrng";

    public QuantumKeyService() {
        this.webClient = WebClient.builder()
                .baseUrl(QRNG_URL)
                .exchangeStrategies(ExchangeStrategies.builder()
                        .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(1024 * 1024))
                        .build())
                .build();
    }

    /**
     * Calls Python QRNG service, extracts randomness, runs HKDF-like hashing,
     * and returns a base64 encoded API key.
     */
    public String generateQuantumApiKey() {
        try {
            // Request raw randomness from Python QRNG microservice
            String rawBits = webClient.get()
                    .retrieve()
                    .bodyToMono(String.class)
                    .block(); // synchronous (safe here)

            if (rawBits == null || rawBits.isBlank()) {
                throw new RuntimeException("QRNG returned empty output");
            }

            // Convert randomness -> cryptographically usable key
            return deriveApiKey(rawBits);

        } catch (WebClientResponseException e) {
            throw new RuntimeException("QRNG service HTTP error: " + e.getStatusCode() + " - " + e.getResponseBodyAsString());
        } catch (Exception e) {
            throw new RuntimeException("Failed to retrieve or process quantum randomness: " + e.getMessage(), e);
        }
    }

    /**
     * Derives a stable fixed-size API key using SHA-256 and Base64.
     * Equivalent to a lightweight KDF / entropy extractor.
     */
    private String deriveApiKey(String rawInput) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(rawInput.getBytes());

            // Encode as URL-safe Base64, remove padding for cleaner API keys.
            return Base64.getUrlEncoder().withoutPadding().encodeToString(hashBytes);

        } catch (Exception e) {
            throw new RuntimeException("Failed to derive API key from randomness", e);
        }
    }
}
