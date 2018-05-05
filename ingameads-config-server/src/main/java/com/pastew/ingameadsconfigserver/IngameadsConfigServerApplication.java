package com.pastew.ingameadsconfigserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.config.server.EnableConfigServer;

@EnableConfigServer
@SpringBootApplication
public class IngameadsConfigServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(IngameadsConfigServerApplication.class, args);
	}
}
