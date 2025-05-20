package com.sprint.mission.discodeit.global.exception;

public class DuplicateUserException extends RuntimeException {
  public DuplicateUserException(String message) {
    super(message);
  }
}