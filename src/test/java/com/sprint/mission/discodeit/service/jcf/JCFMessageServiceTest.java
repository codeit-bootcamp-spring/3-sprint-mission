package com.sprint.mission.discodeit.service.jcf;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.jcf.JCFChannelRepository;
import com.sprint.mission.discodeit.repository.jcf.JCFMessageRepository;
import com.sprint.mission.discodeit.repository.jcf.JCFUserRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.UserService;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class JCFMessageServiceTest {

  private UserService userService;
  private ChannelService channelService;
  private JCFMessageService messageService;
  private User user;
  private Channel channel;

  @BeforeEach
  void setUp() {
    userService = new JCFUserService(new JCFUserRepository());
    channelService = new JCFChannelService(new JCFChannelRepository());
    messageService = new JCFMessageService(new JCFMessageRepository(), userService, channelService);
    user = userService.createUser("user1@example.com", "홍길동", "password1");
    channel = channelService.createChannel(user, "개발자 모임");
  }

  @Test
  void createMessage() {
    // given
    String content = "새로운 메시지 내용";
    UUID userId = user.getId();
    UUID channelId = channel.getId();

    // when
    Message message = messageService.createMessage(content, userId, channelId);

    // then
    assertNotNull(message);
    assertEquals(content, message.getContent());
    assertEquals(userId, message.getUserId());
    assertEquals(channelId, message.getChannelId());
  }

  @Test
  @DisplayName("Message 생성 실패 테스트 - 채널 참여자가 아님")
  void createMessage_shouldFail() {
    //given
    User nonParticipantUser = userService.createUser("nonparticipant@example.com", "임꺽정",
        "password2");

    //when
    Message message = messageService.createMessage("테스트 메시지", nonParticipantUser.getId(),
        channel.getId());

    //then
    assertNull(message);
  }

  @Test
  @DisplayName("Message 생성 실패 테스트 - 유저를 찾을 수 없음")
  void createMessage_shouldFailIfUserNotFound() {
    //given
    UUID nonExistentUserId = UUID.randomUUID();

    //when
    Message message = messageService.createMessage("테스트 메시지", nonExistentUserId,
        channel.getId());

    //then
    assertNull(message);
  }

  @Test
  @DisplayName("Message 생성 실패 테스트 - 채널을 찾을 수 없음")
  void createMessage_shouldFailIfChannelNotFound() {
    //given
    UUID nonExistentChannelId = UUID.randomUUID();

    //when
    Message message = messageService.createMessage("테스트 메시지", user.getId(),
        nonExistentChannelId);

    //then
    assertNull(message);
  }

  @Test
  void getMessageById() {
  }

  @Test
  void searchMessages() {
  }

  @Test
  void getChannelMessages() {
  }

  @Test
  void updateMessageContent() {
  }

  @Test
  void deleteMessage() {
  }
}