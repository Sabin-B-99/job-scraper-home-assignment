package com.homeassignment.jobscraper.dtos;


import java.time.LocalDateTime;
import java.util.List;

public record ValidationError(String message, List<String> details, LocalDateTime timestamp) {
}
