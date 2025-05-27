package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.common.ErrorMessages;
import com.sprint.mission.discodeit.dto.data.ChannelDto;
import com.sprint.mission.discodeit.dto.request.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class BasicChannelService implements ChannelService {

  private final ChannelRepository channelRepository;

  private final ReadStatusRepository readStatusRepository;
  private final MessageRepository messageRepository;

  //private 채널생성
  @Override
  public Channel create(PrivateChannelCreateRequest request) {
    Channel channel = new Channel(ChannelType.PRIVATE);
    Channel createdChannel = channelRepository.save(channel);

    request.participantIds().stream()
        .map(userId -> new ReadStatus(userId, createdChannel.getId(), channel.getCreatedAt()))
        .forEach(readStatusRepository::save);

    return createdChannel;
  }

  //public 채널생성
  @Override
  public Channel create(PublicChannelCreateRequest request) {
    String name = request.name();
    String description = request.description();
    Channel channel = new Channel(ChannelType.PUBLIC, name, description);

    return channelRepository.save(channel);
  }

  @Override
  public ChannelDto find(UUID channelId) {
    return channelRepository.find(channelId)
        .map(this::toDto)
        .orElseThrow(() -> new NoSuchElementException(
            ErrorMessages.format("Channel", ErrorMessages.ERROR_NOT_FOUND)));
  }

  /*
   * 1. 저장소에서 전체 채널 불러오기
   * 2. 응답리스트 생성
   * 3. 채널 순회 하며 필터링(Private, Public)
   * 4. 반환*/
  @Override
  public List<ChannelDto> findAllByUserId(UUID userId) {
    List<UUID> mySubscribedChannelIds = readStatusRepository.findAllByUserId(userId).stream()
        .map(ReadStatus::getChannelId)
        .toList();

    return channelRepository.findAll().stream()
        .filter(channel ->
            channel.getType().equals(ChannelType.PUBLIC)
                || mySubscribedChannelIds.contains(channel.getId())
        )
        .map(this::toDto)
        .toList();
  }

  @Override
  public Channel update(UUID channelId, PublicChannelUpdateRequest request) {
    String newName = request.newName();
    String newDescription = request.newDescription();
    Channel channel = channelRepository.find(channelId)
        .orElseThrow(() -> new IllegalArgumentException(
            ErrorMessages.format("Channel", ErrorMessages.ERROR_NOT_FOUND)));

    //채널 타입이 private이면 수정 불가
    if (channel.getType().equals(ChannelType.PRIVATE)) {
      throw new IllegalArgumentException(
          ErrorMessages.format("Channel", ErrorMessages.ERROR_PRIVATE_CHANNEL_NOT_UPDATE));
    }
    channel.update(newName, newDescription);
    return channelRepository.save(channel);
  }

  @Override
  public void delete(UUID channelId) {
    Channel channel = channelRepository.find(channelId)
        .orElseThrow(() -> new NoSuchElementException(
            ErrorMessages.format("Channel", ErrorMessages.ERROR_NOT_FOUND)));

    messageRepository.deleteAllByChannelId(channel.getId());
    readStatusRepository.deleteAllByChannelId(channel.getId());

    channelRepository.deleteById(channelId);
  }

  private ChannelDto toDto(Channel channel) {
    Instant lastMessageAt = messageRepository.findAllByChannelId(channel.getId())
        .stream()
        .sorted(Comparator.comparing(Message::getCreatedAt).reversed())
        .map(Message::getCreatedAt)
        .limit(1)
        .findFirst()
        .orElse(Instant.MIN);

    List<UUID> participantIds = new ArrayList<>();
    if (channel.getType().equals(ChannelType.PRIVATE)) {
      readStatusRepository.findAllByChannelId(channel.getId())
          .stream()
          .map(ReadStatus::getUserId)
          .forEach(participantIds::add);
    }

    return new ChannelDto(
        channel.getId(),
        channel.getType(),
        channel.getName(),
        channel.getDescription(),
        participantIds,
        lastMessageAt
    );
  }
}
