package com.sprint.mission.discodeit.exception;

import com.sprint.mission.discodeit.entity.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.nio.file.AccessDeniedException;
import java.util.NoSuchElementException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(NoSuchElementException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleNoSuchElementException(NoSuchElementException e) {
        return new ErrorResponse(
            HttpStatus.NOT_FOUND.value(),
            e.getMessage(),
            "RESOURCE_NOT_FOUND"
        );
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleIllegalArgumentException(IllegalArgumentException e) {
        return new ErrorResponse(
            HttpStatus.BAD_REQUEST.value(),
            e.getMessage(),
            "INVALID_INPUT"
        );
    }
    
    @ExceptionHandler(IllegalStateException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleIllegalStateException(IllegalStateException e) {
        return new ErrorResponse(
            HttpStatus.CONFLICT.value(),
            e.getMessage(),
            "INVALID_STATE"
        );
    }

    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ErrorResponse handleAccessDeniedException(AccessDeniedException e) {
        return new ErrorResponse(
            HttpStatus.FORBIDDEN.value(),
            "접근 권한이 없습니다.",
            "ACCESS_DENIED"
        );
    }

    // 사용자 정의 예외들을 위한 커스텀 예외 클래스
//    @ExceptionHandler(UserPrincipalNotFoundException.class)
//    @ResponseStatus(HttpStatus.NOT_FOUND)
//    public ErrorResponse handleUserNotFoundException(UserPrincipalNotFoundException e) {
//        return new ErrorResponse(
//            HttpStatus.NOT_FOUND.value(),
//            e.getMessage(),
//            "USER_NOT_FOUND"
//        );
//    }
//
//    @ExceptionHandler(ChannelNotFoundException.class)
//    @ResponseStatus(HttpStatus.NOT_FOUND)
//    public ErrorResponse handleChannelNotFoundException(ChannelNotFoundException e) {
//        return new ErrorResponse(
//            HttpStatus.NOT_FOUND.value(),
//            e.getMessage(),
//            "CHANNEL_NOT_FOUND"
//        );
//    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleAllUncaughtException(Exception e) {
        return new ErrorResponse(
            HttpStatus.INTERNAL_SERVER_ERROR.value(),
            "서버 내부 오류가 발생했습니다.",
            "INTERNAL_SERVER_ERROR"
        );
    }
}