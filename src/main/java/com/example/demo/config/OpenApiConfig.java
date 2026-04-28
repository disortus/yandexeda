package com.example.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI demoOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Demo Spring Boot API")
                        .version("1.0.0")
                        .description("Учебный API для изучения слоёв Spring Boot: controller, dto, entity, service, validator, repository, middleware, config, utils."));
    }
}