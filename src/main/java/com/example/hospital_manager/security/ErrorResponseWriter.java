package com.example.hospital_manager.security;

import com.example.hospital_manager.dto.ErrorResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;

@Component
public class ErrorResponseWriter {

    private final ObjectMapper objectMapper;

    public ErrorResponseWriter() {
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
        this.objectMapper.findAndRegisterModules();
    }

    public void writeErrorResponse(HttpServletResponse response, int status, String error, String message, String path) throws IOException {
        response.setStatus(status);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");

        ErrorResponse errorResponse = new ErrorResponse(
                status,
                error,
                message,
                LocalDateTime.now(),
                path
        );

        try {
            response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
        } catch (Exception ex) {
            String safeJson = String.format(
                    "{\"status\":%d,\"error\":\"%s\",\"message\":\"%s\",\"timestamp\":\"%s\",\"path\":\"%s\"}",
                    status,
                    escape(error),
                    escape(message),
                    LocalDateTime.now().toString(),
                    escape(path)
            );
            response.getWriter().write(safeJson);
        }
    }

    private String escape(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", " ");
    }
}
