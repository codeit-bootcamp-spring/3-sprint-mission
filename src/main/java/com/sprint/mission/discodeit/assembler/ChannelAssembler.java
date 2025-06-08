package com.sprint.mission.discodeit.assembler;

import com.sprint.mission.discodeit.dto.ChannelDto;
import com.sprint.mission.discodeit.dto.UserDto;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.mapper.ChannelMapper;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ChannelAssembler {

  private final ChannelMapper channelMapper;
  private final UserMapper userMapper;
  private final ReadStatusRepository readStatusRepository;

  /* List<UserDto> participants는 엔티티에 없으므로 따로 계산해서 넣어줘야함.*/
  public ChannelDto toDtoWithParticipants(Channel channel) {
    ChannelDto channelDto = channelMapper.toDto(channel);
    List<UserDto> participantDtoList = this.readStatusRepository.findAllByChannelId(channel.getId())
        .stream()
        .map((ReadStatus::getUser)).map(userMapper::toDto).toList();
    channelDto.setParticipants(participantDtoList);

    return channelDto;
  }
}
