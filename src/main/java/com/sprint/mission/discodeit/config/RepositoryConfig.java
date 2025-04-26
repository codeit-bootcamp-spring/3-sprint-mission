package com.sprint.mission.discodeit.config;

import com.sprint.mission.discodeit.repository.file.FileChannelRepository;
import com.sprint.mission.discodeit.repository.file.FileMessageRepository;
import com.sprint.mission.discodeit.repository.file.FileUserRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RepositoryConfig {

  @Bean
  public FileUserRepository fileUserRepository() {
    return FileUserRepository.createDefault();
  }

  @Bean
  public FileChannelRepository fileChannelRepository() {
    return FileChannelRepository.createDefault();
  }

  @Bean
  public FileMessageRepository fileMessageRepository() {
    return FileMessageRepository.createDefault();
  }
}