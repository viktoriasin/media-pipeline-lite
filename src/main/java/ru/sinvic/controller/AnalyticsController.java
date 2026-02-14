package ru.sinvic.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.sinvic.dto.AnalyticsEventRequest;
import ru.sinvic.dto.QoeMetricsResponse;
import ru.sinvic.service.AnalyticsService;

@RestController
@RequiredArgsConstructor
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    @PostMapping("/api/analytics/event")
    public ResponseEntity<Void> recordEvent(@Valid @RequestBody AnalyticsEventRequest request) {
        analyticsService.recordEvent(request);
        return ResponseEntity.accepted().build();
    }

    @GetMapping("/api/analytics/session/{sessionId}/qoe")
    public ResponseEntity<QoeMetricsResponse> getQoeMetrics(@PathVariable String sessionId) {
        QoeMetricsResponse metrics = analyticsService.calculateQoeMetrics(sessionId);
        return ResponseEntity.ok(metrics);
    }
}
