package ru.sinvic.controller;



import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/playback")
public class PlaybackController {

    private final PlaybackService playbackService;
    private final ManifestService manifestService;

    public PlaybackController(PlaybackService playbackService, ManifestService manifestService) {
        this.playbackService = playbackService;
        this.manifestService = manifestService;
    }

    @PostMapping("/start")
    public ResponseEntity<StartPlaybackResponse> startPlayback(
        @Valid @RequestBody StartPlaybackRequest request
    ) {
        StartPlaybackResponse response = playbackService.startPlayback(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping(value = "/session/{sessionId}/master.m3u8", produces = "application/vnd.apple.mpegurl")
    public ResponseEntity<String> getMasterPlaylist(@PathVariable String sessionId) {
        com.kinopoisk.mediapipeline.domain.PlaybackSession session = playbackService.getSession(sessionId);
        String manifest = manifestService.generateMasterPlaylist(session);
        return ResponseEntity.ok()
            .contentType(MediaType.parseMediaType("application/vnd.apple.mpegurl"))
            .body(manifest);
    }
}
