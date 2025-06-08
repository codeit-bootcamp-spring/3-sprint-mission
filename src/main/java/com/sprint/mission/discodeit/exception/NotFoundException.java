package com.sprint.mission.discodeit.exception;

public class NotFoundException extends RuntimeException {
    public NotFoundException(String type, String id) {
        super(type + " not found: " + id);
    }
}