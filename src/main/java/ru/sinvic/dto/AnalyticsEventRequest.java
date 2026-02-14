package ru.sinvic.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import ru.sinvic.domain.AnalyticsEvent;

public record AnalyticsEventRequest(
    @NotBlank String sessionId,
    @NotNull AnalyticsEvent.EventType eventType,
    Double playbackTimeSeconds,
    Long bufferDurationMs,
    Integer qualityHeight,
    String errorCode,
    String errorMessage
) {
}
