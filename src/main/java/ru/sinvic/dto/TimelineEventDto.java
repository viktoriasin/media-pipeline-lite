package ru.sinvic.dto;


import ru.sinvic.domain.TimelineEvent;

public record TimelineEventDto(
    String type,
    Integer startTimeSeconds,
    Integer endTimeSeconds,
    String label,
    Boolean skippable
) {
    public static TimelineEventDto from(TimelineEvent event) {
        return new TimelineEventDto(
            event.getType().name(),
            event.getStartTimeSeconds(),
            event.getEndTimeSeconds(),
            event.getLabel(),
            event.getSkippable()
        );
    }
}
