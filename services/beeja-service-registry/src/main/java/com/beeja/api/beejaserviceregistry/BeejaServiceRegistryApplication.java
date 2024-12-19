package com.beeja.api.beejaserviceregistry;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@SpringBootApplication
@EnableEurekaServer
public class BeejaServiceRegistryApplication {

	public static void main(String[] args) {
		SpringApplication.run(BeejaServiceRegistryApplication.class, args);
	}

}
