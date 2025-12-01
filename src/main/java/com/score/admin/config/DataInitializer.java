package com.score.admin.config;

import com.score.admin.service.UserService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataInitializer {

    @Bean
    public CommandLineRunner initAdmin(UserService userService) {
        return args -> userService.ensureAdminUser();
    }
}

