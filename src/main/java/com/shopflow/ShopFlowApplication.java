package com.shopflow;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableAsync
@EnableScheduling
@EnableConfigurationProperties
public class ShopFlowApplication {

    // Bước 1: Khởi chạy ứng dụng Spring Boot ShopFlow.
    public static void main(String[] args) {
        SpringApplication.run(ShopFlowApplication.class, args);
    }
}
