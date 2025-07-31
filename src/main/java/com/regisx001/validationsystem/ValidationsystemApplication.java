package com.regisx001.validationsystem;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@SpringBootApplication
@EntityScan("com.regisx001.validationsystem.domain.entities")
@EnableJpaRepositories("com.regisx001.validationsystem.repositories")
public class ValidationsystemApplication {

	public static void main(String[] args) {
		SpringApplication.run(ValidationsystemApplication.class, args);
	}

}
