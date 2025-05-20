package com.sprint.mission.discodeit.controller;

import java.util.NoSuchElementException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<ErrorResponse> handleIllegalArgument(IllegalArgumentException e) {
    e.printStackTrace();
    return buildErrorResponse(e.getMessage(), HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(NoSuchElementException.class)
  public ResponseEntity<ErrorResponse> handleNotFound(NoSuchElementException e) {
    e.printStackTrace();
    return buildErrorResponse(e.getMessage(), HttpStatus.NOT_FOUND);
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorResponse> handleOther(Exception e) {
    e.printStackTrace();
    return buildErrorResponse("Internal Server Error", HttpStatus.INTERNAL_SERVER_ERROR);
  }

  private ResponseEntity<ErrorResponse> buildErrorResponse(String message, HttpStatus status) {
    
    return ResponseEntity.status(status).body(new ErrorResponse(message, status.value()));
  }

  public record ErrorResponse(String message, int status) {

  }
}
