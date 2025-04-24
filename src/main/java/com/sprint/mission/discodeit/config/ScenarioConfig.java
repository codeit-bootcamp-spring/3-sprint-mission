package com.sprint.mission.discodeit.config;

import com.sprint.mission.discodeit.flow.TestScenario;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.basic.BasicChannelService;
import com.sprint.mission.discodeit.service.basic.BasicMessageService;
import com.sprint.mission.discodeit.service.basic.BasicUserService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ScenarioConfig {
    @Bean("fileUserService")
    public UserService fileUserService(
            @Qualifier("fileUserRepository") UserRepository userRepository
    ) {
        return new BasicUserService(userRepository);
    }

    @Bean("jcfUserService")
    public UserService jcfUserService(
            @Qualifier("jcfUserRepository") UserRepository userRepository
    ) {
        return new BasicUserService(userRepository);
    }

    @Bean("fileChannelService")
    public ChannelService fileChannelService(
            @Qualifier("fileChannelRepository") ChannelRepository channelRepository
    ) {
        return new BasicChannelService(channelRepository);
    }

    @Bean("jcfChannelService")
    public ChannelService jcfChannelService(
            @Qualifier("jcfChannelRepository") ChannelRepository channelRepository
    ) {
        return new BasicChannelService(channelRepository);
    }

    @Bean("fileMessageService")
    public MessageService fileMessageService(
            @Qualifier("fileMessageRepository") MessageRepository messageRepository,
            @Qualifier("fileUserRepository") UserRepository userRepository,
            @Qualifier("fileChannelRepository") ChannelRepository channelRepository
    ) {
        return new BasicMessageService(userRepository, channelRepository, messageRepository);
    }

    @Bean("jcfMessageService")
    public MessageService jcfMessageService(
            @Qualifier("jcfMessageRepository") MessageRepository messageRepository,
            @Qualifier("jcfUserRepository") UserRepository userRepository,
            @Qualifier("jcfChannelRepository") ChannelRepository channelRepository
    ) {
        return new BasicMessageService(userRepository, channelRepository, messageRepository);
    }

    @Bean("fileTest")
    public TestScenario fileTestExecutor(
            @Qualifier("fileUserService") UserService userService,
            @Qualifier("fileChannelService") ChannelService channelService,
            @Qualifier("fileMessageService") MessageService messageService
    ) {
        return new TestScenario(userService, channelService, messageService);
    }

    @Bean("jcfTest")
    public TestScenario jcfTestExecutor(
            @Qualifier("jcfUserService") UserService userService,
            @Qualifier("jcfChannelService") ChannelService channelService,
            @Qualifier("jcfMessageService") MessageService messageService
    ) {
        return new TestScenario(userService, channelService, messageService);
    }
}