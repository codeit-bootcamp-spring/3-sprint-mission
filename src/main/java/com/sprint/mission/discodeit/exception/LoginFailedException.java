package com.sprint.mission.discodeit.exception;

public class LoginFailedException extends RuntimeException {

  public LoginFailedException() {
      super("name이나 password가 일치하지 않습니다.");
  }

  public LoginFailedException(String message) {
      super(message);
  }
}
