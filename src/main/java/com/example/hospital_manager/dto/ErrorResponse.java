package com.example.hospital_manager.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;

public record ErrorResponse(
    int status,
    String error,
    String message,
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    LocalDateTime timestamp,
    String path
) {
    public ErrorResponse(int status, String error, String message, String path) {
        this(status, error, message, LocalDateTime.now(), path);
    }
}
