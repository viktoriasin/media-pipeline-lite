package ru.sinvic.service;


import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.sinvic.domain.Content;
import ru.sinvic.domain.PlaybackSession;
import ru.sinvic.dto.StartPlaybackRequest;
import ru.sinvic.dto.StartPlaybackResponse;
import ru.sinvic.dto.TimelineEventDto;
import ru.sinvic.exception.ContentNotFoundException;
import ru.sinvic.exception.SessionNotFoundException;
import ru.sinvic.repository.ContentRepository;
import ru.sinvic.repository.PlaybackSessionRepository;

import java.util.List;
import java.util.UUID;

@Service
public class PlaybackService {

    private final ContentRepository contentRepository;
    private final PlaybackSessionRepository sessionRepository;

    public PlaybackService(ContentRepository contentRepository,
                           PlaybackSessionRepository sessionRepository) {
        this.contentRepository = contentRepository;
        this.sessionRepository = sessionRepository;
    }

    @Transactional
    public StartPlaybackResponse startPlayback(StartPlaybackRequest request) {
        Content content = contentRepository.findByIdWithTimeline(request.contentId())
            .orElseThrow(() -> new ContentNotFoundException("Content not found: " + request.contentId()));

        String sessionId = UUID.randomUUID().toString();

        // Создаем сессию без предварительного выбора качества
        // HLS.js клиент сам выберет оптимальное качество на основе bandwidth
        PlaybackSession session = new PlaybackSession(
            sessionId,
            content,
            PlaybackSession.DeviceType.UNKNOWN
        );

        sessionRepository.save(session);

        String masterPlaylistUrl = String.format(
            "/api/playback/session/%s/master.m3u8",
            sessionId
        );

        List<TimelineEventDto> timeline = content.getTimeline().stream()
            .map(TimelineEventDto::from)
            .toList();

        return new StartPlaybackResponse(
            sessionId,
            masterPlaylistUrl,
            timeline
        );
    }

    @Transactional(readOnly = true)
    public PlaybackSession getSession(String sessionId) {
        return sessionRepository.findBySessionId(sessionId)
            .orElseThrow(() -> new SessionNotFoundException("Session not found: " + sessionId));
    }
}
