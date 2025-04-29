package com.sprint.mission.discodeit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.sprint.mission.discodeit.dto.data.UserResponse;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.factory.RepositoryBundle;
import com.sprint.mission.discodeit.repository.factory.RepositoryFactory;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.basic.BasicChannelService;
import com.sprint.mission.discodeit.service.basic.BasicMessageService;
import com.sprint.mission.discodeit.service.basic.BasicUserService;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class DisCodeItApplicationTest {

  private static final String DATA_DIR = "DATA";

  @BeforeEach
  void setUp() throws IOException {
    cleanUpFiles();
  }


  @AfterEach
  void tearDown() throws IOException {
    cleanUpFiles();
  }

  void cleanUpFiles() throws IOException {
    Path dataDirPath = Paths.get(System.getProperty("user.dir"), DATA_DIR);
    if (Files.exists(dataDirPath)) {
      deleteDirectoryRecursively(dataDirPath.toFile());
    }
  }

  private static void deleteDirectoryRecursively(File directoryToBeDeleted) throws IOException {
    File[] allContents = directoryToBeDeleted.listFiles();
    if (allContents != null) {
      for (File file : allContents) {
        deleteDirectoryRecursively(file);
      }
    }
    Files.deleteIfExists(directoryToBeDeleted.toPath());
  }

  // JCF 기반 테스트
  @Test
  void jcfRepositories_shouldWorkTogether() {
    RepositoryBundle bundle = RepositoryFactory.createJCFRepositories();
    UserService userService = new BasicUserService(
        bundle.getUserRepository(),
        bundle.getUserStatusRepository(),
        bundle.getBinaryContentRepository()
    );
    ChannelService channelService = new BasicChannelService(bundle.getChannelRepository());
    MessageService messageService = new BasicMessageService(
        bundle.getMessageRepository(),
        bundle.getUserRepository(),
        bundle.getChannelRepository()
    );

    runIntegrationScenario(userService, channelService, messageService);
  }

  // File 기반 테스트
  @Test
  void fileRepositories_shouldWorkTogether() {
    RepositoryBundle bundle = RepositoryFactory.createFileRepositories();
    UserService userService = new BasicUserService(
        bundle.getUserRepository(),
        bundle.getUserStatusRepository(),
        bundle.getBinaryContentRepository()
    );
    ChannelService channelService = new BasicChannelService(bundle.getChannelRepository());
    MessageService messageService = new BasicMessageService(
        bundle.getMessageRepository(),
        bundle.getUserRepository(),
        bundle.getChannelRepository()
    );

    runIntegrationScenario(userService, channelService, messageService);
  }

  private void runIntegrationScenario(UserService userService, ChannelService channelService,
      MessageService messageService) {
    // 1. 사용자 생성
    User user = userService.createUser("test@test.com", "길동쓰", "pwd1234");
    assertNotNull(user.getId(), "사용자 ID가 생성되어야 합니다.");

    // 2. 채널 생성
    Channel channel = channelService.createChannel(user, "공지", "공지 채널쓰");
    assertNotNull(channel.getId(), "채널 ID가 생성되어야 합니다.");
    assertTrue(channel.getParticipants().contains(user), "채널 생성자가 참여해야 합니다.");

    // 3. 메시지 생성
    Message message = messageService.createMessage("안녕하세요.", user.getId(), channel.getId());
    assertNotNull(message.getId(), "메시지 ID가 생성되어야 합니다.");
    assertEquals("안녕하세요.", message.getContent(), "메시지 내용이 일치해야 합니다.");
    assertEquals(message.getUserId(), user.getId(), "메시지 작성자가 일치해야 합니다.");
    assertEquals(message.getChannelId(), channel.getId(), "메시지 채널이 일치해야 합니다.");

    // 4. 생성된 사용자 확인
    List<UserResponse> allUsers = userService.getAllUsers();
    assertTrue(allUsers.stream().anyMatch(u -> u.id().equals(user.getId())),
        "생성된 사용자가 목록에 있어야 합니다.");

    // 5. 생성된 채널 확인
    List<Channel> allChannels = channelService.searchChannels(null, null);
    assertTrue(allChannels.stream().anyMatch(c -> c.getId().equals(channel.getId())),
        "생성된 채널이 목록에 있어야 합니다.");

    // 6. 생성된 메시지 확인
    List<Message> allMessages = messageService.searchMessages(null, null, null);
    assertTrue(allMessages.stream().anyMatch(m -> m.getId().equals(message.getId())),
        "생성된 메시지가 목록에 있어야 합니다.");
  }
}
