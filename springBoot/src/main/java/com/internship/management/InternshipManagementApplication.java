package com.internship.management;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class InternshipManagementApplication {

	public static void main(String[] args) {
		SpringApplication.run(InternshipManagementApplication.class, args);
	}

}
