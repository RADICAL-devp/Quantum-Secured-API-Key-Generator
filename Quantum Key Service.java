package com.example.quantumkey;

import com.example.quantumkey.dto.QrngResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigInteger;

@Service
public class QuantumKeyService {

    private final RestTemplate restTemplate;

    /**
     * This injects the URL from application.properties
     */
    @Value("${qrng.service.url}")
    private String qrngServiceUrl;

    @Autowired
    public QuantumKeyService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    /**
     * Generates a new API key by fetching random bits from the Python service.
     * @return A 64-character hexadecimal API key.
     */
    public String generateQuantumApiKey() {
        // 1. Call the Python QRNG service
        // We use getForObject to directly map the JSON response to our QrngResponse DTO.
        QrngResponse response;
        try {
            response = restTemplate.getForObject(qrngServiceUrl, QrngResponse.class);
        } catch (Exception e) {
            // Handle cases where the Python service is down or unreachable
            throw new RuntimeException("Failed to connect to QRNG service: " + e.getMessage());
        }


        if (response == null || response.getRandomBits() == null || response.getRandomBits().isEmpty()) {
            throw new RuntimeException("Received empty response from QRNG service");
        }

        String bits = response.getRandomBits();

        // 2. Convert the bit string to a standard API key (Hex)
        // new BigInteger(bits, 2) interprets the string as a binary number.
        // .toString(16) converts that number into its hexadecimal representation.
        String apiKey = new BigInteger(bits, 2).toString(16);

        // Optional: Pad with leading zeros if the hex string is shorter than 64 chars
        // (This can happen if the first few bits are 0)
        int expectedHexLength = bits.length() / 4;
        while (apiKey.length() < expectedHexLength) {
            apiKey = "0" + apiKey;
        }

        return apiKey;
    }
}