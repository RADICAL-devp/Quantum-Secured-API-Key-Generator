package com.example.quantumkey.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * A DTO (Data Transfer Object) to map the JSON response from the Python service.
 * {"random_bits": "0110..."}
 *
 * @JsonIgnoreProperties(ignoreUnknown = true) is good practice,
 * it tells Jackson to not fail if the JSON has extra fields not defined here.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class QrngResponse {

    private String randomBits;

    // Default constructor (needed for Jackson)
    public QrngResponse() {
    }

    // Getter
    public String getRandomBits() {
        return randomBits;
    }

    // Setter
    public void setRandomBits(String randomBits) {
        this.randomBits = randomBits;
    }
}

now do for this
