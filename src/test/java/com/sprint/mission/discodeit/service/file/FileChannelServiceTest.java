package com.sprint.mission.discodeit.service.file;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;
import java.io.File;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class FileChannelServiceTest {

  private FileChannelService fileChannelService;
  private final String FILE_PATH = "data/test/channels.ser";
  private User creator;

  @BeforeEach
  void setUp() {
    // 정적 팩토리 메서드로 테스트용 파일 경로를 가진 FileUserService 인스턴스 생성
    fileChannelService = FileChannelService.from(FILE_PATH);
    creator = User.create("test@example.com", "테스트 사용자", "password");
  }

  @AfterEach
  void tearDown() {
    // 테스트 후 파일 삭제
    File file = new File(FILE_PATH);
    if (file.exists()) {
      file.delete();
    }
  }

  @Test
  void from() {
  }

  @Test
  void createDefault() {
  }

  @Test
  void createChannel() {
    // given
    String channelName = "테스트 채널";

    // when
    Channel createdChannel = fileChannelService.createChannel(creator, channelName);

    // then
    assertNotNull(createdChannel);
    assertEquals(channelName, createdChannel.getName());
    assertEquals(creator.getId(), createdChannel.getCreator().getId());

    // 파일에 저장되었는지 확인
    List<Channel> channels = fileChannelService.searchChannels(null, null); // 모든 채널 검색

    channels.forEach(System.out::println);

    boolean channelFound = channels.stream()
        .anyMatch(c -> c.getId().equals(createdChannel.getId()));
    assertTrue(channelFound);
  }

  @Test
  void getChannelById() {
  }

  @Test
  void searchChannels() {
  }

  @Test
  void getUserChannels() {
  }

  @Test
  void updateChannelName() {
  }

  @Test
  void addParticipant() {
  }

  @Test
  void removeParticipant() {
  }

  @Test
  void deleteChannel() {
  }
}