package com.iot.soil;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SoilApplication {

    public static void main(String[] args) {
        SpringApplication.run(SoilApplication.class, args);
        System.out.println("Sensor Data API started successfully!");
    }
}