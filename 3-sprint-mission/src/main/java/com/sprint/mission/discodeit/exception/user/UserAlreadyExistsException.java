package com.sprint.mission.discodeit.exception.user;

import com.sprint.mission.discodeit.exception.ErrorCode;
import java.util.Map;

public class UserAlreadyExistsException extends UserException {

  private String username;
  private String email;

  private UserAlreadyExistsException(String username, String email,
      Map<String, Object> details) {
    super(ErrorCode.DUPLICATE_USER, details);
    this.username = username;
    this.email = email;
  }

  public static UserAlreadyExistsException fromUsername(String username) {
    return new UserAlreadyExistsException(username, null, Map.of("username", username));
  }

  public static UserAlreadyExistsException fromEmail(String email) {
    return new UserAlreadyExistsException(null, email, Map.of("email", email));
  }
}
