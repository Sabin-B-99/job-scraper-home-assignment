package com.homeassignment.jobscraper.dtos;


import java.time.LocalDateTime;

public record Error(String message, LocalDateTime timestamp) {
}
