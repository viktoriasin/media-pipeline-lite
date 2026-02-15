package ru.sinvic.controller;

import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.sinvic.domain.PlaybackSession;
import ru.sinvic.dto.StartPlaybackRequest;
import ru.sinvic.dto.StartPlaybackResponse;
import ru.sinvic.service.ManifestService;
import ru.sinvic.service.PlaybackService;

@RestController
public class PlaybackController {

    private final PlaybackService playbackService;
    private final ManifestService manifestService;

    public PlaybackController(PlaybackService playbackService, ManifestService manifestService) {
        this.playbackService = playbackService;
        this.manifestService = manifestService;
    }

    @PostMapping("/api/playback/start")
    public ResponseEntity<StartPlaybackResponse> startPlayback(
        @Valid @RequestBody StartPlaybackRequest request
    ) {
        StartPlaybackResponse response = playbackService.startPlayback(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping(value = "/api/playback/session/{sessionId}/master.m3u8", produces = "application/vnd.apple.mpegurl")
    public ResponseEntity<String> getMasterPlaylist(@PathVariable String sessionId) {
        PlaybackSession session = playbackService.getSession(sessionId);
        String contentPath = session.getContent().getContentPath();
        String manifest = manifestService.generateMasterPlaylist(contentPath);
        return ResponseEntity.ok()
            .contentType(MediaType.parseMediaType("application/vnd.apple.mpegurl"))
            .body(manifest);
    }
}
