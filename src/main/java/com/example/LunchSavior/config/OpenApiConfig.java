package com.example.LunchSavior.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI lunchSaviorOpenAPI() {
        String schemeName = "Bearer Authentication";
        SecurityScheme securityScheme = new SecurityScheme()
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT");

        return new OpenAPI()
                .info(new Info()
                        .title("Lunch Savior API")
                        .description("解決今天吃什麼的 API 服務")
                        .version("v1.0"))
                .components(new Components().addSecuritySchemes(schemeName, securityScheme))
                .addSecurityItem(new SecurityRequirement().addList(schemeName));
    }
}