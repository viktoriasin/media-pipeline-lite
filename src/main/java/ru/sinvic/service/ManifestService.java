package ru.sinvic.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import ru.sinvic.config.MediaPipelineProperties;

import java.util.List;

@Service
@Slf4j
public class ManifestService {

    private final MediaPipelineProperties properties;

    public ManifestService(MediaPipelineProperties properties) {
        this.properties = properties;
    }

    @Cacheable(
        value = "master-playlists",
        key = "#contentPath",
        unless = "#result == null"
    )
    public String generateMasterPlaylist(String contentPath) {
        StringBuilder m3u8 = new StringBuilder();
        m3u8.append("#EXTM3U\n");
        m3u8.append("#EXT-X-VERSION:3\n\n");

        List<MediaPipelineProperties.QualityProfile> profiles = properties.getQualityProfiles();

        for (MediaPipelineProperties.QualityProfile profile : profiles) {
            m3u8.append(String.format("#EXT-X-STREAM-INF:BANDWIDTH=%d,RESOLUTION=%dx%d,CODECS=\"%s\"\n",
                profile.getBitrate(),
                profile.getWidth(),
                profile.getHeight(),
                profile.getCodecs()
            ));
            m3u8.append(String.format("%s/%dp/playlist.m3u8\n\n", contentPath, profile.getHeight()));
        }
        return m3u8.toString();
    }

    @CacheEvict(
        value = "master-playlists",
        allEntries = true
    )
    public void clearMasterPlaylistCache() {
        log.info("Master playlist cache cleared - all entries evicted");
    }

    @CacheEvict(
        value = "master-playlists",
        key = "#contentPath"
    )
    public void clearMasterPlaylistCache(String contentPath) {
        log.info("Master playlist cache cleared for contentPath: {}", contentPath);
    }
}
