package com.psychology.product.repository.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;
import com.psychology.product.util.JsonViews;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

@JsonView(JsonViews.DiagnosticResult.class)
public record DiagnosticResultDTO(
        @JsonProperty("user_id")
        UUID userId,
        @JsonProperty("diagnostic_id")
        UUID diagnosticId,
        @JsonProperty("interpretation_points")
        @NotNull
        @Min(0)
        @Max(200)
        short interpretationPoints,
        @JsonProperty("scale_points")
        @NotNull
        @Min(0)
        @Max(200)
        short scalePoints) {
}
