package ru.sinvic.service;

import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.sinvic.domain.AnalyticsEvent;
import ru.sinvic.domain.PlaybackSession;
import ru.sinvic.dto.AnalyticsEventRequest;
import ru.sinvic.repository.AnalyticsEventRepository;
import ru.sinvic.repository.PlaybackSessionRepository;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AnalyticsServiceTest {

    @Mock
    private AnalyticsEventRepository analyticsEventRepository;

    @Mock
    private PlaybackSessionRepository playbackSessionRepository;

    private AnalyticsService analyticsService;

    private AnalyticsEventRequest sampleRequest;

    private final String testSessionId = "test-session-123";

    @BeforeEach
    void setUp() {
        PlaybackSession testSession = new PlaybackSession();
        testSession.setSessionId(testSessionId);

        analyticsService = new AnalyticsService(
            analyticsEventRepository,
            playbackSessionRepository,
            new SimpleMeterRegistry()
        );

        when(playbackSessionRepository.findBySessionId(testSessionId))
            .thenReturn(Optional.of(testSession));

        sampleRequest = new AnalyticsEventRequest(
            testSessionId,
            AnalyticsEvent.EventType.PLAY,
            10.5,
            null,
            null,
            null,
            null
        );
    }

    @Test
    void recordEvent_shouldSaveToRepository() {
        when(analyticsEventRepository.save(any(AnalyticsEvent.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));
        when(playbackSessionRepository.save(any(PlaybackSession.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));

        analyticsService.recordEvent(sampleRequest);

        verify(analyticsEventRepository, times(1)).save(any(AnalyticsEvent.class));
        verify(playbackSessionRepository, times(1)).save(any(PlaybackSession.class));
    }

    @Test
    void recordEvent_withStartEvent_shouldProcessCorrectly() {
        AnalyticsEventRequest startEvent = new AnalyticsEventRequest(
            testSessionId,
            AnalyticsEvent.EventType.START,
            0.0,
            null,
            1080,
            null,
            null
        );
        when(analyticsEventRepository.save(any(AnalyticsEvent.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));
        when(playbackSessionRepository.save(any(PlaybackSession.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));

        analyticsService.recordEvent(startEvent);

        verify(analyticsEventRepository).save(argThat(event ->
            event.getEventType() == AnalyticsEvent.EventType.START &&
                event.getPlaybackTimeSeconds() == 0.0 &&
                event.getQualityHeight() == 1080
        ));
    }

    @Test
    void recordEvent_withBufferEvent_shouldIncludeBufferDuration() {
        AnalyticsEventRequest bufferEvent = new AnalyticsEventRequest(
            testSessionId,
            AnalyticsEvent.EventType.BUFFER,
            45.5,
            2500L,
            null,
            null,
            null
        );
        when(analyticsEventRepository.save(any(AnalyticsEvent.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));
        when(playbackSessionRepository.save(any(PlaybackSession.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));

        analyticsService.recordEvent(bufferEvent);

        verify(analyticsEventRepository).save(argThat(event ->
            event.getEventType() == AnalyticsEvent.EventType.BUFFER &&
                event.getBufferDurationMs() == 2500L &&
                event.getPlaybackTimeSeconds() == 45.5
        ));
    }

    @Test
    void recordEvent_withErrorEvent_shouldIncludeErrorDetails() {
        AnalyticsEventRequest errorEvent = new AnalyticsEventRequest(
            testSessionId,
            AnalyticsEvent.EventType.ERROR,
            120.0,
            null,
            null,
            "NETWORK_ERROR",
            "Failed to load segment"
        );
        when(analyticsEventRepository.save(any(AnalyticsEvent.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));
        when(playbackSessionRepository.save(any(PlaybackSession.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));

        analyticsService.recordEvent(errorEvent);

        verify(analyticsEventRepository).save(argThat(event ->
            event.getEventType() == AnalyticsEvent.EventType.ERROR &&
                event.getErrorCode().equals("NETWORK_ERROR") &&
                event.getErrorMessage().equals("Failed to load segment") &&
                event.getPlaybackTimeSeconds() == 120.0
        ));
    }

    @Test
    void recordEvent_multipleEventsForSameSession_shouldProcessIndependently() {
        AnalyticsEventRequest event1 = new AnalyticsEventRequest(
            testSessionId, AnalyticsEvent.EventType.START, 0.0, null, 1080, null, null
        );
        AnalyticsEventRequest event2 = new AnalyticsEventRequest(
            testSessionId, AnalyticsEvent.EventType.PLAY, 5.0, null, 1080, null, null
        );
        AnalyticsEventRequest event3 = new AnalyticsEventRequest(
            testSessionId, AnalyticsEvent.EventType.PAUSE, 10.0, null, 1080, null, null
        );
        when(analyticsEventRepository.save(any(AnalyticsEvent.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));
        when(playbackSessionRepository.save(any(PlaybackSession.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));

        analyticsService.recordEvent(event1);
        analyticsService.recordEvent(event2);
        analyticsService.recordEvent(event3);

        verify(analyticsEventRepository, times(3)).save(any(AnalyticsEvent.class));
        verify(playbackSessionRepository, times(3)).save(any(PlaybackSession.class));
    }
}
