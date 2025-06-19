package com.sprint.mission.discodeit.dto.mapper.mapstruct;

import com.sprint.mission.discodeit.dto.data.*;
import com.sprint.mission.discodeit.entity.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * MapStruct 매퍼들의 통합 Facade
 * 
 * 기존 EntityDtoMapper와 호환되는 인터페이스 제공
 * 점진적 마이그레이션을 위한 브릿지 역할
 * 
 * 향후 개별 매퍼를 직접 사용하도록 변경 예정
 */
@Component
@RequiredArgsConstructor
public class MapperFacade {

  private final UserMapper userMapper;
  private final MessageMapper messageMapper;
  private final ChannelMapper channelMapper;
  private final BinaryContentMapper binaryContentMapper;
  private final ReadStatusMapper readStatusMapper;
  private final UserStatusMapper userStatusMapper;

  // ====== User 매핑 ======
  public UserDto toDto(User user) {
    return userMapper.toDto(user);
  }

  public List<UserDto> toUserDtoList(List<User> users) {
    return userMapper.toDtoList(users);
  }

  // ====== Message 매핑 ======
  public MessageDto toDto(Message message) {
    return messageMapper.toDto(message);
  }

  public List<MessageDto> toMessageDtoList(List<Message> messages) {
    return messageMapper.toDtoList(messages);
  }

  // ====== Channel 매핑 ======
  public ChannelDto toDto(Channel channel) {
    return channelMapper.toDto(channel);
  }

  public List<ChannelDto> toChannelDtoList(List<Channel> channels) {
    return channelMapper.toDtoList(channels);
  }

  // ====== BinaryContent 매핑 ======
  public BinaryContentDto toDto(BinaryContent binaryContent) {
    return binaryContentMapper.toDto(binaryContent);
  }

  public List<BinaryContentDto> toBinaryContentDtoList(List<BinaryContent> binaryContents) {
    return binaryContentMapper.toDtoList(binaryContents);
  }

  // ====== ReadStatus 매핑 ======
  public ReadStatusDto toDto(ReadStatus readStatus) {
    return readStatusMapper.toDto(readStatus);
  }

  public List<ReadStatusDto> toReadStatusDtoList(List<ReadStatus> readStatuses) {
    return readStatusMapper.toDtoList(readStatuses);
  }

  // ====== UserStatus 매핑 ======
  public UserStatusDto toDto(UserStatus userStatus) {
    return userStatusMapper.toDto(userStatus);
  }

  public List<UserStatusDto> toUserStatusDtoList(List<UserStatus> userStatuses) {
    return userStatusMapper.toDtoList(userStatuses);
  }
}