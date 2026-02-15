package ru.sinvic.dto;

import ru.sinvic.domain.Content;

public record ContentDto (long id, String title, Integer durationSeconds, String baseUrl) {

    public ContentDto {
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("title must not be empty");
        }

        if (baseUrl == null || baseUrl.isBlank()) {
            throw new IllegalArgumentException("baseUrl must not be empty");
        }

        if (durationSeconds == null || durationSeconds <= 0) {
            throw new IllegalArgumentException("durationSeconds must not be null or <= 0");
        }
    }

    public static ContentDto from(Content content) {
        return new ContentDto(content.getId(), content.getTitle(), content.getDurationSeconds(), content.getBaseUrl());
    }

    public Content toDomain() {
        Content content = new Content();
        content.setId(this.id);
        content.setTitle(this.title);
        content.setDurationSeconds(this.durationSeconds);
        content.setBaseUrl(this.baseUrl);
        return content;
    }
}

