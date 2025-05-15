package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.channel.ChannelMemberRequestDTO;
import com.sprint.mission.discodeit.dto.channel.ChannelResponseDTO;
import com.sprint.mission.discodeit.dto.channel.PrivateChannelDTO;
import com.sprint.mission.discodeit.dto.channel.PublicChannelDTO;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.*;
import com.sprint.mission.discodeit.exception.alreadyexist.UserAlreadyInChannelException;
import com.sprint.mission.discodeit.exception.notfound.NotFoundChannelException;
import com.sprint.mission.discodeit.exception.notfound.NotFoundMessageException;
import com.sprint.mission.discodeit.exception.notfound.NotFoundUserException;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service("basicChannelService")
@RequiredArgsConstructor
public class BasicChannelService implements ChannelService {

  private final ChannelRepository channelRepository;
  private final UserRepository userRepository;
  private final MessageRepository messageRepository;
  private final ReadStatusRepository readStatusRepository;

  @Override
  public Channel createPublicChannel(PublicChannelDTO publicChannelDTO) {
    // 존재하지 않는 사용자를 채널 주인으로 설정하는 경우 예외 처리
    if (userRepository.findById(publicChannelDTO.channelMaster()).isEmpty()) {
      throw new NotFoundUserException();
    }

    Channel channel = PublicChannelDTO.toEntity(publicChannelDTO);

    joinChannel(channel);
    createReadStatus(channel);

    channelRepository.save(channel);

    return channel;
  }

  @Override
  public Channel createPrivateChannel(PrivateChannelDTO privateChannelDTO) {
    // 존재하지 않는 사용자를 채널 주인으로 설정하는 경우 예외 처리
    if (userRepository.findById(privateChannelDTO.channelMaster()).isEmpty()) {
      throw new NotFoundUserException();
    }

    Channel channel = PrivateChannelDTO.toEntity(privateChannelDTO);

    joinChannel(channel);
    createReadStatus(channel);

    // 초대 받은 유저의 채널 리스트에 해당 채널 반영
    for (UUID userId : privateChannelDTO.users()) {
      User user = findUser(userId);
      user.getChannels().add(channel.getId());
      userRepository.save(user);
    }

    channelRepository.save(channel);

    return channel;
  }

  @Override
  public ChannelResponseDTO findById(UUID channelId) {
    Channel channel = findChannel(channelId);

    Instant lastMessageTime = getLastMessageTime(channel);
    channel.updateLastMessageTime(lastMessageTime);
    ChannelResponseDTO channelResponseDTO = Channel.toDTO(channel);

    return channelResponseDTO;
  }

  @Override
  public List<ChannelResponseDTO> findByNameContaining(String name) {
    List<Channel> nameContainingChannels = channelRepository.findByNameContaining(name);

    return nameContainingChannels.stream()
        .map(channel -> {
          Instant lastMessageTime = getLastMessageTime(channel);
          channel.updateLastMessageTime(lastMessageTime);
          ChannelResponseDTO channelResponseDTO = Channel.toDTO(channel);

          return channelResponseDTO;
        })
        .toList();
  }

  @Override
  public List<ChannelResponseDTO> findAllByUserId(UUID userId) {
    List<Channel> publicChannels = channelRepository.findAll().stream()
        .filter(channel -> !channel.isPrivate())
        .toList();

    // PRIVATE 채널은 조회한 유저가 참여한 채널만 조회
    List<Channel> privateChannels = channelRepository.findByPrivateChannelUserId(userId);

    List<Channel> channels = new ArrayList<>();
    channels.addAll(publicChannels);
    channels.addAll(privateChannels);

    return channels.stream()
        .map(channel -> {
          Instant lastMessageTime = getLastMessageTime(channel);
          channel.updateLastMessageTime(lastMessageTime);
          ChannelResponseDTO channelResponseDTO = Channel.toDTO(channel);

          return channelResponseDTO;
        })
        .toList();
  }

  @Override
  public List<ChannelResponseDTO> findAll() {
    return channelRepository.findAll().stream()
        .map(channel -> {
          Instant lastMessageTime = getLastMessageTime(channel);
          channel.updateLastMessageTime(lastMessageTime);
          ChannelResponseDTO channelResponseDTO = Channel.toDTO(channel);

          return channelResponseDTO;
        })
        .toList();
  }

