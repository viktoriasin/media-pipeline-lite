package ru.sinvic.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "playback_session")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class PlaybackSession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String sessionId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "content_id", nullable = false)
    private Content content;

    @Enumerated(EnumType.STRING)
    @Column(name = "device_type", nullable = false)
    private DeviceType deviceType;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "last_activity_at")
    private Instant lastActivityAt;

    @OneToMany(mappedBy = "session", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AnalyticsEvent> events = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        createdAt = Instant.now();
        lastActivityAt = Instant.now();
    }

    public PlaybackSession(String sessionId, Content content, DeviceType deviceType) {
        this.sessionId = sessionId;
        this.content = content;
        this.deviceType = deviceType;
    }

    public void updateActivity() {
        this.lastActivityAt = Instant.now();
    }

    public void addEvent(AnalyticsEvent event) {
        events.add(event);
        event.setSession(this);
    }

    public enum DeviceType {
        MOBILE,
        TABLET,
        DESKTOP,
        TV,
        UNKNOWN
    }
}
