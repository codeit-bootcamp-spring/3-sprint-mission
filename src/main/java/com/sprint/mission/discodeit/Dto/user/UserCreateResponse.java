package com.sprint.mission.discodeit.Dto.user;

import com.sprint.mission.discodeit.Dto.binaryContent.RedirectAttributesResponse;
import java.time.Instant;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

/**
 * packageName    : com.sprint.mission.discodeit.Dto.user fileName       : UserCreateResponse author
 *         : doungukkim date           : 2025. 4. 29. description    :
 * =========================================================== DATE              AUTHOR
 * NOTE ----------------------------------------------------------- 2025. 4. 29.        doungukkim
 * 최초 생성
 */
@Getter
public class UserCreateResponse {

  private final UUID id;
  private final String username;
  private final String email;
  private final UUID profileId;
  private final UUID userStatusId;

  public UserCreateResponse(UUID id, String username, String email, UUID profileId,
      UUID userStatusId) {
    this.id = id;
    this.username = username;
    this.email = email;
    this.profileId = profileId;
    this.userStatusId = userStatusId;
  }

}
