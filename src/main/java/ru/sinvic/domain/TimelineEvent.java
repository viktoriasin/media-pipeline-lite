package ru.sinvic.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

// TODO: проверить нужен ли здест конструктор со всеми значениями (jpa)

@Entity
@Table(name = "timeline_event")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TimelineEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "content_id", nullable = false)
    private Content content;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TimelineEventType type;

    @Column(name = "start_time_seconds", nullable = false)
    private Integer startTimeSeconds;

    @Column(name = "end_time_seconds", nullable = false)
    private Integer endTimeSeconds;

    @Column(nullable = false)
    private String label;

    @Column(nullable = false)
    private Boolean skippable;

    public enum TimelineEventType {
        INTRO,
        CREDITS,
        CHAPTER,
        RECAP
    }
}
