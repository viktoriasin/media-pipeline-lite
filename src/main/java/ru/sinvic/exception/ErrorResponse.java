package ru.sinvic.exception;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@NoArgsConstructor
@Getter
@Setter
public class ErrorResponse {
    private String code;
    private String message;
    private Object errorData;
    private LocalDateTime timestamp;
    private String path;

    private ErrorResponse(String code, String message, Object errorData, LocalDateTime timestamp, String path) {
        this.code = code;
        this.message = message;
        this.errorData = errorData;
        this.timestamp = timestamp;
        this.path = path;
    }

    public static class Builder {
        private String code;
        private String message;
        private Object errorData;
        private LocalDateTime timestamp;
        private String path;

        public Builder code(String code) {
            this.code = code;
            return this;
        }

        public Builder message(String message) {
            this.message = message;
            return this;
        }

        public Builder errorData(Object errorData) {
            this.errorData = errorData;
            return this;
        }

        public Builder timestamp(LocalDateTime timestamp) {
            this.timestamp = timestamp;
            return this;
        }

        public Builder path(String path) {
            this.path = path;
            return this;
        }

        public ErrorResponse build() {
            if (code == null) throw new IllegalArgumentException("code cannot be null");
            if (message == null) throw new IllegalArgumentException("message cannot be null");

            LocalDateTime finalTimestamp = (timestamp != null) ? timestamp : LocalDateTime.now();

            return new ErrorResponse(code, message, errorData, timestamp, path);
        }
    }

    public static Builder builder() {
        return new Builder();
    }
}
