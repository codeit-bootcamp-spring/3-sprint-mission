package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.data.ChannelDto;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(componentModel = "spring", uses = {UserMapper.class, ReadStatusMapper.class,
    MessageMapper.class})
public abstract class ChannelMapper {

  //Q. mapper가 이렇게 주입받아서 많은 책임을 가져도 되나?
  @Autowired
  private MessageRepository messageRepository;
  @Autowired
  private ReadStatusRepository readStatusRepository;
  @Autowired
  private UserMapper userMapper;

  @Mapping(target = "participants", qualifiedByName = "getParticipants")
  @Mapping(target = "lastMessageAt", qualifiedByName = "getLastMessageAt")
  public abstract ChannelDto toDto(Channel channel);

  public abstract Channel toEntity(ChannelDto channelDto);


  @Named("getParticipants")
  public List<UserDto> getParticipants(Channel channel) {
    List<UserDto> participants = new ArrayList<>();
    this.readStatusRepository.findAllByChannelIdWithUser(channel.getId())
        .stream()
        .map(ReadStatus::getUser)
        .map(userMapper::toDto).forEach(participants::add);

    return participants;
  }

  @Named("getLastMessageAt")
  public Instant getLastMessageAt(Channel channel) {
    return this.messageRepository.findLastMessageAtByChannelId(channel.getId())
        .orElse(Instant.MIN);
  }
}
