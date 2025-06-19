package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.assembler.ChannelAssembler;
import com.sprint.mission.discodeit.dto.response.ChannelResponse;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.channel.CannotUpdatePrivateChannelException;
import com.sprint.mission.discodeit.exception.channel.ChannelNotFoundException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageAttachmentRepository;
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
@Transactional
public class BasicChannelService implements ChannelService {

  private final UserRepository userRepository;
  private final ChannelRepository channelRepository;
  private final ReadStatusRepository readStatusRepository;
  private final MessageRepository messageRepository;
  private final MessageAttachmentRepository messageAttachmentRepository;
  private final ChannelAssembler channelAssembler;

  @Override
  public ChannelResponse create(String name, String description) {
    Channel channel = Channel.createPublic(name, description);
    Channel savedChannel = channelRepository.save(channel);
    return channelAssembler.toResponse(savedChannel);
  }

  @Override
  public ChannelResponse create(List<UUID> participantIds) {
    Channel channel = Channel.createPrivate();
    Channel savedChannel = channelRepository.save(channel);

    for (UUID participantId : participantIds) {
      User participant = userRepository.findById(participantId)
          .orElseThrow(() -> new UserNotFoundException(participantId.toString()));
      ReadStatus status = ReadStatus.create(participant, savedChannel);
      readStatusRepository.save(status);
    }

    return channelAssembler.toResponse(savedChannel);
  }

  @Override
  public ChannelResponse findById(UUID channelId) {
    Channel channel = channelRepository.findById(channelId)
        .orElseThrow(() -> new ChannelNotFoundException(channelId.toString()));
    return channelAssembler.toResponse(channel);
  }

  @Override
  public List<ChannelResponse> findAllByUserId(UUID userId) {
    return channelRepository.findAllByUserId(userId).stream()
        .map(channelAssembler::toResponse)
        .toList();
  }

  @Override
  public ChannelResponse update(UUID channelId, String newName, String newDescription) {
    Channel channel = channelRepository.findById(channelId)
        .orElseThrow(() -> new ChannelNotFoundException(channelId.toString()));

    if (channel.getType() == ChannelType.PRIVATE) {
      throw new CannotUpdatePrivateChannelException(channelId.toString());
    }

    channel.updateName(newName);
    channel.updateDescription(newDescription);

    Channel updated = channelRepository.save(channel);
    return channelAssembler.toResponse(updated);
  }

  @Override
  public ChannelResponse delete(UUID channelId) {
    Channel channel = channelRepository.findById(channelId)
        .orElseThrow(() -> new ChannelNotFoundException(channelId.toString()));

    // 해당 채널의 메시지 ID 목록 조회
    List<UUID> messageIds = messageRepository.findMessageIdsByChannelId(channelId);

    // 첨부파일 먼저 삭제
    messageAttachmentRepository.deleteByMessageIds(messageIds);

    // 메시지 삭제
    messageRepository.deleteByChannelId(channelId);

    // 읽음 상태 삭제
    readStatusRepository.deleteByChannelId(channelId);

    // 채널 삭제
    channelRepository.deleteById(channelId);

    return channelAssembler.toResponse(channel);
  }
}
