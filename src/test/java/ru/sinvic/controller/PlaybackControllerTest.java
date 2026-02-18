package ru.sinvic.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.sinvic.domain.Content;
import ru.sinvic.domain.PlaybackSession;
import ru.sinvic.dto.StartPlaybackRequest;
import ru.sinvic.dto.StartPlaybackResponse;
import ru.sinvic.service.ManifestService;
import ru.sinvic.service.PlaybackService;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(PlaybackController.class)
class PlaybackControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private PlaybackService playbackService;

    @MockBean
    private ManifestService manifestService;

    @Test
    void startPlayback_shouldStartSession() throws Exception {
        StartPlaybackRequest request = new StartPlaybackRequest(1L);
        StartPlaybackResponse response = new StartPlaybackResponse(
                "session-123",
                "/api/playback/session/session-123/master.m3u8",
                List.of()
        );

        when(playbackService.startPlayback(any(StartPlaybackRequest.class)))
                .thenReturn(response);

        mockMvc.perform(post("/api/playback/start")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.sessionId").value("session-123"))
                .andExpect(jsonPath("$.masterPlaylistUrl").value("/api/playback/session/session-123/master.m3u8"))
                .andExpect(jsonPath("$.timeline").isArray());
    }

    @Test
    void startPlayback_shouldReturnBadRequest_whenInvalidRequest() throws Exception {
        String invalidRequest = "{\"contentId\": null}";

        mockMvc.perform(post("/api/playback/start")
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidRequest))
                .andExpect(status().isBadRequest());
    }

    @Test
    void startPlayback_shouldReturnBadRequest_whenNegativeContentId() throws Exception {
        StartPlaybackRequest request = new StartPlaybackRequest(-1L);

        mockMvc.perform(post("/api/playback/start")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void startPlayback_shouldReturnBadRequest_whenZeroContentId() throws Exception {
        StartPlaybackRequest request = new StartPlaybackRequest(0L);

        mockMvc.perform(post("/api/playback/start")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getMasterPlaylist_shouldReturnPlaylist() throws Exception {
        String sessionId = "session-123";
        String contentPath = "/content/movie1";
        String masterPlaylist = "#EXTM3U\n#EXT-X-VERSION:3\n#EXT-X-STREAM-INF:BANDWIDTH=800000\nplaylist-720p.m3u8";

        Content content = new Content();
        content.setContentPath(contentPath);

        PlaybackSession session = new PlaybackSession();
        session.setSessionId(sessionId);
        session.setContent(content);

        when(playbackService.getSession(sessionId)).thenReturn(session);
        when(manifestService.generateMasterPlaylist(contentPath)).thenReturn(masterPlaylist);

        mockMvc.perform(get("/api/playback/session/{sessionId}/master.m3u8", sessionId))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/vnd.apple.mpegurl"))
                .andExpect(content().string(masterPlaylist));
    }
}
