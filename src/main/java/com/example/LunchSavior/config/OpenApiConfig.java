package com.example.LunchSavior.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI lunchSaviorOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Lunch Savior API")
                        .description("解決午餐吃什麼的 API 服務")
                        .version("v1.0"));
    }
}