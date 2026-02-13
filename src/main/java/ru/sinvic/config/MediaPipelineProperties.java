package ru.sinvic.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

// TODO: проверить ситуации как будет работать если будут использованы поля по умолчанию

@Component
@ConfigurationProperties(prefix = "media-pipeline")
public class MediaPipelineProperties {

    private int segmentDurationSeconds = 6;
    private HlsConfig hls = new HlsConfig();
    private List<QualityProfile> qualityProfiles = new ArrayList<>();

    public int getSegmentDurationSeconds() {
        return segmentDurationSeconds;
    }

    public void setSegmentDurationSeconds(int segmentDurationSeconds) {
        this.segmentDurationSeconds = segmentDurationSeconds;
    }

    public HlsConfig getHls() {
        return hls;
    }

    public void setHls(HlsConfig hls) {
        this.hls = hls;
    }

    public List<QualityProfile> getQualityProfiles() {
        return qualityProfiles;
    }

    public void setQualityProfiles(List<QualityProfile> qualityProfiles) {
        this.qualityProfiles = qualityProfiles;
    }

    public static class HlsConfig {
        private String masterPlaylistName = "master.m3u8";
        private String mediaPlaylistName = "playlist.m3u8";

        public String getMasterPlaylistName() {
            return masterPlaylistName;
        }

        public void setMasterPlaylistName(String masterPlaylistName) {
            this.masterPlaylistName = masterPlaylistName;
        }

        public String getMediaPlaylistName() {
            return mediaPlaylistName;
        }

        public void setMediaPlaylistName(String mediaPlaylistName) {
            this.mediaPlaylistName = mediaPlaylistName;
        }
    }

    public static class QualityProfile {
        private int height;
        private int width;
        private long bitrate;
        private String codecs;
        private String level;

        public int getHeight() {
            return height;
        }

        public void setHeight(int height) {
            this.height = height;
        }

        public int getWidth() {
            return width;
        }

        public void setWidth(int width) {
            this.width = width;
        }

        public long getBitrate() {
            return bitrate;
        }

        public void setBitrate(long bitrate) {
            this.bitrate = bitrate;
        }

        public String getCodecs() {
            return codecs;
        }

        public void setCodecs(String codecs) {
            this.codecs = codecs;
        }

        public String getLevel() {
            return level;
        }

        public void setLevel(String level) {
            this.level = level;
        }
    }
}
