package com.sprint.mission.discodeit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.sprint.mission.discodeit.dto.response.ChannelResponse;
import com.sprint.mission.discodeit.dto.response.UserResponse;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.command.CreateMessageCommand;
import com.sprint.mission.discodeit.service.command.CreateUserCommand;
import java.nio.file.Path;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

@SpringBootTest
public class DiscodeitApplicationTest {

  private static final Logger log = LogManager.getLogger(DiscodeitApplicationTest.class);

  @TempDir
  static Path tempDir;

  @Autowired
  private UserService userService;

  @Autowired
  private ChannelService channelService;

  @Autowired
  private MessageService messageService;

  @DynamicPropertySource
  static void overrideProperties(DynamicPropertyRegistry registry) {
    registry.add("discodeit.repository.file-directory.folder",
        () -> tempDir.toAbsolutePath().toString());
  }

  @Test
  void 통합_테스트는_요구사항에_맞게_성공해야_한다() {
    log.info("임시 파일 경로: " + tempDir.toAbsolutePath());

    // 1. 사용자 생성
    UserResponse user = userService.create(
        new CreateUserCommand("test@test.com", "길동쓰", "pwd123", null));
    assertNotNull(user.id(), "사용자 ID 생성 확인");

    // 2. Public 채널 생성
    ChannelResponse channel = channelService.create("공지", "공지사항");
    assertNotNull(channel.id(), "채널 ID 생성 확인");

    // 3. Public 채널 조회
    ChannelResponse channelResponse = channelService.findById(channel.id());

    assertEquals(ChannelType.PUBLIC, channelResponse.type(), "Public 채널 타입 확인");

    // 4. 메시지 생성 (첨부파일 없이)
    CreateMessageCommand createMessageCommand = new CreateMessageCommand("안녕하세요.", user.id(),
        channel.id(), List.of());
    Message message = messageService.create(createMessageCommand);
    assertNotNull(message.getId(), "메시지 ID 생성 확인");
    assertEquals("안녕하세요.", message.getContent(), "메시지 내용 확인");
    assertEquals(user.id(), message.getAuthorId(), "메시지 작성자 확인");
    assertEquals(channel.id(), message.getChannelId(), "메시지 채널 확인");

    // 5. 사용자 목록에 존재 확인
    List<UserResponse> users = userService.findAll();
    assertTrue(users.stream().anyMatch(u -> u.id().equals(user.id())), "사용자 목록 포함 여부 확인");

    // 6. 채널 목록에 생성된 채널 존재 확인
    List<ChannelResponse> channels = channelService.findAllByUserId(user.id());
    assertTrue(channels.stream().anyMatch(c -> c.id().equals(channel.id())), "채널 목록 포함 여부 확인");

    // 7. 메시지 목록에 생성된 메시지 존재 확인
    List<Message> messages = messageService.findAllByChannelId(channel.id());
    assertTrue(messages.stream().anyMatch(m -> m.getId().equals(message.getId())),
        "메시지 목록 포함 여부 확인");
  }
}
