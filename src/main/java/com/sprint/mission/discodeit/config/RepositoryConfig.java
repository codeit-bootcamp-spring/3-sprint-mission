package com.sprint.mission.discodeit.config;

import com.sprint.mission.discodeit.repository.file.*;
import com.sprint.mission.discodeit.repository.jcf.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RepositoryConfig {

    @Value("${discodeit.repository.file-directory}")
    private String filePath;

    @Bean
    @ConditionalOnProperty(name = "discodeit.repository.type", havingValue = "jcf")
    public JCFUserRepository jcfUserRepository() {
        System.out.println(filePath);
        return new JCFUserRepository();
    }

    @Bean
    @ConditionalOnProperty(name = "discodeit.repository.type", havingValue = "file")
    public FileUserRepository fileUserRepository() {
        return new FileUserRepository(filePath);
    }

    @Bean
    @ConditionalOnProperty(name = "discodeit.repository.type", havingValue = "jcf")
    public JCFChannelRepository jcfChannelRepository() {
        return new JCFChannelRepository();
    }

    @Bean
    @ConditionalOnProperty(name = "discodeit.repository.type", havingValue = "file")
    public FileChannelRepository fileChannelRepository() {
        return new FileChannelRepository(filePath);
    }

    @Bean
    @ConditionalOnProperty(name = "discodeit.repository.type", havingValue = "jcf")
    public JCFMessageRepository jcfMessageRepository() {
        return new JCFMessageRepository();
    }

    @Bean
    @ConditionalOnProperty(name = "discodeit.repository.type", havingValue = "file")
    public FileMessageRepository fileMessageRepository() {
        return new FileMessageRepository(filePath);
    }

    @Bean
    @ConditionalOnProperty(name = "discodeit.repository.type", havingValue = "jcf")
    public JCFReadStatusRepository jcfReadStatusRepository() {
        return new JCFReadStatusRepository();
    }

    @Bean
    @ConditionalOnProperty(name = "discodeit.repository.type", havingValue = "file")
    public FileReadStatusRepository fileReadStatusRepository() {
        return new FileReadStatusRepository(filePath);
    }

    @Bean
    @ConditionalOnProperty(name = "discodeit.repository.type", havingValue = "jcf")
    public JCFUserStatusRepository jcfUserStatusRepository() {
        return new JCFUserStatusRepository();
    }

    @Bean
    @ConditionalOnProperty(name = "discodeit.repository.type", havingValue = "file")
    public FileUserStatusRepository fileUserStatusRepository() {
        return new FileUserStatusRepository(filePath);
    }

    @Bean
    @ConditionalOnProperty(name = "discodeit.repository.type", havingValue = "jcf")
    public JCFBinaryContentRepository jcfBinaryContentRepository() {
        return new JCFBinaryContentRepository();
    }

    @Bean
    @ConditionalOnProperty(name = "discodeit.repository.type", havingValue = "file")
    public FileBinaryContentRepository fileBinaryContentRepository() {
        return new FileBinaryContentRepository(filePath);
    }
}
