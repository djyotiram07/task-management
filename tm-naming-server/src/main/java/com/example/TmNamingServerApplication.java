package com.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@SpringBootApplication
@EnableEurekaServer
public class TmNamingServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(TmNamingServerApplication.class, args);
	}

}
