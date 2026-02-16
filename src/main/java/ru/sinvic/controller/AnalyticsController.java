package ru.sinvic.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.sinvic.dto.AnalyticsEventRequest;
import ru.sinvic.dto.QoeMetricsResponse;
import ru.sinvic.kafka.AnalyticsEventProducer;
import ru.sinvic.service.AnalyticsService;

@RestController
@RequiredArgsConstructor
@Slf4j
public class AnalyticsController {

    private final AnalyticsEventProducer eventProducer;
    private final AnalyticsService analyticsService;

    @PostMapping("/api/analytics/event")
    public ResponseEntity<Void> recordEvent(@Valid @RequestBody AnalyticsEventRequest request) {
        log.info("Received analytics event: type={}, sessionId={}",
            request.eventType(), request.sessionId());

        eventProducer.sendEvent(request);

        return ResponseEntity.accepted().build();
    }

    @GetMapping("/api/analytics/session/{sessionId}/qoe")
    public ResponseEntity<QoeMetricsResponse> getQoeMetrics(@PathVariable String sessionId) {
        QoeMetricsResponse metrics = analyticsService.calculateQoeMetrics(sessionId);
        return ResponseEntity.ok(metrics);
    }
}
