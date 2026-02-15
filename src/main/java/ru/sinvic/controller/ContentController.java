package ru.sinvic.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import ru.sinvic.dto.ContentDto;
import ru.sinvic.service.ContentService;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ContentController {

    private final ContentService contentService;

    @GetMapping("/api/content")
    public ResponseEntity<List<ContentDto>> getAllContent() {
        return ResponseEntity.ok(contentService.getAllContent());
    }

    @GetMapping("/api/content/{id}")
    public ResponseEntity<ContentDto> getContent(@PathVariable Long id) {
        return ResponseEntity.ok(contentService.getContent(id));
    }
}
