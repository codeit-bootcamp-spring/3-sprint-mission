package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.repository.file.FileChannelRepository;
import com.sprint.mission.discodeit.repository.file.FileMessageRepository;
import com.sprint.mission.discodeit.repository.file.FileUserRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
public class RepositoryConfig {

    @Bean
    public UserRepository userRepository() {
        Path path = Paths.get(System.getProperty("user.dir"), "data", "users");
        return new FileUserRepository(path);
    }

    @Bean
    public ChannelRepository channelRepository() {
        Path path = Paths.get(System.getProperty("user.dir"), "data", "channels");
        return new FileChannelRepository(path);
    }

    @Bean
    public MessageRepository messageRepository() {
        Path path = Paths.get(System.getProperty("user.dir"), "data", "messages");
        return new FileMessageRepository(path);
    }
}
