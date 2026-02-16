package ru.sinvic.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import ru.sinvic.dto.ContentDto;
import ru.sinvic.service.ContentService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
public class ContentController {

    private final ContentService contentService;

    @GetMapping("/api/content")
    public ResponseEntity<List<ContentDto>> getAllContent() {
        return ResponseEntity.ok(contentService.getAllContent());
    }

    @GetMapping("/api/content/{id}")
    public ResponseEntity<ContentDto> getContent(@PathVariable Long id) {
        log.info("Received get request for content id={}", id);
        return ResponseEntity.ok(contentService.getContent(id));
    }
}
