package ru.sinvic.dto;

import ru.sinvic.domain.Content;

public record ContentDto (long id, String title, int durationSeconds, String baseUrl) {
    public static ContentDto from(Content content) {
        return new ContentDto(content.getId(), content.getTitle(), content.getDurationSeconds(), content.getBaseUrl());
    }
}

