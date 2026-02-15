package ru.sinvic.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record StartPlaybackRequest(
    @NotNull @Positive Long contentId
) {
}
