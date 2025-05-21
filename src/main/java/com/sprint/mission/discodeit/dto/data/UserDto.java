package com.sprint.mission.discodeit.dto.data;

import com.sprint.mission.discodeit.entity.User;
import lombok.Getter;

import java.util.UUID;

@Getter
public class UserDto {
  private final UUID id;
  private final String username;
  private final String email;
  private final boolean hasProfileImage;
  private final boolean isOnline;
  private final UUID profileId;

  public UserDto(User user, boolean hasProfileImage, boolean isOnline) {
    this.id = user.getId();
    this.username = user.getUsername();
    this.email = user.getEmail();
    this.hasProfileImage = hasProfileImage;
    this.isOnline = isOnline;
    this.profileId = user.getProfileId();
  }

  @Override
  public String toString() {
    return "[" + "username='" + username + "', email='" + email +
        "', 프로필이미지: " + hasProfileImage +
        ", 온라인: " + isOnline + "]";
  }
}


