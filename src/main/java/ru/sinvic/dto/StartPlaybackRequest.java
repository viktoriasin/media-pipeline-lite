package ru.sinvic.dto;

import jakarta.validation.constraints.NotNull;

// TODO: нужен ли тут Long
public record StartPlaybackRequest(
    @NotNull Long contentId
) {
}
