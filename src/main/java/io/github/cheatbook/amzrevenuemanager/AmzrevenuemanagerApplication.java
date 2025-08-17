package io.github.cheatbook.amzrevenuemanager;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class AmzrevenuemanagerApplication {

	public static void main(String[] args) {
		SpringApplication.run(AmzrevenuemanagerApplication.class, args);
	}

}
