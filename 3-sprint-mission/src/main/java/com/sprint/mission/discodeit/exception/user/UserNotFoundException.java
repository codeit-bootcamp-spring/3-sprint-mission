package com.sprint.mission.discodeit.exception.user;

import com.sprint.mission.discodeit.exception.ErrorCode;
import java.util.Map;
import java.util.UUID;

public class UserNotFoundException extends UserException {

  private UUID userId;
  private String username;
  private String email;

  private UserNotFoundException(UUID userId, String username, String email,
      Map<String, Object> details) {
    super(ErrorCode.USER_NOT_FOUND, details);
    this.userId = userId;
    this.username = username;
    this.email = email;
  }

  public static UserNotFoundException fromUserId(UUID userId) {
    return new UserNotFoundException(userId, null, null, Map.of("userId", userId));
  }

  public static UserNotFoundException fromUsername(String username) {
    return new UserNotFoundException(null, username, null, Map.of("username", username));
  }

  public static UserNotFoundException fromEmail(String email) {
    return new UserNotFoundException(null, null, email, Map.of("email", email));
  }
}
