package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TestConfiguration {

    @Bean("fileTest")
    public TestLogic fileTestExecutor(
            UserService fileUserService,
            ChannelService fileChannelService,
            MessageService fileMessageService
    ) {
        return new TestLogic(
                fileUserService, fileChannelService, fileMessageService
        );
    }

    @Bean("jfcTest")
    public TestLogic jfcTestExecutor(
            UserService fileUserService,
            ChannelService fileChannelService,
            MessageService fileMessageService
    ) {
        return new TestLogic(
                fileUserService, fileChannelService, fileMessageService
        );
    }
}