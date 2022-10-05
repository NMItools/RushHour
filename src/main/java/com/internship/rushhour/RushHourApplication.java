package com.internship.rushhour;

import com.internship.rushhour.domain.role.models.RoleCreateDTO;
import com.internship.rushhour.domain.role.service.RoleService;
import com.internship.rushhour.infrastructure.exceptions.ResourceUniqueFieldTakenException;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class RushHourApplication {

	public static void main(String[] args) {
		SpringApplication.run(RushHourApplication.class, args);
	}

	@Bean
	CommandLineRunner run(RoleService roleService) {
		return args -> {
			try {
				roleService.createRole(new RoleCreateDTO("PROVIDER_ADMINISTRATOR"));
				roleService.createRole(new RoleCreateDTO("EMPLOYEE"));
				roleService.createRole(new RoleCreateDTO("CLIENT"));
			} catch (ResourceUniqueFieldTakenException e) {

			}
		};
	}
}
