package com.tanda.daraja_api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

@SpringBootApplication(exclude = { SecurityAutoConfiguration.class })
public class DarajaApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(DarajaApiApplication.class, args);
	}

}