  @Override
  public ChannelResponseDTO update(UUID channelId, PublicChannelDTO publicChannelDTO) {
    Channel channel = findChannel(channelId);

    // PRIVATE 채널은 수정 불가
    if (channel.isPrivate()) {
      throw new PrivateChannelModificationException();
    }

    // 존재하지 않는 사용자를 채널 주인으로 설정하는 경우 예외 처리
    if (userRepository.findById(publicChannelDTO.channelMaster()).isEmpty()) {
      throw new NotFoundUserException();
    }

    // 변경 사항 적용
    channel.updateChannelName(publicChannelDTO.channelName());
    channel.updateChannelMaster(publicChannelDTO.channelMaster());
    channel.updateDescription(publicChannelDTO.description());
    joinChannel(channel);

    channelRepository.save(channel);

    return Channel.toDTO(channel);
  }

  @Override
  public void deleteById(UUID channelId) {
    Channel channel = findChannel(channelId);

    // 채널에 속한 User의 channelList에서 해당 채널 삭제
    userRepository.findAll().forEach(user -> {
      if (user.getChannels().removeIf(id -> id.equals(channelId))) {
        userRepository.save(user);
      }
    });

    // 채널에 있는 모든 메시지 삭제
    channel.getMessages().forEach(messageRepository::deleteById);

    readStatusRepository.deleteByChannelId(channelId);
    channelRepository.deleteById(channelId);
  }

  // 채널에 사용자 추가
  @Override
  public void inviteUser(ChannelMemberRequestDTO channelMemberRequestDTO) {
    Channel channel = findChannel(channelMemberRequestDTO.channelId());
    User user = findUser(channelMemberRequestDTO.userId());

    UUID channelId = channel.getId();
    UUID userId = user.getId();

    if (channel.getUsers().contains(userId)) {
      throw new UserAlreadyInChannelException(user.getName() + "은 이미 채널에 있습니다.");
    }

    // Channel의 userList에 해당 user 추가
    channel.getUsers().add(userId);
    user.getChannels().add(channelId);

    ReadStatus readStatus = new ReadStatus(userId, channelId, Instant.now());

    userRepository.save(user);
    channelRepository.save(channel);
    readStatusRepository.save(readStatus);
  }

  // 채널에서 사용자 제거
  @Override
  public void kickUser(ChannelMemberRequestDTO channelMemberRequestDTO) {
    Channel channel = findChannel(channelMemberRequestDTO.channelId());
    User user = findUser(channelMemberRequestDTO.userId());

    UUID channelId = channel.getId();
    UUID userId = user.getId();

    if (!channel.getUsers().contains(userId)) {
      throw new UserNotInChannelException(user.getName() + "은 채널에 속해있지 않습니다.");
    }

    // Channel의 userList에 해당 user 추가
    channel.getUsers().remove(userId);
    // User의 channelList에 해당 channel 추가
    user.getChannels().remove(channelId);

    userRepository.save(user);
    channelRepository.save(channel);
    // 채널에 속한 User의 ReadStatus 정보 삭제
    readStatusRepository.deleteByChannelIdAndUserId(channelId, userId);
  }

  private void joinChannel(Channel channel) {
    User user = findUser(channel.getChannelMaster());

    // 채널 주인은 채널 생성 시 채널에 입장
    if (!user.getChannels().contains(channel.getId())) {
      user.getChannels().add(channel.getId());
      channel.getUsers().add(user.getId());
      userRepository.save(user);
    }
  }

  private void createReadStatus(Channel channel) {
    channel.getUsers().forEach(userId -> {
      ReadStatus readStatus = new ReadStatus(userId, channel.getId(), Instant.now());
      readStatusRepository.save(readStatus);
    });
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
      UUID lastMessageId = channel.getMessages().get(messageCount - 1);

      return messageRepository.findById(lastMessageId)
          .orElseThrow(NotFoundMessageException::new)
          .getCreatedAt();
    } else {
      return null;
    }
  }
}
