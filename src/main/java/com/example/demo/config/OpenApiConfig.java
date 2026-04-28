package com.example.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityScheme;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI demoOpenAPI() {
        return new OpenAPI()
                .components(new Components()
                        .addSecuritySchemes("bearerAuth", new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .description("JWT access token. Формат заголовка: Bearer {token}")))
                .info(new Info()
                        .title("Demo Spring Boot API")
                        .version("1.0.0")
                        .description("Учебный API для изучения слоёв Spring Boot: controller, dto, entity, service, validator, repository, middleware, config, utils. Включены JWT, роли, security best practices и профили БД."));
    }
}