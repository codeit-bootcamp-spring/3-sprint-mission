package com.sprint.mission.discodeit.repository.factory;

import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;

public class RepositoryBundle {

  private final UserRepository userRepository;
  private final ChannelRepository channelRepository;
  private final MessageRepository messageRepository;

  public RepositoryBundle(UserRepository userRepository,
      ChannelRepository channelRepository, MessageRepository messageRepository) {
    this.userRepository = userRepository;
    this.channelRepository = channelRepository;
    this.messageRepository = messageRepository;
  }

  public UserRepository getUserRepository() {
    return userRepository;
  }

  public ChannelRepository getChannelRepository() {
    return channelRepository;
  }

  public MessageRepository getMessageRepository() {
    return messageRepository;
  }
}
