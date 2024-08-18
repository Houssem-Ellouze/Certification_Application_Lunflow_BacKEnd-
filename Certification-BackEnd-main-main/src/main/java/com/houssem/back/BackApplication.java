package com.houssem.back;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

@SpringBootApplication(exclude = {SecurityAutoConfiguration.class} , scanBasePackages = "com.houssem.back")
public class BackApplication {

    public static void main(String[] args) {
        SpringApplication.run ( BackApplication.class, args );
    }

}
