package com.sprint.mission.discodeit.dto.request;

public record UserRequest(
    String username,
    String email,
    String password
) {

  public record Login(
      String username,
      String password
  ) {

  }

  public record Update(
      String newUsername,
      String newEmail,
      String newPassword
  ) {

  }
}
