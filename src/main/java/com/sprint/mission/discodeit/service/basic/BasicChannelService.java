package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.channel.ChannelResponseDto;
import com.sprint.mission.discodeit.dto.channel.PrivateChannelDto;
import com.sprint.mission.discodeit.dto.channel.PublicChannelDto;
import com.sprint.mission.discodeit.dto.channel.PublicChannelUpdateDto;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.PrivateChannelModificationException;
import com.sprint.mission.discodeit.exception.notfound.NotFoundChannelException;
import com.sprint.mission.discodeit.exception.notfound.NotFoundMessageException;
import com.sprint.mission.discodeit.exception.notfound.NotFoundUserException;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("basicChannelService")
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BasicChannelService implements ChannelService {

  private final ChannelRepository channelRepository;
  private final UserRepository userRepository;
  private final MessageRepository messageRepository;
  private final ReadStatusRepository readStatusRepository;

  @Override
  @Transactional
  public Channel createPublicChannel(PublicChannelDto publicChannelDTO) {

    Channel channel = PublicChannelDto.toEntity(publicChannelDTO);

    channelRepository.save(channel);

    return channel;
  }

  @Override
  @Transactional
  public Channel createPrivateChannel(PrivateChannelDto privateChannelDTO) {
    Channel channel = PrivateChannelDto.toEntity(privateChannelDTO);

    Channel createdChannel = channelRepository.save(channel);

    // 읽음 상태 추가
    privateChannelDTO.participantIds().stream()
        .map(userId -> new ReadStatus(findUser(userId), createdChannel, channel.getCreatedAt()))
        .forEach(readStatusRepository::save);

    return createdChannel;
  }

  @Override
  public ChannelResponseDto findById(UUID channelId) {
    Channel channel = findChannel(channelId);

    Instant lastMessageTime = getLastMessageTime(channel);
    ChannelResponseDto channelResponseDTO = Channel.toDTO(channel);

    return channelResponseDTO;
  }

  @Override
  public List<ChannelResponseDto> findAllByUserId(UUID userId) {
    List<UUID> participatedChannelIds = readStatusRepository.findAllByUserId(userId).stream()
        .map(readStatus -> readStatus.getChannel().getId())
        .toList();

    return channelRepository.findAll().stream()
        .filter(channel ->
            channel.getType().equals(ChannelType.PUBLIC)
                || participatedChannelIds.contains(channel.getId()))
        .map(Channel::toDTO)
        .toList();
  }

  @Override
  @Transactional
  public ChannelResponseDto update(UUID channelId, PublicChannelUpdateDto publicChannelUpdateDTO) {
    Channel channel = findChannel(channelId);

    // PRIVATE 채널은 수정 불가
    if (channel.getType().equals(ChannelType.PRIVATE)) {
      throw new PrivateChannelModificationException();
    }

    // 변경 사항 적용
    channel.updateName(publicChannelUpdateDTO.newName());
    channel.updateDescription(publicChannelUpdateDTO.newDescription());

    channelRepository.save(channel);

    return Channel.toDTO(channel);
  }

  @Override
  @Transactional
  public void deleteById(UUID channelId) {
    Channel channel = findChannel(channelId);

    channelRepository.deleteById(channelId);
  }


  private Channel findChannel(UUID id) {
    return channelRepository.findById(id)
        .orElseThrow(NotFoundChannelException::new);
  }

  private User findUser(UUID id) {
    return userRepository.findById(id)
        .orElseThrow(NotFoundUserException::new);
  }

  private Instant getLastMessageTime(Channel channel) {
    if (!channel.getMessages().isEmpty()) {
      int messageCount = channel.getMessages().size();
      Message lastMessage = channel.getMessages().get(messageCount - 1);

      return messageRepository.findById(lastMessage.getId())
          .orElseThrow(NotFoundMessageException::new)
          .getCreatedAt();
    } else {
      return null;
    }
  }
}
