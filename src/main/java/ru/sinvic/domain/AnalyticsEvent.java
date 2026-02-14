package ru.sinvic.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Entity
@Table(name = "analytics_event", indexes = {
    @Index(name = "idx_session_type", columnList = "session_id,event_type"),
    @Index(name = "idx_session_timestamp", columnList = "session_id,timestamp")
})
@Getter
@Setter
@NoArgsConstructor
public class AnalyticsEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "session_id", nullable = false)
    private PlaybackSession session;

    @Enumerated(EnumType.STRING)
    @Column(name = "event_type", nullable = false)
    private EventType eventType;

    @Column(nullable = false)
    private Instant timestamp;

    @Column(name = "playback_time_seconds")
    private Double playbackTimeSeconds;

    @Column(name = "buffer_duration_ms")
    private Long bufferDurationMs;

    @Column(name = "quality_height")
    private Integer qualityHeight;

    @Column(name = "error_code")
    private String errorCode;

    @Column(name = "error_message")
    private String errorMessage;

    @PrePersist
    protected void onCreate() {
        if (timestamp == null) {
            timestamp = Instant.now();
        }
    }

    public AnalyticsEvent(EventType eventType) {
        this.eventType = eventType;
        this.timestamp = Instant.now();
    }

    public enum EventType {
        START,
        PLAY,
        PAUSE,
        BUFFER,
        QUALITY_CHANGE,
        SKIP_INTRO,
        SEEK,
        ERROR,
        HEARTBEAT,
        END
    }
}
