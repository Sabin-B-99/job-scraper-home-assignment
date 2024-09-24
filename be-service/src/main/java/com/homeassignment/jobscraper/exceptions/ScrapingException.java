package com.homeassignment.jobscraper.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

import java.time.LocalDateTime;

public class ScrapingException extends  RuntimeException{

    private HttpStatus statusCode;
    private LocalDateTime timestamp;

    public ScrapingException(String message, HttpStatus statusCode, LocalDateTime timestamp) {
        super(message);
        this.statusCode = statusCode;
        this.timestamp = timestamp;
    }

    public HttpStatus getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(HttpStatus statusCode) {
        this.statusCode = statusCode;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}
