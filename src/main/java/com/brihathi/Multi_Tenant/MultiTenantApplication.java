package com.brihathi.Multi_Tenant;

import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;


@SpringBootApplication(scanBasePackages = "com.brihathi")
@EntityScan("com.brihathi")
@EnableJpaRepositories("com.brihathi")
@EnableScheduling
public class MultiTenantApplication {

	public static void main(String[] args) {
		SpringApplication.run(MultiTenantApplication.class, args);
		System.out.println("Multi Tenant Application Started Successfully");
	}

}
