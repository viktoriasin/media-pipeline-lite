package ru.sinvic.service;

import org.springframework.stereotype.Service;
import ru.sinvic.config.MediaPipelineProperties;
import ru.sinvic.domain.PlaybackSession;

import java.util.List;

@Service
public class ManifestService {

    private final MediaPipelineProperties properties;

    public ManifestService(MediaPipelineProperties properties) {
        this.properties = properties;
    }

    public String generateMasterPlaylist(PlaybackSession session) {
        StringBuilder m3u8 = new StringBuilder();
        m3u8.append("#EXTM3U\n");
        m3u8.append("#EXT-X-VERSION:3\n\n");

        List<MediaPipelineProperties.QualityProfile> profiles = properties.getQualityProfiles();
        String baseUrl = session.getContent().getBaseUrl();

        for (MediaPipelineProperties.QualityProfile profile : profiles) {
            m3u8.append(String.format("#EXT-X-STREAM-INF:BANDWIDTH=%d,RESOLUTION=%dx%d,CODECS=\"%s\"\n",
                profile.getBitrate(),
                profile.getWidth(),
                profile.getHeight(),
                profile.getCodecs()
            ));
            m3u8.append(String.format("%s/%dp/playlist.m3u8\n\n", baseUrl, profile.getHeight()));
        }

        return m3u8.toString();
    }
}
