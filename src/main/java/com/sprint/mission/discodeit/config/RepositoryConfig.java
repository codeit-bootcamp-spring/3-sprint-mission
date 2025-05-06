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
import com.sprint.mission.discodeit.repository.storage.FileStorage;
import com.sprint.mission.discodeit.repository.storage.FileStorageImpl;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RepositoryConfig {

  @Bean
  @ConditionalOnProperty(prefix = "discodeit.repository", name = "type", havingValue = "file")
  public UserRepository fileUserRepository(RepositoryProperties props) {
    FileStorage fileStorage = new FileStorageImpl(props.getFileDirectory() + "/users");
    return FileUserRepository.create(fileStorage);
  }

  @Bean
  @ConditionalOnProperty(prefix = "discodeit.repository", name = "type", havingValue = "jcf", matchIfMissing = true)
  public UserRepository jcfUserRepository() {
    return new JCFUserRepository();
  }

  @Bean
  @ConditionalOnProperty(prefix = "discodeit.repository", name = "type", havingValue = "file")
  public UserStatusRepository fileUserStatusRepository(RepositoryProperties props) {
    FileStorage fileStorage = new FileStorageImpl(props.getFileDirectory() + "/user-status");
    return FileUserStatusRepository.create(fileStorage);
  }


  @Bean
  @ConditionalOnProperty(prefix = "discodeit.repository", name = "type", havingValue = "jcf", matchIfMissing = true)
  public UserStatusRepository jcfUserStatusRepository() {
    return new JCFUserStatusRepository();
  }

  @Bean
  @ConditionalOnProperty(prefix = "discodeit.repository", name = "type", havingValue = "file")
  public ChannelRepository fileChannelRepository(RepositoryProperties props) {
    FileStorage fileStorage = new FileStorageImpl(props.getFileDirectory() + "/channels");
    return FileChannelRepository.create(fileStorage);
  }

  @Bean
  @ConditionalOnProperty(prefix = "discodeit.repository", name = "type", havingValue = "jcf", matchIfMissing = true)
  public ChannelRepository jcfChannelRepository() {
    return new JCFChannelRepository();
  }

  @Bean
  @ConditionalOnProperty(prefix = "discodeit.repository", name = "type", havingValue = "file")
  public MessageRepository fileMessageRepository(RepositoryProperties props) {
    FileStorage fileStorage = new FileStorageImpl(props.getFileDirectory() + "/messages");
    return FileMessageRepository.create(fileStorage);
  }

  @Bean
  @ConditionalOnProperty(prefix = "discodeit.repository", name = "type", havingValue = "jcf", matchIfMissing = true)
  public MessageRepository jcfMessageRepository() {
    return new JCFMessageRepository();
  }

  @Bean
  @ConditionalOnProperty(prefix = "discodeit.repository", name = "type", havingValue = "file")
  public BinaryContentRepository fileBinaryContentRepository(RepositoryProperties props) {
    FileStorage fileStorage = new FileStorageImpl(props.getFileDirectory() + "/binary-content");
    return FileBinaryContentRepository.create(fileStorage);
  }


  @Bean
  @ConditionalOnProperty(prefix = "discodeit.repository", name = "type", havingValue = "jcf", matchIfMissing = true)
  public BinaryContentRepository jcfBinaryContentRepository() {
    return new JCFBinaryContentRepository();
  }
  
  @Bean
  @ConditionalOnProperty(prefix = "discodeit.repository", name = "type", havingValue = "file")
  public ReadStatusRepository fileReadStatusRepository(RepositoryProperties props) {
    FileStorage fileStorage = new FileStorageImpl(props.getFileDirectory() + "/read-status");
    return FileReadStatusRepository.create(fileStorage);
  }

  @Bean
  @ConditionalOnProperty(prefix = "discodeit.repository", name = "type", havingValue = "jcf", matchIfMissing = true)
  public ReadStatusRepository jcfReadStatusRepository() {
    return new JCFReadStatusRepository();
  }
}