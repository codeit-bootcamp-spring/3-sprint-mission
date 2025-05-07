package com.sprint.mission.discodeit.configuration;

import com.sprint.mission.discodeit.repository.file.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.nio.file.Path;

// Config를 통해 Spring Bean들을 설정하는 클래스
@Configuration
public class ApplicationConfiguration {

    // 파일 경로를 Bean으로 등록!
    @Bean
    public Path userFilePath() {
        return Path.of("src/main/java/com/sprint/mission/discodeit/files/users.json");
    }

    @Bean
    public FileUserRepository fileUserRepository(Path userFilePath) {
        return new FileUserRepository(userFilePath);
    }

    @Bean
    public Path channelFilePath() {
        return Path.of("src/main/java/com/sprint/mission/discodeit/files/channels.json");
    }

    @Bean
    public FileChannelRepository fileChannelRepository(Path channelFilePath) {
        return new FileChannelRepository(channelFilePath);
    }

    @Bean
    public Path messageFilePath() {
        return Path.of("src/main/java/com/sprint/mission/discodeit/files/messages.json");
    }

    @Bean
    public FileMessageRepository fileMessageRepository(Path messageFilePath){
        return new FileMessageRepository(messageFilePath);
    }

    @Bean
    public Path userStatusFilePath() {
        return Path.of("src/main/java/com/sprint/mission/discodeit/files/userstatus.json");
    }

    @Bean
    public FileUserStatusRepository fileUserStatusRepository(Path userStatusFilePath) {
        return new FileUserStatusRepository(userStatusFilePath);
    }

    @Bean
    public Path readStatusFilePath() {
        return Path.of("src/main/java/com/sprint/mission/discodeit/files/readstatus.json");
    }

    @Bean
    public FileReadStatusRepository fileReadStatusRepository(Path readStatusFilePath) {
        return new FileReadStatusRepository(readStatusFilePath);
    }

    @Bean
    public Path binaryContentFilePath() {
        return Path.of("src/main/java/com/sprint/mission/discodeit/files/binarycontent.json");
    }

    @Bean
    public FileBinaryContentRepository fileBinaryContentRepository(Path binaryContentFilePath) {
        return new FileBinaryContentRepository(binaryContentFilePath);
    }


}
