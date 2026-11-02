package com.bharath.stockroom;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class StockroomApplication {
    public static void main(String[] args) { SpringApplication.run(StockroomApplication.class, args); }
}
