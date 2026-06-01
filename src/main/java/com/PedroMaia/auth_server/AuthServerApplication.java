package com.PedroMaia.auth_server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class AuthServerApplication {

	static void main(String[] args) {
		SpringApplication.run(AuthServerApplication.class, args);
	}

}
