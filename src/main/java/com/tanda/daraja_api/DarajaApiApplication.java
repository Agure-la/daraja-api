package com.tanda.daraja_api;

import com.tanda.daraja_api.service.B2CService;
import net.minidev.json.JSONObject;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.context.ApplicationContext;

@SpringBootApplication(exclude = { SecurityAutoConfiguration.class })
public class DarajaApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(DarajaApiApplication.class, args);
	}

}
