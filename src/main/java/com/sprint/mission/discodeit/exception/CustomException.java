package com.sprint.mission.discodeit.exception;

public class CustomException extends RuntimeException {

  public CustomException(String message) {
    super(message);
  }

  public CustomException(String message, Throwable cause) {
    super(message, cause);
  }

  // 사용자 관련 예외들
  public static class UserNotFoundException extends CustomException {
    public UserNotFoundException(String message) {
      super(message);
    }
  }

  public static class DuplicateUserException extends CustomException {
    public DuplicateUserException(String message) {
      super(message);
    }
  }

  public static class InvalidPasswordException extends CustomException {
    public InvalidPasswordException(String message) {
      super(message);
    }
  }

  // 사용자 상태 관련 예외들
  public static class InvalidUserStatusException extends CustomException {
    public InvalidUserStatusException(String message) {
      super(message);
    }
  }

  public static class DuplicateUserStatusException extends CustomException {
    public DuplicateUserStatusException(String message) {
      super(message);
    }
  }

  // 채널 관련 예외들
  public static class ChannelNotFoundException extends CustomException {
    public ChannelNotFoundException(String message) {
      super(message);
    }
  }

  public static class PrivateChannelUpdateException extends CustomException {
    public PrivateChannelUpdateException(String message) {
      super(message);
    }
  }

  // 메시지 관련 예외들
  public static class MessageNotFoundException extends CustomException {
    public MessageNotFoundException(String message) {
      super(message);
    }
  }

  // 읽기 상태 관련 예외들
  public static class ReadStatusNotFoundException extends CustomException {
    public ReadStatusNotFoundException(String message) {
      super(message);
    }
  }

  public static class DuplicateReadStatusException extends CustomException {
    public DuplicateReadStatusException(String message) {
      super(message);
    }
  }

  // 바이너리 콘텐츠 관련 예외들
  public static class BinaryContentNotFoundException extends CustomException {
    public BinaryContentNotFoundException(String message) {
      super(message);
    }
  }
}