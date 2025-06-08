package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.assembler.ChannelAssembler;
import com.sprint.mission.discodeit.dto.ChannelDto;
import com.sprint.mission.discodeit.dto.request.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Transactional
public class BasicChannelService implements ChannelService {

  private final ChannelRepository channelRepository;
  private final MessageRepository messageRepository;
  private final ReadStatusRepository readStatusRepository;
  private final UserRepository userRepository;

  private final ChannelAssembler channelAssember;

  @Override
  public ChannelDto create(PublicChannelCreateRequest publicChannelCreateRequest) {
    Channel channel = new Channel(ChannelType.PUBLIC,
        publicChannelCreateRequest.name(), publicChannelCreateRequest.description(),
        publicChannelCreateRequest.ownerId());
    Channel createdChannel = this.channelRepository.save(channel);
    return channelAssember.toDtoWithParticipants(createdChannel);
  }

  /* User 별 ReadStatus 생성 , name과 description 생략 */
  public ChannelDto create(PrivateChannelCreateRequest privateCreateRequest) {
    Channel channel = new Channel(ChannelType.PRIVATE, null, null, privateCreateRequest.ownerId());

    Channel createdChannel = this.channelRepository.save(channel);

    if (privateCreateRequest.ownerId() != null) {
      User user = this.userRepository.findById(privateCreateRequest.ownerId()).orElseThrow(
          () -> new NoSuchElementException(
              "User with id " + privateCreateRequest.ownerId() + " not found"));

      this.readStatusRepository.save(
          new ReadStatus(user, createdChannel, createdChannel.getCreatedAt()));
    }

    /* participantIds 에 있는 유저들 ReadStatus 생성 */
    List<User> participants = this.userRepository.findAllById(
        privateCreateRequest.participantIds());
    for (User user : participants) {
      this.readStatusRepository.save(
          new ReadStatus(user, createdChannel, createdChannel.getCreatedAt()));
    }
    return channelAssember.toDtoWithParticipants(createdChannel);
  }

  @Override
  public ChannelDto find(UUID channelId) {
    return this.channelRepository
        .findById(channelId)
        .map(channelAssember::toDtoWithParticipants)
        .orElseThrow(
            () -> new NoSuchElementException("Channel with id " + channelId + " not found"));
  }

  @Override
  public List<ChannelDto> findAllByUserId(UUID userId) {
    // get private channel by userId
    List<UUID> mySubscribedChannelIds = this.readStatusRepository.findAllByUserId(userId)
        .stream()
        .map(readStatus -> {
          return readStatus.getChannel().getId();
        })
        .toList();

    // get public + private channel
    List<ChannelDto> myChannels = this.channelRepository.findAll()
        .stream()
        .filter((channel -> channel.getType().equals(ChannelType.PUBLIC)
            || mySubscribedChannelIds.contains(channel.getId())))
        .map(channelAssember::toDtoWithParticipants)
        .toList();

    return myChannels;
  }

  @Override
  public ChannelDto update(UUID channelId, PublicChannelUpdateRequest updateRequest) {
    Channel channel = this.channelRepository.findById(channelId)
        .orElseThrow(
            () -> new NoSuchElementException("Channel with id " + channelId + " not found"));

    if (channel.getType().equals(ChannelType.PRIVATE)) {
      throw new IllegalArgumentException("private 채널은 수정이 불가능합니다.");
    }

    channel.update(updateRequest.newName(), updateRequest.newDescription());

    /* 업데이트 후 다시 DB 저장 */
    this.channelRepository.save(channel);

    return this.channelRepository.findById(channelId)
        .map(channelAssember::toDtoWithParticipants)
        .orElseThrow(
            () -> new NoSuchElementException("Channel with id " + channelId + " not found"));
  }

  @Override
  public void delete(UUID channelId) {
    Channel channel = this.channelRepository.findById(channelId).
        orElseThrow(
            () -> new NoSuchElementException("Channel with channelId " + channelId + " not found"));

    /* 해당 채널과 관련된 모든 Message 삭제 */
    this.messageRepository.deleteAllByChannelId(channel.getId());

    /* 해당 채널과 관련된 모든 ReadStatus 삭제 */
    this.readStatusRepository.deleteAllByChannelId(channel.getId());

    this.channelRepository.deleteById(channel.getId());
  }

  @Override
  public List<User> findParticipantsByChannelId(UUID channelId) {
    //채널있는지 확인
    Channel channel = this.channelRepository
        .findById(channelId)
        .orElseThrow(
            () -> new NoSuchElementException("Channel with id " + channelId + " not found"));

    return this.readStatusRepository.findAllByChannelId(channel.getId()).stream()
        .map((ReadStatus::getUser)).toList();
  }
}
