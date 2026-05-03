package com.example.demo.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "DTO категории в ответе API")
public record CategoryResponse(
        @Schema(description = "ID категории", example = "1")
        Long id,
        @Schema(description = "Название категории", example = "Пиццы")
        String name) {
}
