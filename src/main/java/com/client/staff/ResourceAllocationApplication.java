package com.client.staff;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;


@EnableAsync
@SpringBootApplication
@EnableScheduling
@EnableAspectJAutoProxy
public class ResourceAllocationApplication {

	public static void main(String[] args) {
		SpringApplication.run(ResourceAllocationApplication.class, args);
	}
}



