package ru.sinvic.dto;

import java.util.List;

public record StartPlaybackResponse(
    String sessionId,
    String masterPlaylistUrl,
    List<TimelineEventDto> timeline
) {
}
