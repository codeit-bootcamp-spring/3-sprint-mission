package com.sprint.mission.discodeit.exception;

import ch.qos.logback.core.net.SocketConnector.ExceptionHandler;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.ErrorResponse;

@ExceptionHandler(DiscodeitException.class)
public ResponseEntity<ErrorResponse> handleDiscodeitException(DiscodeitException e) {
    ErrorCode errorCode = e.getErrorCode();
    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .body(new ErrorResponse(errorCode.getCode(), errorCode.getMessage()));
}