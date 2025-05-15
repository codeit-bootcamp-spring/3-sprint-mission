package com.sprint.mission.discodeit.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.NoSuchElementException;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(NullPointerException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String nullPointerExceptionHandler(NullPointerException exception) {
        return "올바르지 않은 참조입니다.";
    }

    @ExceptionHandler(NoSuchElementException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String noSuchElementExceptionHandler(NoSuchElementException e) {
        return "존재하지 않는 값입니다.";
    }

    //default 처리
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String defaultExceptionHandler(Exception exception) {
        return "알수없는 오류가 발생하였습니다. 다시 시도해 주세요.";
    }
}