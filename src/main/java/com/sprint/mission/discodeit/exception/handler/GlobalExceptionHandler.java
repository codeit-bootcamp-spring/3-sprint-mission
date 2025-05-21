package com.sprint.mission.discodeit.exception.handler;

import com.sprint.mission.discodeit.exception.LoginFailedException;
import com.sprint.mission.discodeit.exception.NotFriendsException;
import com.sprint.mission.discodeit.exception.PrivateChannelModificationException;
import com.sprint.mission.discodeit.exception.UserNotInChannelException;
import com.sprint.mission.discodeit.exception.alreadyexist.AlreadyExistsException;
import com.sprint.mission.discodeit.exception.duplicate.DuplicateException;
import com.sprint.mission.discodeit.exception.notfound.NotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(LoginFailedException.class)
    public ResponseEntity<String> handleLoginFailedException(Exception e) {
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED) // 401
                .body(e.getMessage());
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<String> handleNotFoundException(Exception e) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(e.getMessage());
    }

    @ExceptionHandler({DuplicateException.class, AlreadyExistsException.class})
    public ResponseEntity<String> handleConflictException(Exception e) {
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(e.getMessage());
    }

    @ExceptionHandler({NotFriendsException.class, PrivateChannelModificationException.class, UserNotInChannelException.class})
    public ResponseEntity<String> handleBadRequestException(Exception e) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(e.getMessage());
    }
}
