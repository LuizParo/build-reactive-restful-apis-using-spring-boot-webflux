package com.learnreactivespring;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.reactive.function.client.WebClient;

@SpringBootApplication
public class SpringReactiveClientExampleApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringReactiveClientExampleApplication.class, args);
	}

	@Bean
	public WebClient newWebClient() {
		return WebClient.create("http://localhost:8080");
	}
}
