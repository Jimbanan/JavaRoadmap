package ru.personal.baserestcontroller.dto;

public record TaskDto(
        String id,
        String title,
        Boolean completed
) {
}
