package ru.sinvic.dto;

import ru.sinvic.domain.Content;
import ru.sinvic.domain.TimelineEvent;

import java.util.List;

public record ContentDto (long id, String title, Integer durationSeconds, String contentPath, List<TimelineEvent> timeline) {

    public ContentDto {
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("title must not be empty");
        }

        if (contentPath == null || contentPath.isBlank()) {
            throw new IllegalArgumentException("contentPath must not be empty");
        }

        if (durationSeconds == null || durationSeconds <= 0) {
            throw new IllegalArgumentException("durationSeconds must not be null or <= 0");
        }
    }

    public static ContentDto from(Content content) {
        return new ContentDto(content.getId(), content.getTitle(), content.getDurationSeconds(), content.getContentPath(), content.getTimeline());
    }

    public Content toDomain() {
        Content content = new Content();
        content.setId(this.id);
        content.setTitle(this.title);
        content.setDurationSeconds(this.durationSeconds);
        content.setContentPath(this.contentPath);
        content.setTimeline(this.timeline);
        return content;
    }
}

