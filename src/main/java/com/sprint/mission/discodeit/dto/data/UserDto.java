package com.sprint.mission.discodeit.dto.data;

import java.time.Instant;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


/* online 가 엔티티에는 없고 dto는 있어서 값을 넣어주려면 불변 객체인 record는 사용 불가 */
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Setter
public class UserDto {

  private UUID id;
  private String username;
  private String email;
  private BinaryContentDto profile;
  private Boolean online;
  private Instant createdAt;
  private Instant updatedAt;


}
