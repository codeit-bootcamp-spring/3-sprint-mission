package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.config.RepositoryProperties;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;
import java.util.UUID;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class TestInitializer {

  private static final Logger log = LogManager.getLogger(TestInitializer.class);

  public static void initializeAndTest(UserService userService, ChannelService channelService,
      MessageService messageService, RepositoryProperties props) {
    log.info("=== 테스트 시작 (Repository 타입: {}) ===", props.getType());

    User user = setupUser(userService);
    Channel channel = setupChannel(channelService, user.getId());
    messageCreateTest(messageService, channel, user);
    printTestResults(userService, channelService, messageService);
  }

  private static User setupUser(UserService userService) {
    return userService.create("test@test.com", "길동쓰", "pwd1234");
  }

  private static Channel setupChannel(ChannelService channelService, UUID creatorId) {
    Channel channel = channelService.create(creatorId, "공지", "공지 채널쓰");
    channelService.addParticipant(channel.getId(), creatorId);
    log.info("채널 생성 완료: {}", channel.getId());
    return channel;
  }

  private static void messageCreateTest(MessageService messageService, Channel channel,
      User author) {
    Message message = messageService.create("안녕하세요.", author.getId(), channel.getId());
    log.info("메시지 생성 완료: {}", message.getId());
  }

  private static void printTestResults(UserService userService, ChannelService channelService,
      MessageService messageService) {
    log.info("=== 테스트 결과 ===");

    log.info("모든 사용자:");
    userService.findAll().forEach(user -> log.info(user.toString()));

    log.info("모든 채널:");
    channelService.findByCreatorIdOrName(null, null)
        .forEach(channel -> log.info(channel.toString()));

    log.info("모든 메시지:");
    messageService.searchMessages(null, null, null)
        .forEach(message -> log.info(message.toString()));
  }
}