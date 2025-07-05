package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.service.AuthService;
import com.sprint.mission.discodeit.service.BinaryContentService;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.ReadStatusService;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.UserStatusService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class DiscodeitApplication {

  public static void main(String[] args) {
    ConfigurableApplicationContext context = SpringApplication.run(DiscodeitApplication.class,
        args);

    // application.yml 설정에 따라 Service Bean들을 스프링 컨텍스트에서 가져오기
    UserService userService = context.getBean(UserService.class);
    ChannelService channelService = context.getBean(ChannelService.class);
    MessageService messageService = context.getBean(MessageService.class);
    UserStatusService userStatusService = context.getBean(UserStatusService.class);
    AuthService authService = context.getBean(AuthService.class);
    BinaryContentService binaryContentService = context.getBean(BinaryContentService.class);
    ReadStatusService readStatusService = context.getBean(ReadStatusService.class);

    // 테스트 실행
    TestInitializer.initializeAndTest(userService, channelService, messageService,
        userStatusService, authService, binaryContentService, readStatusService);
  }
}