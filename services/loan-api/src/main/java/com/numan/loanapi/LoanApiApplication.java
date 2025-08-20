package com.numan.loanapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.PropertySource;

@SpringBootApplication
@EnableConfigurationProperties
@PropertySource("classpath:application-common.yml")  // Load shared config
public class LoanApiApplication {
    public static void main(String[] args) {
        SpringApplication.run(LoanApiApplication.class, args);
    }
}