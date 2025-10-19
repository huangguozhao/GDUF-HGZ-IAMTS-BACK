package com.victor.iatms;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class IatmsApplication {

    public static void main(String[] args) {
        SpringApplication.run(IatmsApplication.class, args);
    }

}
