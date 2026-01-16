package com.iot.soil;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class BusLivingLabApplication {

    public static void main(String[] args) {
        SpringApplication.run(BusLivingLabApplication.class, args);
        System.out.println("BUS Living Lab 2026 - Sensor Data API started successfully!");
    }
}

