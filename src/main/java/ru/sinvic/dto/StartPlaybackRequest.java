package ru.sinvic.dto;

import jakarta.validation.constraints.NotNull;

public record StartPlaybackRequest(
    @NotNull Long contentId
) {
}
