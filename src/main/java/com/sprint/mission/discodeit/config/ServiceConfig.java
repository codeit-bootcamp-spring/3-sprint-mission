package com.sprint.mission.discodeit.config;

import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.basic.BasicChannelService;
import com.sprint.mission.discodeit.service.basic.BasicMessageService;
import com.sprint.mission.discodeit.service.basic.BasicUserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ServiceConfig {

    @Bean
    public UserService userService() {
        return new BasicUserService();
    }

    @Bean
    public ChannelService channelService() {
        return new BasicChannelService();
    }

    @Bean
    public MessageService messageService() {
        return new BasicMessageService();
    }
}
