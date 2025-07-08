package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.data.ChannelDto;
import com.sprint.mission.discodeit.dto.request.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.exception.Channel.PrivateChannelUpdateException;
import com.sprint.mission.discodeit.exception.Common.ResourceNotFoundException;
import com.sprint.mission.discodeit.mapper.ChannelMapper;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BasicChannelService implements ChannelService {

  private final ChannelRepository channelRepository;
  private final MessageRepository messageRepository;
  private final ReadStatusRepository readStatusRepository;
  private final UserRepository userRepository;

  private final ChannelMapper channelMapper;

  @Transactional
  @Override
  public ChannelDto create(PublicChannelCreateRequest publicChannelCreateRequest) {
    Channel channel = new Channel(ChannelType.PUBLIC,
        publicChannelCreateRequest.name(), publicChannelCreateRequest.description(),
        publicChannelCreateRequest.ownerId());
    this.channelRepository.save(channel);

    return channelMapper.toDto(channel);
  }

  /* User 별 ReadStatus 생성 , name과 description 생략 */
  @Transactional
  public ChannelDto create(PrivateChannelCreateRequest privateCreateRequest) {
    Channel channel = new Channel(ChannelType.PRIVATE, null, null, privateCreateRequest.ownerId());
    this.channelRepository.save(channel);

    /* participantIds 에 있는 유저들 ReadStatus 생성 */
    List<ReadStatus> readStatuses = userRepository.findAllById(
            privateCreateRequest.participantIds()).stream()
        .map(user -> new ReadStatus(user, channel, channel.getCreatedAt()))
        .toList();
    this.readStatusRepository.saveAll(readStatuses);

    /*  owner 정보가 있다면 FIXME : 위 로직과 코드가 중복됨  */
    if (privateCreateRequest.ownerId() != null) {
      ReadStatus readStatus = this.userRepository.findById(privateCreateRequest.ownerId())
          .map(user -> new ReadStatus(user, channel, channel.getCreatedAt())).get();
      this.readStatusRepository.save(readStatus);
    }

    return channelMapper.toDto(channel);
  }

  @Override
  public ChannelDto find(UUID channelId) {

    return this.channelRepository
        .findById(channelId)
        .map(channelMapper::toDto)
        .orElseThrow(
            () -> new ResourceNotFoundException("channelId = " + channelId));
  }

  @Override
  public List<ChannelDto> findAllByUserId(UUID userId) {
    // get private channel by userId
    List<UUID> mySubscribedChannelIds = this.readStatusRepository.findAllByUserId(userId)
        .stream()
        .map(ReadStatus::getChannel)
        .map(Channel::getId)
        .toList();

    // get public + private channel
    return this.channelRepository.findAllByTypeOrIdIn(ChannelType.PUBLIC, mySubscribedChannelIds)
        .stream()
        .map(channelMapper::toDto)
        .toList();
  }

  @Transactional
  @Override
  public ChannelDto update(UUID channelId, PublicChannelUpdateRequest updateRequest) {
    Channel channel = this.channelRepository.findById(channelId)
        .orElseThrow(
            () -> new ResourceNotFoundException("channelId = " + channelId));

    if (channel.getType().equals(ChannelType.PRIVATE)) {
      throw new PrivateChannelUpdateException(channel);
    }
    channel.update(updateRequest.newName(), updateRequest.newDescription());

    return channelMapper.toDto(channel);
  }

  @Transactional
  @Override
  public void delete(UUID channelId) {
    Channel channel = this.channelRepository.findById(channelId).
        orElseThrow(
            () -> new ResourceNotFoundException("channelId = " + channelId));

    /* 해당 채널과 관련된 모든 Message 삭제 */
    this.messageRepository.deleteAllByChannelId(channel.getId());
    /* 해당 채널과 관련된 모든 ReadStatus 삭제 */
    this.readStatusRepository.deleteAllByChannelId(channel.getId());
    this.channelRepository.deleteById(channel.getId());
  }

}
