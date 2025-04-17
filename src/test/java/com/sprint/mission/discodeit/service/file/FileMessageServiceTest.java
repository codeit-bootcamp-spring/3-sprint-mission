package com.sprint.mission.discodeit.service.file;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;
import java.io.File;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class FileMessageServiceTest {

  private MessageService fileMessageService;
  private UserService userService;
  private ChannelService channelService;
  private User user;
  private Channel channel;

  private final String USER_FILE_PATH = "data/test/user.ser";
  private final String CHANNEL_FILE_PATH = "data/test/channel.ser";
  private final String MESSAGE_FILE_PATH = "data/test/messages.ser";

  @BeforeEach
  void setUp() {
    userService = FileUserService.from(USER_FILE_PATH);
    channelService = FileChannelService.from(CHANNEL_FILE_PATH);
    // 정적 팩토리 메서드로 테스트용 파일 경로를 가진 FileUserService 인스턴스 생성
    fileMessageService = FileMessageService.from(userService, channelService, MESSAGE_FILE_PATH);

    user = userService.createUser("test@example.com", "테스트 사용자", "password");
    channel = channelService.createChannel(user, "테스트 채널");
  }

  @AfterEach
  void tearDown() {
    // 테스트 후 파일 삭제
    File userFile = new File(USER_FILE_PATH);
    File channelFile = new File(CHANNEL_FILE_PATH);
    File messageFile = new File(MESSAGE_FILE_PATH);

    if (userFile.exists()) {
      userFile.delete();
    }
    if (channelFile.exists()) {
      channelFile.delete();
    }
    if (messageFile.exists()) {
      messageFile.delete();
    }
  }

  @Test
  void from() {
  }

  @Test
  void createDefault() {
  }

  @Test
  void createMessage() {
    // given
    String content = "테스트 메시지";

    // when
    Message createdMessage = fileMessageService.createMessage(content, user.getId(),
        channel.getId());

    // then
    assertNotNull(createdMessage);
    assertEquals(content, createdMessage.getContent());
    assertEquals(user.getId(), createdMessage.getUserId());
    assertEquals(channel.getId(), createdMessage.getChannelId());

    // 파일에 저장되었는지 확인
    List<Message> messages = fileMessageService.searchMessages(null, null, null);

    messages.forEach(System.out::println);

    boolean messageFound = messages.stream()
        .anyMatch(m -> m.getId().equals(createdMessage.getId()));
    assertTrue(messageFound);
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