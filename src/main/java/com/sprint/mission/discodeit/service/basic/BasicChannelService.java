package com.sprint.mission.discodeit.service.basic;

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
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;

@RequiredArgsConstructor
@Service
public class BasicChannelService implements ChannelService {

  private final ChannelRepository channelRepository;
  private final MessageRepository messageRepository;
  private final ReadStatusRepository readStatusRepository;

  @Override
  public Channel create(PrivateChannelCreateRequest privateChannelCreateRequest) {
    Channel channel = new Channel(ChannelType.PRIVATE, null, null);
    Channel createdChannel = channelRepository.save(channel);

    privateChannelCreateRequest.participantIds().stream()
        .map(userId -> new ReadStatus(userId, createdChannel.getId(), Instant.MIN))
        .forEach(readStatusRepository::save);

    return createdChannel;
  }

  @Override
  public Channel create(PublicChannelCreateRequest publicChannelCreateRequest) {
    String name = publicChannelCreateRequest.name();
    String description = publicChannelCreateRequest.description();

    if (isExistName(name)) {
      System.out.println("생성 실패 : 이미 존재하는 채널명입니다.");
      return null;
    } else {
      Channel channel = new Channel(ChannelType.PUBLIC, name, description);
      return channelRepository.save(channel);
    }
  }

  @Override
  public ChannelDto find(UUID channelId) {
    Channel channel = channelRepository.findById(channelId);
    if (channel == null) {
      throw new NoSuchElementException("조회 실패 : 존재하지 않는 채널Id입니다. [" + channelId + "]");
    }
    return toDto(channel);
  }


  @Override
  public List<ChannelDto> findAllByUserId(UUID userId) {
    List<UUID> mySubscribedChannels = readStatusRepository.findAllByUserId(userId).stream()
        .map(ReadStatus::getChannelId).toList();

    return channelRepository.findAll().stream()
        .filter(channel ->
            channel.getType().equals(ChannelType.PUBLIC)
                || mySubscribedChannels.contains(channel.getId())
        ).map(this::toDto).toList();
  }

  @Override
  public boolean isExistName(String name) {
    return channelRepository.isExistName(name);
  }

  @Override
  public Channel update(UUID channelId, PublicChannelUpdateRequest publicChannelUpdateRequest) {
    Channel channel = channelRepository.findById(channelId);
    String name = publicChannelUpdateRequest.newName();
    String description = publicChannelUpdateRequest.newDescription();

    if (channel == null) {
      System.out.println("수정 실패 : 존재하지 않는 채널입니다.");
    } else if (channel.getType() != ChannelType.PUBLIC) {
      throw new IllegalArgumentException("수정 실패 : Private 채널은 수정할 수 없습니다.");
    } else if (isExistName(name)) {
      System.out.println("수정 실패 : 이미 존재하는 채널명입니다.");
    } else {
      channel.update(name, description);
      channelRepository.save(channel);
    }
    return channel;
  }

  @Override
  public void delete(UUID channelId) {
    Channel channel = channelRepository.findById(channelId);
    if (channel == null) {
      throw new NoSuchElementException("수정 실패 : 존재하지 않는 채널입니다.");
    }

    readStatusRepository.deleteAllByChannelId(channelId);
    messageRepository.deleteByChannelId(channelId);
    channelRepository.delete(channelId);
  }


  private ChannelDto toDto(Channel channel) {
    Instant lastMessageAt = messageRepository.findByChannel(channel.getId())
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
        participantIds
    );
  }
}
