package ru.sinvic.dto;

public record QoeMetricsResponse(
    String sessionId,
    double bufferRatio,
    long totalBufferTimeMs,
    long totalPlayTimeMs,
    int errorCount,
    int qualityChanges,
    String grade,
    int eventsCount
) {
}
