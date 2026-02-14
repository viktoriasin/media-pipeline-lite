package ru.sinvic.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.sinvic.dto.AnalyticsEventRequest;
import ru.sinvic.dto.QoeMetricsResponse;
import ru.sinvic.service.AnalyticsService;

@RestController
@RequestMapping("/api/analytics")
@RequiredArgsConstructor
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    @PostMapping("/event")
    public ResponseEntity<Void> recordEvent(@Valid @RequestBody AnalyticsEventRequest request) {
        analyticsService.recordEvent(request);
        return ResponseEntity.accepted().build();
    }

    @GetMapping("/session/{sessionId}/qoe")
    public ResponseEntity<QoeMetricsResponse> getQoeMetrics(@PathVariable String sessionId) {
        QoeMetricsResponse metrics = analyticsService.calculateQoeMetrics(sessionId);
        return ResponseEntity.ok(metrics);
    }
}
