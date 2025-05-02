package com.sprint.mission.discodeit.repository.factory;

import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;

public class RepositoryBundle {

  private final UserRepository userRepository;
  private final UserStatusRepository userStatusRepository;
  private final ChannelRepository channelRepository;
  private final MessageRepository messageRepository;
  private final BinaryContentRepository binaryContentRepository;
  private final ReadStatusRepository readStatusRepository;

  public RepositoryBundle(UserRepository userRepository, UserStatusRepository userStatusRepository,
      ChannelRepository channelRepository, MessageRepository messageRepository,
      BinaryContentRepository binaryContentRepository, ReadStatusRepository readStatusRepository) {
    this.userRepository = userRepository;
    this.userStatusRepository = userStatusRepository;
    this.channelRepository = channelRepository;
    this.messageRepository = messageRepository;
    this.binaryContentRepository = binaryContentRepository;
    this.readStatusRepository = readStatusRepository;
  }

  public UserRepository getUserRepository() {
    return userRepository;
  }

  public UserStatusRepository getUserStatusRepository() {
    return userStatusRepository;
  }

  public ChannelRepository getChannelRepository() {
    return channelRepository;
  }

  public MessageRepository getMessageRepository() {
    return messageRepository;
  }

  public BinaryContentRepository getBinaryContentRepository() {
    return binaryContentRepository;
  }

  public ReadStatusRepository getReadStatusRepository() {
    return readStatusRepository;
  }
}