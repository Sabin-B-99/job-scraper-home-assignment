package com.homeassignment.jobscraper.exceptions;

import com.homeassignment.jobscraper.dtos.Error;
import com.homeassignment.jobscraper.dtos.ValidationError;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.LocalDateTime;
import java.util.List;

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
        Error error = new Error("Potential elements changes in job website. Please report" + e.getMessage(), LocalDateTime.now());
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

    @ExceptionHandler(value = MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Error> handleTypeMismatchExceptions(Exception e){
        Error error = new Error("Invalid argument type. Please check your request parameters.", LocalDateTime.now());
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(error);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ValidationError> handleMethodArgumentValidationException(MethodArgumentNotValidException e){
        List<String> errors = e.getBindingResult()
                .getAllErrors()
                .stream()
                .map(ObjectError::getDefaultMessage)
                .toList();

        ValidationError error = new ValidationError("Validation failed :(", errors, LocalDateTime.now());
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(error);
    }

    @ExceptionHandler(value = Exception.class)
    public ResponseEntity<Error> handleGenericException(Exception e){
        Error error = new Error(e.getMessage(), LocalDateTime.now());
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(error);
    }
}
