package com.hackathon.lep.offline;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class OfflineApplication {

	public static void main(String[] args) {
		SpringApplication.run(OfflineApplication.class, args);
	}

}
