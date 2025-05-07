package com.sprint.mission.discodeit.entity.exception;

public class DuplicateUserStatusException extends RuntimeException {
    public DuplicateUserStatusException(String message) {
        super(message);
    }
}
