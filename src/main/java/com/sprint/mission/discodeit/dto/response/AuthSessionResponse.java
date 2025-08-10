package com.sprint.mission.discodeit.dto.response;

public record AuthSessionResponse(boolean authenticated, UserResponse user) {

  public static AuthSessionResponse guest() {
    return new AuthSessionResponse(false, null);
  }

  public static AuthSessionResponse of(UserResponse user) {
    return new AuthSessionResponse(true, user);
  }
}
