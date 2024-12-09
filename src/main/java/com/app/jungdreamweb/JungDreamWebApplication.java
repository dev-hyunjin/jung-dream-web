package com.app.jungdreamweb;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class JungDreamWebApplication {

	public static void main(String[] args) {
		SpringApplication.run(JungDreamWebApplication.class, args);
	}

}
