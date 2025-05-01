package com.sprint.mission.discodeit.config;

import com.sprint.mission.discodeit.flow.TestScenario;
import com.sprint.mission.discodeit.repository.*;
import com.sprint.mission.discodeit.service.*;
import com.sprint.mission.discodeit.service.basic.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ScenarioConfig {
    @Bean("fileUserService")
    public UserService fileUserService (
            @Qualifier("fileUserRepository") UserRepository userRepository,
            @Qualifier("fileUserStatusRepository") UserStatusRepository userStatusRepository,
            @Qualifier("fileReadStatusRepository") ReadStatusRepository readStatusRepository,
            @Qualifier("fileBinaryContentRepository") BinaryContentRepository binaryContentRepository
    ) {
        return new BasicUserService(userRepository, userStatusRepository, readStatusRepository, binaryContentRepository);
    }

    @Bean("jcfUserService")
    public UserService jcfUserService (
            @Qualifier("jcfUserRepository") UserRepository userRepository,
            @Qualifier("jcfUserStatusRepository") UserStatusRepository userStatusRepository,
            @Qualifier("jcfReadStatusRepository") ReadStatusRepository readStatusRepository,
            @Qualifier("jcfBinaryContentRepository") BinaryContentRepository binaryContentRepository
    ) {
        return new BasicUserService(userRepository, userStatusRepository, readStatusRepository, binaryContentRepository);
    }

    @Bean("fileUserStatusService")
    public UserStatusService fileUserStatusService(
            @Qualifier("fileUserStatusRepository") UserStatusRepository userStatusRepository
    ) {
        return new BasicUserStatusService(userStatusRepository);
    }

    @Bean("jcfUserStatusService")
    public UserStatusService jcfUserStatusService (
            @Qualifier("jcfUserStatusRepository") UserStatusRepository userStatusRepository
    ) {
        return new BasicUserStatusService(userStatusRepository);
    }

    @Bean("fileReadStatusService")
    public ReadStatusService fileReadStatusService (
            @Qualifier("fileReadStatusRepository") ReadStatusRepository readStatusRepository
    ) {
        return new BasicReadStatusService(readStatusRepository);
    }

    @Bean("jcfReadStatusService")
    public ReadStatusService jfcReadStatusService (
            @Qualifier("jcfReadStatusRepository") ReadStatusRepository readStatusRepository
    ) {
        return new BasicReadStatusService(readStatusRepository);
    }

    @Bean("fileBinaryContentService")
    public BinaryContentService fileBinaryContentService (
            @Qualifier("fileBinaryContentRepository") BinaryContentRepository binaryContentRepository
    ) {
        return new BasicBinaryContentService(binaryContentRepository);
    }

    @Bean("jcfBinaryContentService")
    public BinaryContentService jcfBinaryContentService (
            @Qualifier("jcfBinaryContentRepository") BinaryContentRepository binaryContentRepository
    ) {
        return new BasicBinaryContentService(binaryContentRepository);
    }

    @Bean("fileAuthService")
    public AuthService fileAuthService(
            @Qualifier("fileUserRepository") UserRepository userRepository,
            @Qualifier("fileUserStatusRepository") UserStatusRepository userStatusRepository
    ) {
        return new BasicAuthService(userRepository, userStatusRepository);
    }

    @Bean("jcfAuthService")
    public AuthService jcfAuthService(
            @Qualifier("jcfUserRepository") UserRepository userRepository,
            @Qualifier("jcfUserStatusRepository") UserStatusRepository userStatusRepository
    ) {
        return new BasicAuthService(userRepository, userStatusRepository);
    }

    @Bean("fileChannelService")
    public ChannelService fileChannelService (
            @Qualifier("fileChannelRepository") ChannelRepository channelRepository
    ) {
        return new BasicChannelService(channelRepository);
    }

    @Bean("jcfChannelService")
    public ChannelService jcfChannelService (
            @Qualifier("jcfChannelRepository") ChannelRepository channelRepository
    ) {
        return new BasicChannelService(channelRepository);
    }

    @Bean("fileMessageService")
    public MessageService fileMessageService (
            @Qualifier("fileMessageRepository") MessageRepository messageRepository,
            @Qualifier("fileUserRepository") UserRepository userRepository,
            @Qualifier("fileChannelRepository") ChannelRepository channelRepository
    ) {
        return new BasicMessageService(userRepository, channelRepository, messageRepository);
    }

    @Bean("jcfMessageService")
    public MessageService jcfMessageService (
            @Qualifier("jcfMessageRepository") MessageRepository messageRepository,
            @Qualifier("jcfUserRepository") UserRepository userRepository,
            @Qualifier("jcfChannelRepository") ChannelRepository channelRepository
    ) {
        return new BasicMessageService(userRepository, channelRepository, messageRepository);
    }

    @Bean("fileTest")
    public TestScenario fileTestExecutor (
            @Qualifier("fileUserService") UserService userService,
            @Qualifier("fileUserStatusService") UserStatusService userStatusService,
            @Qualifier("fileReadStatusService") ReadStatusService readStatusService,
            @Qualifier("fileBinaryContentService") BinaryContentService binaryContentService,
            @Qualifier("fileAuthService") AuthService authService,
            @Qualifier("fileChannelService") ChannelService channelService,
            @Qualifier("fileMessageService") MessageService messageService
            ) {
        return new TestScenario(userService, userStatusService, readStatusService, binaryContentService, authService, channelService, messageService);
    }

    @Bean("jcfTest")
    public TestScenario jcfTestExecutor (
            @Qualifier("jcfUserService") UserService userService,
            @Qualifier("jcfUserStatusService") UserStatusService userStatusService,
            @Qualifier("jcfReadStatusService") ReadStatusService readStatusService,
            @Qualifier("jcfBinaryContentService") BinaryContentService binaryContentService,
            @Qualifier("jcfAuthService") AuthService authService,
            @Qualifier("jcfChannelService") ChannelService channelService,
            @Qualifier("jcfMessageService") MessageService messageService
    ) {
        return new TestScenario(userService, userStatusService, readStatusService, binaryContentService , authService, channelService, messageService);
    }
}