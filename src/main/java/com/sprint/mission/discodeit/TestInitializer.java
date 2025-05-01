package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.factory.RepositoryBundle;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.basic.BasicChannelService;
import com.sprint.mission.discodeit.service.basic.BasicMessageService;
import com.sprint.mission.discodeit.service.basic.BasicUserService;
import java.util.UUID;

public class TestInitializer {

  public static void initializeAndTest(RepositoryBundle bundle) {
    UserService userService = new BasicUserService(
        bundle.getUserRepository(),
        bundle.getUserStatusRepository(),
        bundle.getBinaryContentRepository()
    );
    ChannelService channelService = new BasicChannelService(bundle.getChannelRepository(),
        bundle.getReadStatusRepository(), bundle.getMessageRepository());
    MessageService messageService = new BasicMessageService(
        bundle.getMessageRepository(),
        bundle.getUserRepository(),
        bundle.getChannelRepository()
    );

    User user = setupUser(userService);
    Channel channel = setupChannel(channelService, user.getId());
    messageCreateTest(messageService, channel, user);
    printTestResults(userService, channelService, messageService);
  }

  private static User setupUser(UserService userService) {
    return userService.createUser("test@test.com", "길동쓰", "pwd1234");
  }

  private static Channel setupChannel(ChannelService channelService, UUID creatorId) {
    return channelService.createChannel(creatorId, "공지", "공지 채널쓰");
  }

  private static void messageCreateTest(MessageService messageService, Channel channel,
      User author) {
    Message message = messageService.createMessage("안녕하세요.", author.getId(), channel.getId());
    System.out.println("메시지 생성 성공: " + message.getId());
  }

  private static void printTestResults(UserService userService, ChannelService channelService,
      MessageService messageService) {
    System.out.println("\n=== 테스트 결과 ===");

    System.out.println("모든 사용자:");
    userService.getAllUsers().forEach(System.out::println);

    System.out.println("\n모든 채널:");
    channelService.searchChannels(null, null).forEach(System.out::println);

    System.out.println("\n모든 메시지:");
    messageService.searchMessages(null, null, null).forEach(System.out::println);
  }
}
