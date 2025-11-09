package com.example.quantumkey;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
public class QuantumKeyApplication {

	public static void main(String[] args) {
		SpringApplication.run(QuantumKeyApplication.class, args);
	}

	/**
	 * Creates a RestTemplate bean for making HTTP requests.
	 * This bean will be managed by Spring and can be injected into other services.
	 */
	@Bean
	public RestTemplate restTemplate() {
		return new RestTemplate();
	}
}