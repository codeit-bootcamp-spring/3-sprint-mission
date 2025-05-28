package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.channel.ChannelResponseDto;
import com.sprint.mission.discodeit.dto.channel.PrivateChannelDto;
import com.sprint.mission.discodeit.dto.channel.PublicChannelDto;
import com.sprint.mission.discodeit.dto.channel.PublicChannelUpdateDto;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.PrivateChannelModificationException;
import com.sprint.mission.discodeit.exception.notfound.NotFoundChannelException;
import com.sprint.mission.discodeit.exception.notfound.NotFoundUserException;
import com.sprint.mission.discodeit.mapper.ChannelMapper;
import com.sprint.mission.discodeit.repository.ChannelRepository;
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
  private final ReadStatusRepository readStatusRepository;
  private final ChannelMapper channelMapper;

  @Override
  @Transactional
  public ChannelResponseDto createPublicChannel(PublicChannelDto publicChannelDto) {

    String name = publicChannelDto.name();
    String description = publicChannelDto.description();

    Channel channel = new Channel(name, description);

    channelRepository.save(channel);

    return channelMapper.toDto(channel);
  }

  @Override
  @Transactional
  public ChannelResponseDto createPrivateChannel(PrivateChannelDto privateChannelDto) {
    Channel channel = new Channel();

    Channel createdChannel = channelRepository.save(channel);

    // 읽음 상태 추가
    List<ReadStatus> readStatuses = privateChannelDto.participantIds().stream()
        .map(userId -> {
          User user = findUser(userId);
          ReadStatus readStatus = new ReadStatus(user, createdChannel, channel.getCreatedAt());
          createdChannel.getReadStatuses().add(readStatus);
          return readStatus;
        })
        .toList();

    readStatusRepository.saveAll(readStatuses);

    return channelMapper.toDto(channel);
  }

  @Override
  public ChannelResponseDto findById(UUID channelId) {
    Channel channel = findChannel(channelId);

    return channelMapper.toDto(channel);
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
        .map(channelMapper::toDto)
        .toList();
  }

  @Override
  @Transactional
  public ChannelResponseDto update(UUID channelId, PublicChannelUpdateDto publicChannelUpdateDto) {
    Channel channel = findChannel(channelId);

    // PRIVATE 채널은 수정 불가
    if (channel.getType().equals(ChannelType.PRIVATE)) {
      throw new PrivateChannelModificationException();
    }

    // 변경 사항 적용
    channel.updateName(publicChannelUpdateDto.newName());
    channel.updateDescription(publicChannelUpdateDto.newDescription());

    channelRepository.save(channel);

    return channelMapper.toDto(channel);
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
}
