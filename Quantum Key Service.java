package com.example.quantumkey;

import com.example.quantumkey.dto.QrngResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.math.BigInteger;

@Service
public class QuantumKeyService {

    private final RestTemplate restTemplate;

    @Value("${qrng.service.url}")
    private String qrngServiceUrl;

    public QuantumKeyService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public String generateQuantumApiKey() {
        QrngResponse response;

        try {
            response = restTemplate.getForObject(qrngServiceUrl + "?bits=256", QrngResponse.class);
        } catch (RestClientException e) {
            throw new RuntimeException("Failed to connect to QRNG Python service: " + e.getMessage(), e);
        }

        if (response == null || response.getRandomBits() == null || response.getRandomBits().isEmpty()) {
            throw new RuntimeException("QRNG service returned an empty or invalid response.");
        }

        String bits = response.getRandomBits();

        String apiKey = new BigInteger(bits, 2).toString(16);

        int expectedHexLength = bits.length() / 4;
        apiKey = String.format("%" + expectedHexLength + "s", apiKey).replace(' ', '0');

        return apiKey;
    }
}
