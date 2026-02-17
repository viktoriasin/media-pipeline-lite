package ru.sinvic.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.CacheManager;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.sinvic.service.ManifestService;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@Slf4j
public class AdminController {

    private final ManifestService manifestService;
    private final CacheManager cacheManager;

    @PostMapping("/cache/clear/master-playlists")
    public ResponseEntity<String> clearMasterPlaylistCache() {
        log.info("Admin: Clearing master playlist cache");
        manifestService.clearMasterPlaylistCache();
        return ResponseEntity.ok("Master playlist cache cleared successfully");
    }

    @PostMapping("/cache/clear/master-playlists/{contentPath}")
    public ResponseEntity<String> clearMasterPlaylistCache(@PathVariable String contentPath) {
        log.info("Admin: Clearing master playlist cache for contentPath: {}", contentPath);
        manifestService.clearMasterPlaylistCache(contentPath);
        return ResponseEntity.ok("Master playlist cache cleared for contentPath: " + contentPath);
    }

    @PostMapping("/cache/clear/all")
    public ResponseEntity<String> clearAllCaches() {
        log.warn("Admin: Clearing ALL caches");

        cacheManager.getCacheNames().forEach(cacheName -> {
            var cache = cacheManager.getCache(cacheName);
            if (cache != null) {
                cache.clear();
                log.info("Cache cleared: {}", cacheName);
            }
        });

        return ResponseEntity.ok("All caches cleared successfully");
    }

    @GetMapping("/cache/info")
    public ResponseEntity<?> getCacheInfo() {
        var cacheNames = cacheManager.getCacheNames();

        log.info("Admin: Retrieving cache info");

        return ResponseEntity.ok(new CacheInfoResponse(
            cacheNames.size(),
            cacheNames.stream().toList(),
            cacheManager.getClass().getSimpleName()
        ));
    }

    @GetMapping("/health/redis")
    public ResponseEntity<String> checkRedisHealth() {
        try {
            var cache = cacheManager.getCache("master-playlists");
            if (cache != null) {
                cache.get("health-check");
                return ResponseEntity.ok("Redis is UP");
            } else {
                return ResponseEntity.status(503).body("Redis cache not found");
            }
        } catch (Exception e) {
            log.error("Redis health check failed", e);
            return ResponseEntity.status(503).body("Redis is DOWN: " + e.getMessage());
        }
    }

    private record CacheInfoResponse(
        int totalCaches,
        java.util.List<String> cacheNames,
        String cacheManagerType
    ) {
    }
}
