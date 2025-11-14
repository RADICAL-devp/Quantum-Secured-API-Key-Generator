package com.example.quantumkey.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class QrngResponse {

    private String randomBits;

    public QrngResponse() {
    }

    public String getRandomBits() {
        return randomBits;
    }

    public void setRandomBits(String randomBits) {
        this.randomBits = randomBits;
    }
}
