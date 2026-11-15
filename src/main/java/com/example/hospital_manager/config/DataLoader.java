package com.example.hospital_manager.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataLoader implements CommandLineRunner {

    @Override
    public void run(String... args) {
        // Data seeding disabled on application startup.
        // Database and tables will still be created automatically by Hibernate/JPA if they do not exist.
    }
}
