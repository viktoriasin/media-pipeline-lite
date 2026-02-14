package ru.sinvic.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.sinvic.domain.PlaybackSession;

import java.time.Instant;
import java.util.Optional;

@Repository
public interface PlaybackSessionRepository extends JpaRepository<PlaybackSession, Long> {

    Optional<PlaybackSession> findBySessionId(String sessionId);

    @Query("SELECT COUNT(s) FROM PlaybackSession s WHERE s.lastActivityAt > :since")
    long countActiveSessions(Instant since);
}
