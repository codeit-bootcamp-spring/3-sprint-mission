package com.sprint.mission.discodeit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.sprint.mission.discodeit.dto.ChannelResponse;
import com.sprint.mission.discodeit.dto.response.MessageResponse;
import com.sprint.mission.discodeit.dto.response.PrivateChannelResponse;
import com.sprint.mission.discodeit.dto.response.PublicChannelResponse;
import com.sprint.mission.discodeit.dto.response.UserResponse;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;
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
    registry.add("discodeit.repository.file-directory", () -> tempDir.toAbsolutePath().toString());
  }

  @Test
  void integrationTest_shouldWorkProperly() {
    log.info("임시 파일 경로: " + tempDir.toAbsolutePath());

    // 1. 사용자 생성
    User user = userService.create("test@test.com", "길동쓰", "pwd1234");
    assertNotNull(user.getId(), "사용자 ID가 생성되어야 합니다.");

    // 2. 채널 생성
    Channel channel = channelService.create(user.getId(), "공지", "공지 채널쓰");

    // 생성자를 참여자로 추가 (서비스 메서드로 처리)
    channelService.addParticipant(channel.getId(), user.getId());

    assertNotNull(channel.getId(), "채널 ID가 생성되어야 합니다.");

    ChannelResponse channelResponse = channelService.findById(channel.getId())
        .orElseThrow(() -> new IllegalStateException("채널을 찾을 수 없습니다."));

    if (channelResponse instanceof PrivateChannelResponse privateResp) {
      // Private 채널일 경우 참여자 정보 검증
      assertTrue(privateResp.participantIds().contains(user.getId()), "채널 생성자가 참여해야 합니다.");
    } else if (channelResponse instanceof PublicChannelResponse) {
      log.info("Public 채널에는 참여자 정보가 없습니다.");
    } else {
      throw new IllegalStateException("알 수 없는 ChannelResponse 타입: " + channelResponse.getClass());
    }

    // 3. 메시지 생성
    Message message = messageService.create("안녕하세요.", user.getId(), channel.getId());
    assertNotNull(message.getId(), "메시지 ID가 생성되어야 합니다.");
    assertEquals("안녕하세요.", message.getContent(), "메시지 내용이 일치해야 합니다.");
    assertEquals(user.getId(), message.getUserId(), "메시지 작성자가 일치해야 합니다.");
    assertEquals(channel.getId(), message.getChannelId(), "메시지 채널이 일치해야 합니다.");

    // 4. 생성된 사용자 확인
    List<UserResponse> allUsers = userService.findAll();
    assertTrue(allUsers.stream().anyMatch(u -> u.id().equals(user.getId())),
        "생성된 사용자가 목록에 있어야 합니다.");

    // 5. 생성된 채널 확인
    List<ChannelResponse> allChannels = channelService.findByCreatorIdOrName(null, null);
    assertTrue(allChannels.stream().anyMatch(c -> c.id().equals(channel.getId())),
        "생성된 채널이 목록에 있어야 합니다.");

    // 6. 생성된 메시지 확인
    List<MessageResponse> allMessages = messageService.searchMessages(null, null, null);
    assertTrue(allMessages.stream().anyMatch(m -> m.id().equals(message.getId())),
        "생성된 메시지가 목록에 있어야 합니다.");
  }
}
