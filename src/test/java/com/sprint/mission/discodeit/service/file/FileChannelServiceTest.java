package com.sprint.mission.discodeit.service.file;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;
import java.io.File;
import java.util.List;
import java.util.Optional;
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
    // when
    FileChannelService service = FileChannelService.from(FILE_PATH);

    // then
    assertNotNull(service);
  }

  @Test
  void createDefault() {
    // when
    FileChannelService service = FileChannelService.createDefault();

    // then
    assertNotNull(service);
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
    // given
    String channelName = "채널 ID 테스트";
    Channel createdChannel = fileChannelService.createChannel(creator, channelName);

    // when
    Optional<Channel> foundChannel = fileChannelService.getChannelById(createdChannel.getId());

    // then
    assertTrue(foundChannel.isPresent());
    assertEquals(createdChannel.getId(), foundChannel.get().getId());
    assertEquals(channelName, foundChannel.get().getName());
    assertEquals(creator.getId(), foundChannel.get().getCreator().getId());
  }

  @Test
  void searchChannels() {
    // given
    String channelName1 = "테스트 채널 1";
    String channelName2 = "다른 채널 2";
    User anotherUser = User.create("another@example.com", "다른 사용자", "password");

    Channel channel1 = fileChannelService.createChannel(creator, channelName1);
    Channel channel2 = fileChannelService.createChannel(anotherUser, channelName2);

    // when - 모든 채널 검색
    List<Channel> allChannels = fileChannelService.searchChannels(null, null);

    // then
    assertEquals(2, allChannels.size());

    // when - 이름으로 검색
    List<Channel> channelsByName = fileChannelService.searchChannels(null, "테스트");

    // then
    assertEquals(1, channelsByName.size());
    assertEquals(channelName1, channelsByName.get(0).getName());

    // when - 생성자로 검색
    List<Channel> channelsByCreator = fileChannelService.searchChannels(creator.getId(), null);

    // then
    assertEquals(1, channelsByCreator.size());
    assertEquals(creator.getId(), channelsByCreator.get(0).getCreator().getId());

    // when - 생성자와 이름으로 검색
    List<Channel> channelsByCreatorAndName = fileChannelService.searchChannels(anotherUser.getId(), "다른");

    // then
    assertEquals(1, channelsByCreatorAndName.size());
    assertEquals(anotherUser.getId(), channelsByCreatorAndName.get(0).getCreator().getId());
    assertEquals(channelName2, channelsByCreatorAndName.get(0).getName());
  }

  @Test
  void getUserChannels() {
    // given
    String channelName1 = "사용자 채널 1";
    String channelName2 = "사용자 채널 2";
    User user1 = User.create("user1@example.com", "사용자1", "password");
    User user2 = User.create("user2@example.com", "사용자2", "password");

    // 채널 생성 (생성자는 자동으로 참여자가 됨)
    Channel channel1 = fileChannelService.createChannel(user1, channelName1);
    Channel channel2 = fileChannelService.createChannel(user2, channelName2);

    // user2를 channel1에 참여시킴
    fileChannelService.addParticipant(channel1.getId(), user2);

    // when - user1의 채널 조회
    List<Channel> user1Channels = fileChannelService.getUserChannels(user1.getId());

    // then
    assertEquals(1, user1Channels.size());
    assertEquals(channel1.getId(), user1Channels.get(0).getId());

    // when - user2의 채널 조회 (두 채널 모두 참여)
    List<Channel> user2Channels = fileChannelService.getUserChannels(user2.getId());

    // then
    assertEquals(2, user2Channels.size());
    assertTrue(user2Channels.stream().anyMatch(c -> c.getId().equals(channel1.getId())));
    assertTrue(user2Channels.stream().anyMatch(c -> c.getId().equals(channel2.getId())));
  }

  @Test
  void updateChannelName() {
    // given
    String originalName = "원래 채널명";
    String updatedName = "변경된 채널명";
    Channel channel = fileChannelService.createChannel(creator, originalName);

    // when
    Optional<Channel> updatedChannel = fileChannelService.updateChannelName(channel.getId(), updatedName);

    // then
    assertTrue(updatedChannel.isPresent());
    assertEquals(updatedName, updatedChannel.get().getName());

    // 파일에 저장되었는지 확인
    Optional<Channel> retrievedChannel = fileChannelService.getChannelById(channel.getId());
    assertTrue(retrievedChannel.isPresent());
    assertEquals(updatedName, retrievedChannel.get().getName());
  }

  @Test
  void addParticipant() {
    // given
    String channelName = "참여자 추가 테스트 채널";
    Channel channel = fileChannelService.createChannel(creator, channelName);
    User newParticipant = User.create("participant@example.com", "새 참여자", "password");

    // when
    fileChannelService.addParticipant(channel.getId(), newParticipant);

    // then
    Optional<Channel> retrievedChannel = fileChannelService.getChannelById(channel.getId());
    assertTrue(retrievedChannel.isPresent());

    List<User> participants = retrievedChannel.get().getParticipants();
    assertEquals(2, participants.size()); // 생성자 + 새 참여자

    boolean participantFound = participants.stream()
        .anyMatch(p -> p.getId().equals(newParticipant.getId()));
    assertTrue(participantFound);
  }

  @Test
  void removeParticipant() {
    // given
    String channelName = "참여자 제거 테스트 채널";
    Channel channel = fileChannelService.createChannel(creator, channelName);
    User participant = User.create("participant@example.com", "참여자", "password");

    // 참여자 추가
    fileChannelService.addParticipant(channel.getId(), participant);

    // 참여자가 추가되었는지 확인
    Optional<Channel> channelWithParticipant = fileChannelService.getChannelById(channel.getId());
    assertTrue(channelWithParticipant.isPresent());
    assertEquals(2, channelWithParticipant.get().getParticipants().size());

    // when
    fileChannelService.removeParticipant(channel.getId(), participant.getId());

    // then
    Optional<Channel> retrievedChannel = fileChannelService.getChannelById(channel.getId());
    assertTrue(retrievedChannel.isPresent());

    List<User> participants = retrievedChannel.get().getParticipants();
    assertEquals(1, participants.size()); // 생성자만 남음

    boolean participantRemoved = participants.stream()
        .noneMatch(p -> p.getId().equals(participant.getId()));
    assertTrue(participantRemoved);
  }

  @Test
  void deleteChannel() {
    // given
    String channelName = "삭제할 채널";
    Channel channel = fileChannelService.createChannel(creator, channelName);

    // 채널이 생성되었는지 확인
    Optional<Channel> createdChannel = fileChannelService.getChannelById(channel.getId());
    assertTrue(createdChannel.isPresent());

    // when
    Optional<Channel> deletedChannel = fileChannelService.deleteChannel(channel.getId());

    // then
    assertTrue(deletedChannel.isPresent());
    assertEquals(channel.getId(), deletedChannel.get().getId());

    // 실제로 삭제되었는지 확인
    Optional<Channel> retrievedChannel = fileChannelService.getChannelById(channel.getId());
    assertTrue(retrievedChannel.isEmpty());

    // 모든 채널 목록에서도 삭제되었는지 확인
    List<Channel> allChannels = fileChannelService.searchChannels(null, null);
    boolean channelStillExists = allChannels.stream()
        .anyMatch(c -> c.getId().equals(channel.getId()));
    assertFalse(channelStillExists);
  }
}
