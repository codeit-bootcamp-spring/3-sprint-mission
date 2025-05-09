package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.LoginRequest;
import com.sprint.mission.discodeit.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.request.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserUpdateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
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
import java.util.UUID;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class TestInitializer {

  public static void initializeAndTest(
      UserService userService,
      ChannelService channelService,
      MessageService messageService,
      UserStatusService userStatusService,
      AuthService authService,
      BinaryContentService binaryContentService,
      ReadStatusService readStatusService
  ) {
    log.info("=== 테스트 시작 ===");

    User user1 = setupUser(userService, "user1@test.com", "길동쓰1", "pwd1234");
    User user2 = setupUser(userService, "user2@test.com", "길동쓰2", "pwd5678");

    loginUser(authService, userStatusService, user1);
    loginUser(authService, userStatusService, user2);

    addProfileImageToUser(userService, user1);
    addProfileImageToUser(userService, user2);

    Channel publicChannel = createPublicChannel(channelService, user1.getId());
    Channel privateChannel = createPrivateChannel(channelService,
        List.of(user1.getId(), user2.getId()));

    createMessage(messageService, publicChannel, user1);
    createMessageWithAttachment(messageService, privateChannel, user2);

    log.info("=== 테스트 완료 ===");
  }

  private static User setupUser(UserService userService, String email, String name,
      String password) {
    return userService.create(email, name, password);
  }

  private static void loginUser(AuthService authService, UserStatusService userStatusService,
      User user) {
    authService.login(new LoginRequest(user.getName(), user.getPassword()));
    UserStatus userStatus = userStatusService.updateByUserId(user.getId());
    log.info("로그인 완료: {}, 온라인 상태: {}", user.getName(), userStatus.isOnline());
  }

  private static void addProfileImageToUser(UserService userService, User user) {
    try {
      userService.update(new UserUpdateRequest(user.getId(), user.getName(), user.getPassword(),
          UUID.randomUUID()));
      log.info("프로필 이미지 추가 완료: {}", user.getId());
    } catch (Exception e) {
      log.warn("프로필 이미지 추가 실패: {}", user.getId(), e);
    }
  }

  private static Channel createPublicChannel(ChannelService channelService, UUID creatorId) {
    Channel channel = channelService.createPublic(
        new PublicChannelCreateRequest("공지", "공지사항"));
    log.info("Public 채널 생성 완료: {}", channel.getId());
    return channel;
  }

  private static Channel createPrivateChannel(ChannelService channelService,
      List<UUID> participantIds) {
    Channel channel = channelService.createPrivate(new PrivateChannelCreateRequest(participantIds));
    log.info("Private 채널 생성 완료: {}", channel.getId());
    return channel;
  }

  private static void createMessage(MessageService messageService, Channel channel, User author) {
    Message message = messageService.create(
        new MessageCreateRequest("안녕하세요.", author.getId(), channel.getId()), List.of());
    log.info("메시지 생성 완료: {}", message.getId());
  }

  private static void createMessageWithAttachment(MessageService messageService, Channel channel,
      User author) {
    var binaryRequest1 = new BinaryContentCreateRequest(
        "file1.jpg", "image/jpeg", new byte[]{1, 2, 3, 4});
    Message message = messageService.create(new MessageCreateRequest(
            "첨부파일 포함 메시지",
            author.getId(),
            channel.getId()
        ), List.of(binaryRequest1)
    );
    log.info("첨부파일 포함 메시지 생성 완료: {}", message.getId());
  }
}
