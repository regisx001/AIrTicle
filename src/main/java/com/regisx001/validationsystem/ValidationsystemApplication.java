package com.regisx001.validationsystem;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@SpringBootApplication
public class ValidationsystemApplication {

	public static void main(String[] args) {
		SpringApplication.run(ValidationsystemApplication.class, args);
	}

}
