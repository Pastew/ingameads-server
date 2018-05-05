package com.pastew.ingameadsimageprovidergateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@EnableDiscoveryClient
@SpringBootApplication
public class IngameadsImageProviderGatewayApplication {

	@Bean
	@LoadBalanced
	public RestTemplate restTemplate() {
		return new RestTemplate();
	}

	@RestController
	public class CodeCoupleController {

		private final RestTemplate restTemplate;

		public CodeCoupleController(RestTemplate restTemplate) {
			this.restTemplate = restTemplate;
		}

		@GetMapping("/show")
		public String AskRandomInstanceForPort(){
			return restTemplate.getForObject("http://ingameads-image-provider/showport", String.class);
		}
	}

	public static void main(String[] args) {
		SpringApplication.run(IngameadsImageProviderGatewayApplication.class, args);
	}
}
