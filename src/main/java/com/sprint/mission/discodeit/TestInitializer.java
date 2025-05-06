package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.LoginRequest;
import com.sprint.mission.discodeit.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.request.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserUpdateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.BinaryContent.ContentType;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.service.AuthService;
import com.sprint.mission.discodeit.service.BinaryContentService;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.ReadStatusService;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.UserStatusService;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class TestInitializer {

  private static final Logger log = LogManager.getLogger(TestInitializer.class);

  public static void initializeAndTest(UserService userService, ChannelService channelService,
      MessageService messageService, UserStatusService userStatusService, AuthService authService,
      BinaryContentService binaryContentService, ReadStatusService readStatusService) {
    log.info("=== 테스트 시작 ===");

    User user1 = setupUser(userService, "user1@test.com", "길동쓰1", "pwd1234");
    User user2 = setupUser(userService, "user2@test.com", "길동쓰쓰2", "pwd5678");

    loginUser(authService, userStatusService, user1);
    loginUser(authService, userStatusService, user2);

    addProfileImageToUser(userService, user1);
    addProfileImageToUser(userService, user2);

    Channel publicChannel = createPublicChannel(channelService, user1.getId());
    Channel privateChannel = createPrivateChannel(channelService, user1.getId(),
        List.of(user1.getId(), user2.getId()));

    createMessage(messageService, publicChannel, user1);
    createMessageWithAttachment(messageService, binaryContentService, privateChannel, user2);

    testReadStatus(readStatusService, publicChannel, user1);

    printTestResults(userService, channelService, messageService);
  }

  private static User setupUser(UserService userService, String email, String name,
      String password) {
    return userService.create(email, name, password);
  }

  private static void loginUser(AuthService authService, UserStatusService userStatusService,
      User user) {
    authService.login(new LoginRequest(user.getName(), user.getPassword()));
    UserStatus userStatus = userStatusService.updateByUserId(user.getId());
    log.info("유저 로그인 완료: 이름: {}, 상태: {}, UUID: {}", user.getName(), userStatus.isOnline(),
        user.getId());
  }

  private static void addProfileImageToUser(UserService userService, User user) {
    try {
      userService.update(new UserUpdateRequest(user.getId(), user.getName(), user.getPassword(),
          UUID.randomUUID()));
      log.info("프로필 이미지 추가 완료: {}", user.getId());
    } catch (Exception e) {
      log.warn("프로필 이미지 추가 실패: {}", user.getId());
    }
  }

  private static Channel createPublicChannel(ChannelService channelService, UUID creatorId) {
    Channel channel = channelService.createPublic(
        new PublicChannelCreateRequest(creatorId, "공지 채널", "공지사항을 위한 채널"));
    log.info("Public 채널 생성 완료: {}", channel.getId());
    return channel;
  }

  private static Channel createPrivateChannel(ChannelService channelService, UUID creatorId,
      List<UUID> participantIds) {
    Channel channel = channelService.createPrivate(
        new PrivateChannelCreateRequest(creatorId, participantIds));
    log.info("Private 채널 생성 완료: {}", channel.getId());
    return channel;
  }

  private static void createMessage(MessageService messageService, Channel channel, User author) {
    Message message = messageService.create("안녕하세요.", author.getId(), channel.getId());
    log.info("메시지 생성 완료: {}", message.getId());
  }

  private static void createMessageWithAttachment(MessageService messageService,
      BinaryContentService binaryContentService, Channel channel,
      User author) {
    BinaryContent attachment = binaryContentService.create(
        new BinaryContentCreateRequest(new byte[]{}, "image.jpg", "image/jpeg",
            ContentType.MESSAGE_ATTACHMENT, author.getId(), null));
    MessageCreateRequest request = new MessageCreateRequest("안녕하세요, 이미지 첨부.", author.getId(),
        channel.getId(), Optional.of(Set.of(attachment.getId())));
    Message message = messageService.create(request);
    log.info("메시지와 첨부파일 생성 완료: {}", message.getId());
  }

  private static void testReadStatus(ReadStatusService readStatusService, Channel channel,
      User user) {
    // 생성 시 바로 초기화되며 필요 시 업데이트
    ReadStatus readStatus = readStatusService.create(
        new ReadStatusCreateRequest(user.getId(), channel.getId()));
    log.info("읽기 상태 확인: 사용자 {}, 채널 {}, 마지막 읽은 시간 {}", user.getId(), channel.getId(),
        readStatus.getLastReadAt());
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
