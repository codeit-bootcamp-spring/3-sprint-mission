package com.sprint.mission.discodeit.configuration;

import com.sprint.mission.discodeit.repository.file.FileMessageRepository;
import com.sprint.mission.discodeit.repository.file.FileUserRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.nio.file.Path;

@Configuration
public class ApplicationConfiguration {

    @Bean
    public Path userFilePath() {
        return Path.of("src/main/java/discodeit/files/users.json");
    }

    @Bean
    public FileUserRepository fileUserRepository(Path userFilePath) {
        return new FileUserRepository(userFilePath);
    }

    @Bean
    public Path channelFilePath() {
        return Path.of("src/main/java/discodeit/files/channels.json");
    }

    @Bean
    public FileUserRepository fileChannelRepository(Path channelFilePath) {
        return new FileUserRepository(channelFilePath);
    }

    @Bean
    public Path messageFilePath() {
        return Path.of("src/main/java/discodeit/files/messages.json");
    }

    @Bean
    public FileMessageRepository fileMessageRepository(Path messageFilePath){
        return new FileMessageRepository(messageFilePath);
    }

    @Bean
    public Path userStatusFilePath() {
        return Path.of("src/main/java/discodeit/files/userstatus.json");
    }

    @Bean
    public FileUserRepository fileUserStatusRepository(Path userStatusFilePath) {
        return new FileUserRepository(userStatusFilePath);
    }

    @Bean
    public Path readStatusFilePath() {
        return Path.of("src/main/java/discodeit/files/readstatus.json");
    }

    @Bean
    public FileUserRepository fileReadStatusRepository(Path readStatusFilePath) {
        return new FileUserRepository(readStatusFilePath);
    }

    @Bean
    public Path binaryContentFilePath() {
        return Path.of("src/main/java/discodeit/files/binarycontent.json");
    }

    @Bean
    public FileUserRepository fileBinaryContentRepository(Path binaryContentFilePath) {
        return new FileUserRepository(binaryContentFilePath);
    }


}
