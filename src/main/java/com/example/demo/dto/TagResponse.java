package com.example.demo.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "DTO тега в ответе API")
public record TagResponse(
        @Schema(description = "ID тега", example = "1")
        Long id,
        @Schema(description = "Название тега", example = "Вегетарианское")
        String name) {
}
