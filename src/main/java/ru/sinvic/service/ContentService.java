package ru.sinvic.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.sinvic.dto.ContentDto;
import ru.sinvic.exception.ContentNotFoundException;
import ru.sinvic.repository.ContentRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ContentService {
    private final ContentRepository contentRepository;

    public List<ContentDto> getAllContent() {
        return contentRepository.findAll().stream()
            .map(ContentDto::from)
            .toList();
    }

    public ContentDto getContent(Long id) {
        return contentRepository.findByIdWithTimeline(id)
            .map(ContentDto::from)
            .orElseThrow(() -> new ContentNotFoundException("Content not found: " + id));
    }
}

