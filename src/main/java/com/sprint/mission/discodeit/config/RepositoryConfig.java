package com.sprint.mission.discodeit.config;

import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.repository.file.FileBinaryContentRepository;
import com.sprint.mission.discodeit.repository.file.FileChannelRepository;
import com.sprint.mission.discodeit.repository.file.FileMessageRepository;
import com.sprint.mission.discodeit.repository.file.FileReadStatusRepository;
import com.sprint.mission.discodeit.repository.file.FileUserRepository;
import com.sprint.mission.discodeit.repository.file.FileUserStatusRepository;
import com.sprint.mission.discodeit.repository.jcf.JCFBinaryContentRepository;
import com.sprint.mission.discodeit.repository.jcf.JCFChannelRepository;
import com.sprint.mission.discodeit.repository.jcf.JCFMessageRepository;
import com.sprint.mission.discodeit.repository.jcf.JCFReadStatusRepository;
import com.sprint.mission.discodeit.repository.jcf.JCFUserRepository;
import com.sprint.mission.discodeit.repository.jcf.JCFUserStatusRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RepositoryConfig {

  // UserRepository
  @Bean
  @ConditionalOnProperty(prefix = "discodeit.repository", name = "type", havingValue = "file")
  public UserRepository fileUserRepository(RepositoryProperties props) {
    return FileUserRepository.from(props.getFileDirectory() + "/users.ser");
  }

  @Bean
  @ConditionalOnProperty(prefix = "discodeit.repository", name = "type", havingValue = "jcf", matchIfMissing = true)
  public UserRepository jcfUserRepository() {
    return new JCFUserRepository();
  }

  // UserStatusRepository
  @Bean
  @ConditionalOnProperty(prefix = "discodeit.repository", name = "type", havingValue = "file")
  public UserStatusRepository fileUserStatusRepository(RepositoryProperties props) {
    return FileUserStatusRepository.from(props.getFileDirectory() + "/user-status.ser");
  }

  @Bean
  @ConditionalOnProperty(prefix = "discodeit.repository", name = "type", havingValue = "jcf", matchIfMissing = true)
  public UserStatusRepository jcfUserStatusRepository() {
    return new JCFUserStatusRepository();
  }

  // ChannelRepository
  @Bean
  @ConditionalOnProperty(prefix = "discodeit.repository", name = "type", havingValue = "file")
  public ChannelRepository fileChannelRepository(RepositoryProperties props) {
    return FileChannelRepository.from(props.getFileDirectory() + "/channels.ser");
  }

  @Bean
  @ConditionalOnProperty(prefix = "discodeit.repository", name = "type", havingValue = "jcf", matchIfMissing = true)
  public ChannelRepository jcfChannelRepository() {
    return new JCFChannelRepository();
  }

  // MessageRepository
  @Bean
  @ConditionalOnProperty(prefix = "discodeit.repository", name = "type", havingValue = "file")
  public MessageRepository fileMessageRepository(RepositoryProperties props) {
    return FileMessageRepository.from(props.getFileDirectory() + "/messages.ser");
  }

  @Bean
  @ConditionalOnProperty(prefix = "discodeit.repository", name = "type", havingValue = "jcf", matchIfMissing = true)
  public MessageRepository jcfMessageRepository() {
    return new JCFMessageRepository();
  }

  // BinaryContentRepository
  @Bean
  @ConditionalOnProperty(prefix = "discodeit.repository", name = "type", havingValue = "file")
  public BinaryContentRepository fileBinaryContentRepository(RepositoryProperties props) {
    return FileBinaryContentRepository.from(props.getFileDirectory() + "/binary-content.ser");
  }

  @Bean
  @ConditionalOnProperty(prefix = "discodeit.repository", name = "type", havingValue = "jcf", matchIfMissing = true)
  public BinaryContentRepository jcfBinaryContentRepository() {
    return new JCFBinaryContentRepository();
  }

  // ReadStatusRepository
  @Bean
  @ConditionalOnProperty(prefix = "discodeit.repository", name = "type", havingValue = "file")
  public ReadStatusRepository fileReadStatusRepository(RepositoryProperties props) {
    return FileReadStatusRepository.from(props.getFileDirectory() + "/read-status.ser");
  }

  @Bean
  @ConditionalOnProperty(prefix = "discodeit.repository", name = "type", havingValue = "jcf", matchIfMissing = true)
  public ReadStatusRepository jcfReadStatusRepository() {
    return new JCFReadStatusRepository();
  }
}