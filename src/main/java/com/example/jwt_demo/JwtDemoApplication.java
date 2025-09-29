package com.example.jwt_demo;

import com.example.jwt_demo.entity.AppUser;
import com.example.jwt_demo.entity.Role;
import com.example.jwt_demo.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootApplication
public class JwtDemoApplication {
	public static void main(String[] args) {
		SpringApplication.run(JwtDemoApplication.class, args);
	}

	// Seed an admin and a user for testing
	@Bean
	CommandLineRunner run(UserRepository userRepository) {
		return args -> {
			if(userRepository.findByUsername("admin").isEmpty()){
				AppUser admin = new AppUser();
				admin.setUsername("admin");
				admin.setPassword(new BCryptPasswordEncoder().encode("adminpass"));
				admin.setRole(Role.ROLE_ADMIN);
				userRepository.save(admin);
			}
			if(userRepository.findByUsername("user").isEmpty()){
				AppUser user = new AppUser();
				user.setUsername("user");
				user.setPassword(new BCryptPasswordEncoder().encode("userpass"));
				user.setRole(Role.ROLE_USER);
				userRepository.save(user);
			}
		};
	}
}
