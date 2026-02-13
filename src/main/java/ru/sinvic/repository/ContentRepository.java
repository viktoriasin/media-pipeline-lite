package ru.sinvic.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.sinvic.domain.Content;

import java.util.Optional;

@Repository
public interface ContentRepository extends JpaRepository<Content, Long> {

    @Query("SELECT c FROM Content c LEFT JOIN FETCH c.timeline WHERE c.id = :id")
    Optional<Content> findByIdWithTimeline(Long id);
}
