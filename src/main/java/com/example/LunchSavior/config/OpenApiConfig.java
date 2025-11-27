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
        // 1. 定義鎖頭 (Security Scheme)
        String schemeName = "Bearer Authentication";
        SecurityScheme securityScheme = new SecurityScheme()
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT");

        // 2. 組合起來
        return new OpenAPI()
                .info(new Info()
                        .title("Lunch Savior API")
                        .description("解決午餐吃什麼的 API 服務")
                        .version("v1.0"))
                // [重點 1] 把鎖加入 Components (倉庫)
                .components(new Components().addSecuritySchemes(schemeName, securityScheme))
                // [重點 2] 把鎖掛上去 (全域套用)
                .addSecurityItem(new SecurityRequirement().addList(schemeName));
    }
}