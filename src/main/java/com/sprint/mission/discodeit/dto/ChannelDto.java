package com.sprint.mission.discodeit.dto;

import com.sprint.mission.discodeit.entity.ChannelType;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/* 가장 최근 메세지 시간 정보. PRIVATE 채널이면, DTO에 참여한 User의 id 를 포함할것 */
/* List<UserDto> participants 가 엔티티에는 없고 dto는 있어서 값을 넣어주려면 불변 객체인 record는 사용 불가 */
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Setter
public class ChannelDto {

  private UUID id;

  private ChannelType type;

  private String name;

  private String description;

  private List<UserDto> participants;

  private Instant lastMessageAt;

}