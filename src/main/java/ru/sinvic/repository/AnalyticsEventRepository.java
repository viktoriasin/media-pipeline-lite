package ru.sinvic.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.sinvic.domain.AnalyticsEvent;
import ru.sinvic.domain.PlaybackSession;

import java.util.List;

@Repository
public interface AnalyticsEventRepository extends JpaRepository<AnalyticsEvent, Long> {

    List<AnalyticsEvent> findBySessionOrderByTimestampAsc(PlaybackSession session);

    long countByEventType(AnalyticsEvent.EventType eventType);
}
