package ru.sinvic.service;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.sinvic.domain.AnalyticsEvent;
import ru.sinvic.domain.PlaybackSession;
import ru.sinvic.dto.AnalyticsEventRequest;
import ru.sinvic.dto.QoeMetricsResponse;
import ru.sinvic.repository.AnalyticsEventRepository;
import ru.sinvic.repository.PlaybackSessionRepository;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class AnalyticsService {

    private final AnalyticsEventRepository eventRepository;
    private final PlaybackSessionRepository sessionRepository;
    private final Counter eventsCounter;
    private final Counter errorsCounter;
    private final MeterRegistry meterRegistry;

    public AnalyticsService(AnalyticsEventRepository eventRepository,
                            PlaybackSessionRepository sessionRepository,
                            MeterRegistry meterRegistry) {
        this.eventRepository = eventRepository;
        this.sessionRepository = sessionRepository;
        this.meterRegistry = meterRegistry;

        // Инициализация счетчиков Prometheus
        this.eventsCounter = Counter.builder("playback.events.total")
            .description("Общее количество событий воспроизведения")
            .register(meterRegistry);

        this.errorsCounter = Counter.builder("playback.errors.total")
            .description("Общее количество ошибок воспроизведения")
            .register(meterRegistry);

        // Регистрация gauge для активных сессий
        meterRegistry.gauge("playback.sessions.active", this, service -> {
            Instant fiveMinutesAgo = Instant.now().minus(5, ChronoUnit.MINUTES);
            return sessionRepository.countActiveSessions(fiveMinutesAgo);
        });
    }

    @Transactional
    public void recordEvent(AnalyticsEventRequest request) {
        PlaybackSession session = sessionRepository.findBySessionId(request.sessionId())
            .orElseThrow(() -> new IllegalArgumentException("Session not found: " + request.sessionId()));

        AnalyticsEvent event = new AnalyticsEvent(request.eventType());
        event.setPlaybackTimeSeconds(request.playbackTimeSeconds());
        event.setBufferDurationMs(request.bufferDurationMs());
        event.setQualityHeight(request.qualityHeight());
        event.setErrorCode(request.errorCode());
        event.setErrorMessage(request.errorMessage());

        session.addEvent(event);
        session.updateActivity();

        eventRepository.save(event);
        sessionRepository.save(session);

        // Обновление метрик Prometheus
        eventsCounter.increment();
        meterRegistry.counter("playback.events.by.type",
            "type", request.eventType().name()).increment();

        if (request.eventType() == AnalyticsEvent.EventType.ERROR) {
            errorsCounter.increment();
        }

        if (request.bufferDurationMs() != null && request.bufferDurationMs() > 0) {
            meterRegistry.summary("playback.buffer.duration.ms")
                .record(request.bufferDurationMs());
        }
    }

    @Transactional(readOnly = true)
    public QoeMetricsResponse calculateQoeMetrics(String sessionId) {
        PlaybackSession session = sessionRepository.findBySessionId(sessionId)
            .orElseThrow(() -> new IllegalArgumentException("Session not found: " + sessionId));

        List<AnalyticsEvent> events = eventRepository.findBySessionOrderByTimestampAsc(session);

        long totalBufferTimeMs = events.stream()
            .filter(e -> e.getEventType() == AnalyticsEvent.EventType.BUFFER)
            .mapToLong(e -> e.getBufferDurationMs() != null ? e.getBufferDurationMs() : 0)
            .sum();

        // Расчет времени воспроизведения от START до END (или последнего события)
        long totalPlayTimeMs = calculatePlayTimeMs(events);

        int errorCount = (int) events.stream()
            .filter(e -> e.getEventType() == AnalyticsEvent.EventType.ERROR)
            .count();

        int qualityChanges = (int) events.stream()
            .filter(e -> e.getEventType() == AnalyticsEvent.EventType.QUALITY_CHANGE)
            .count();

        double bufferRatio = calculateBufferRatio(totalBufferTimeMs, totalPlayTimeMs);
        String grade = calculateGrade(bufferRatio, errorCount);

        return new QoeMetricsResponse(
            sessionId,
            bufferRatio,
            totalBufferTimeMs,
            totalPlayTimeMs,
            errorCount,
            qualityChanges,
            grade,
            events.size()
        );
    }

    private long calculatePlayTimeMs(List<AnalyticsEvent> events) {
        if (events.isEmpty()) {
            return 0;
        }

        Instant startTime = events.stream()
            .filter(e -> e.getEventType() == AnalyticsEvent.EventType.START)
            .map(AnalyticsEvent::getTimestamp)
            .findFirst()
            .orElse(events.get(0).getTimestamp());

        Instant endTime = events.stream()
            .filter(e -> e.getEventType() == AnalyticsEvent.EventType.END)
            .map(AnalyticsEvent::getTimestamp)
            .findFirst()
            .orElse(events.get(events.size() - 1).getTimestamp());

        return ChronoUnit.MILLIS.between(startTime, endTime);
    }

    private double calculateBufferRatio(long bufferTimeMs, long playTimeMs) {
        if (playTimeMs == 0) {
            return 0.0;
        }
        return (double) bufferTimeMs / (double) (bufferTimeMs + playTimeMs);
    }

    /**
     * Расчет оценки QoE:
     * - Excellent: buffer ratio < 1%, нет ошибок
     * - Good: buffer ratio < 2%, <= 1 ошибка
     * - Fair: buffer ratio < 5%, <= 2 ошибки
     * - Poor: все что хуже
     */
    private String calculateGrade(double bufferRatio, int errorCount) {
        if (bufferRatio < 0.01 && errorCount == 0) {
            return "Excellent";
        } else if (bufferRatio < 0.02 && errorCount <= 1) {
            return "Good";
        } else if (bufferRatio < 0.05 && errorCount <= 2) {
            return "Fair";
        } else {
            return "Poor";
        }
    }
}
