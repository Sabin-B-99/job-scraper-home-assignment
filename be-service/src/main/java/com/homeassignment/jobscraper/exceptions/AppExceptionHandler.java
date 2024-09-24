package com.homeassignment.jobscraper.exceptions;

import com.homeassignment.jobscraper.dtos.Error;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;

@ControllerAdvice
public class AppExceptionHandler {


    @ExceptionHandler(value = ScrapingException.class)
    public ResponseEntity<Error> handleScrapingExceptions(ScrapingException e){
        Error error = new Error(e.getMessage(), LocalDateTime.now());
        return ResponseEntity
                .status(e.getStatusCode().value())
                .body(error);
    }

    @ExceptionHandler(value = NullPointerException.class)
    public ResponseEntity<Error> handleNullPointerExceptions(NullPointerException e){
        Error error = new Error("[Potential elements changes in job website]" + e.getMessage(), LocalDateTime.now());
        return ResponseEntity
                .status(HttpStatus.BAD_GATEWAY)
                .body(error);
    }

    @ExceptionHandler(value = JobNotFoundException.class)
    public ResponseEntity<Error> handleJobNotFoundException(JobNotFoundException e){
        Error error = new Error(e.getMessage(), LocalDateTime.now());
        return ResponseEntity
                .status(e.getStatusCode().value())
                .body(error);
    }

    @ExceptionHandler(value = Exception.class)
    public ResponseEntity<Error> handleGenericException(Exception e){
        Error error = new Error(e.getMessage(), LocalDateTime.now());
        return ResponseEntity
                .status(HttpStatus.BAD_GATEWAY)
                .body(error);
    }
}
