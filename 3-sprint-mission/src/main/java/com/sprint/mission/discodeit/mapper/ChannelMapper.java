package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.data.ChannelDTO;
import com.sprint.mission.discodeit.dto.data.UserDTO;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(componentModel = "spring", uses = {UserMapper.class})
public abstract class ChannelMapper {

  @Autowired
  private MessageRepository messageRepository;
  @Autowired
  private ReadStatusRepository readStatusRepository;
  @Autowired
  private UserMapper userMapper;

  @Mapping(target = "participants", expression = "java(resolveParticipants(channel))")
  @Mapping(target = "lastMessageAt", expression = "java(resolveLastMessageAt(channel))")
  abstract public ChannelDTO toDTO(Channel channel);

  protected Instant resolveLastMessageAt(Channel channel) {
    return messageRepository.findLastMessageAtByChannelId(
            channel.getId())
        .orElse(Instant.MIN);
  }

  protected List<UserDTO> resolveParticipants(Channel channel) {
    List<UserDTO> participants = new ArrayList<>();

    if (channel.getType().equals(ChannelType.PRIVATE)) {
      readStatusRepository.findAllByChannelId(channel.getId())
          .stream()
          .map(readStatus -> readStatus.getUser())
          .map(userMapper::toDTO)
          .forEach(participants::add);
    }

    return participants;
  }
}
