package com.post.parcels.exceptions;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.java.Log;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RestControllerAdvice
@Log
class ExceptionHandlerAdvice {
    static final Pattern ENUM_VALIDATION_PATTERN = Pattern.compile("from String \\\"(?<actual>[^\\]]*)\\\": not one of the values accepted for Enum class: \\[(?<expected>[^\\]]*)\\]");

    @ExceptionHandler({BusinessException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    String handleRepositoryExceptionHandler(BusinessException ex) {
        return ex.getMessage();
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Map<String, String> handleMethodArgumentNotValidException(
            MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return errors;
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public String handleHttpMessageNotReadableException(HttpMessageNotReadableException ex) {
        if (ex.getCause() != null && ex.getCause().getCause() != null && ex.getCause().getCause() instanceof DateTimeParseException) {
            return ex.getCause().getCause().getMessage();
        } else if (ex.getCause() != null && ex.getCause() instanceof InvalidFormatException) {
            Matcher matcher = ENUM_VALIDATION_PATTERN.matcher(ex.getCause().getMessage());
            if (matcher.find() && matcher.groupCount() == 2) {
                return String.format("Enum value '%s' should be: '%s'", matcher.group("actual"), matcher.group("expected"));
            }
        }
        return ex.getMessage();
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(ConstraintViolationException.class)
    public Map<String, String> handleConstraintViolationException(ConstraintViolationException e) {
        Set<ConstraintViolation<?>> constraintViolations = e.getConstraintViolations();
        Map<String, String> errors = new HashMap<>();
        constraintViolations.forEach(violation ->
        {
            String[] path = violation.getPropertyPath().toString().split("\\.");
            errors.put(path[path.length - 1], violation.getMessage());
        });

        return errors;
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public Map<String, String> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException e) {
        Map<String, String> errors = new HashMap<>();
        errors.put(e.getName(), e.getMessage());
        return errors;
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleAllUncaughtException(Exception e) {
        log.warning("Unknown error occurred: " + e);
        return "Internal server error!";
    }

}
