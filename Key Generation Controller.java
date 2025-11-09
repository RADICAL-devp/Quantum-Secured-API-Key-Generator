package com.example.quantumkey;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class KeyGenerationController {

    private final QuantumKeyService keyService;

    @Autowired
    public KeyGenerationController(QuantumKeyService keyService) {
        this.keyService = keyService;
    }

    /**
     * The main API endpoint for clients to request a new API key.
     */
    @GetMapping("/generate-new-api-key")
    public ResponseEntity<Map<String, String>> getNewKey() {
        try {
            // 1. Call the service to do the work
            String key = keyService.generateQuantumApiKey();

            // 2. Create a simple JSON response: {"apiKey": "..."}
            Map<String, String> response = Map.of("apiKey", key);

            // 3. Return it with a 200 OK status
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            // If anything goes wrong (e.g., Python service is down), return an error
            Map<String, String> errorResponse = Map.of(
                "error", "Failed to generate quantum key",
                "message", e.getMessage()
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
}