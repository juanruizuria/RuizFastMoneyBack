package com.ruiz.prestamos;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableJpaRepositories
@EnableScheduling
@EnableAsync
@EnableJpaAuditing(auditorAwareRef = "auditUser")
public class PrestamosApplication {

	public static void main(String[] args) {

		SpringApplication.run(PrestamosApplication.class, args);
	}

}
