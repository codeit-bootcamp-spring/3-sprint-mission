package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.testutil.TestDataBuilder;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.test.context.ActiveProfiles;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;

@DataJpaTest
@EnableJpaAuditing
@ActiveProfiles("test")
@DisplayName("ChannelRepository 슬라이스 테스트")
class ChannelRepositoryTest {

  @Autowired
  private ChannelRepository channelRepository;

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private ReadStatusRepository readStatusRepository;

  @Autowired
  private TestEntityManager testEntityManager;

  @Autowired
  private EntityManager entityManager;

  // === TestFixture 메서드들 ===

  private User createAndSaveTestUser(String username, String email) {
    User user = TestDataBuilder.createUser(username, email);
    return userRepository.save(user);
  }

  private Channel createAndSaveTestChannel(ChannelType type, String name, String description) {
    Channel channel = new Channel(type, name, description);
    return channelRepository.save(channel);
  }

  private ReadStatus createAndSaveTestReadStatus(User user, Channel channel) {
    ReadStatus readStatus = new ReadStatus(user, channel, Instant.now());
    return readStatusRepository.save(readStatus);
  }

  private void clearPersistenceContext() {
    entityManager.flush();
    entityManager.clear();
  }

  // === 커스텀 쿼리 메서드 테스트 ===

  @Test
  @DisplayName("findByIdWithParticipants_존재하는채널ID_채널과참가자정보반환")
  void findByIdWithParticipants_ExistingChannelId_ReturnsChannelWithParticipants() {
    // given
    User user1 = createAndSaveTestUser("user1", "user1@example.com");
    User user2 = createAndSaveTestUser("user2", "user2@example.com");
    Channel channel = createAndSaveTestChannel(ChannelType.PUBLIC, "Test Channel", "Test Description");
    createAndSaveTestReadStatus(user1, channel);
    createAndSaveTestReadStatus(user2, channel);
    clearPersistenceContext();

    // when
    Optional<Channel> result = channelRepository.findByIdWithParticipants(channel.getId());

    // then
    assertThat(result).isPresent();
    Channel foundChannel = result.get();
    assertThat(foundChannel.getName()).isEqualTo("Test Channel");
    assertThat(foundChannel.getDescription()).isEqualTo("Test Description");
    assertThat(foundChannel.getType()).isEqualTo(ChannelType.PUBLIC);
    assertThat(foundChannel.getReadStatuses()).hasSize(2);
  }

  @Test
  @DisplayName("findByIdWithParticipants_존재하지않는채널ID_빈Optional반환")
  void findByIdWithParticipants_NonExistingChannelId_ReturnsEmpty() {
    // given
    UUID nonExistingChannelId = UUID.randomUUID();
    createAndSaveTestChannel(ChannelType.PUBLIC, "Test Channel", "Test Description");
    clearPersistenceContext();

    // when
    Optional<Channel> result = channelRepository.findByIdWithParticipants(nonExistingChannelId);

    // then
    assertThat(result).isEmpty();
  }

  @Test
  @DisplayName("findAllWithParticipants_모든채널조회_채널목록과참가자정보반환")
  void findAllWithParticipants_FindAllChannels_ReturnsAllChannelsWithParticipants() {
    // given
    User user = createAndSaveTestUser("user1", "user1@example.com");
    Channel publicChannel = createAndSaveTestChannel(ChannelType.PUBLIC, "Public Channel", "Public Description");
    Channel privateChannel = createAndSaveTestChannel(ChannelType.PRIVATE, null, null);
    createAndSaveTestReadStatus(user, publicChannel);
    createAndSaveTestReadStatus(user, privateChannel);
    clearPersistenceContext();

    // when
    List<Channel> result = channelRepository.findAllWithParticipants();

    // then
    assertThat(result).hasSize(2);
    assertThat(result.stream().anyMatch(c -> c.getType() == ChannelType.PUBLIC)).isTrue();
    assertThat(result.stream().anyMatch(c -> c.getType() == ChannelType.PRIVATE)).isTrue();
  }

  @Test
  @DisplayName("findAllByIdInWithParticipants_특정채널ID목록_해당채널들과참가자정보반환")
  void findAllByIdInWithParticipants_SpecificChannelIds_ReturnsChannelsWithParticipants() {
    // given
    User user = createAndSaveTestUser("user1", "user1@example.com");
    Channel channel1 = createAndSaveTestChannel(ChannelType.PUBLIC, "Channel 1", "Description 1");
    Channel channel2 = createAndSaveTestChannel(ChannelType.PUBLIC, "Channel 2", "Description 2");
    Channel channel3 = createAndSaveTestChannel(ChannelType.PUBLIC, "Channel 3", "Description 3");
    createAndSaveTestReadStatus(user, channel1);
    createAndSaveTestReadStatus(user, channel2);
    createAndSaveTestReadStatus(user, channel3);
    clearPersistenceContext();

    List<UUID> channelIds = Arrays.asList(channel1.getId(), channel2.getId());

    // when
    List<Channel> result = channelRepository.findAllByIdInWithParticipants(channelIds);

    // then
    assertThat(result).hasSize(2);
    assertThat(result.stream().map(Channel::getName)).containsExactlyInAnyOrder("Channel 1", "Channel 2");
  }

  @Test
  @DisplayName("findAllWithParticipantsOnly_모든채널조회_채널과참가자정보만반환")
  void findAllWithParticipantsOnly_FindAllChannels_ReturnsChannelsWithParticipantsOnly() {
    // given
    User user = createAndSaveTestUser("user1", "user1@example.com");
    Channel channel = createAndSaveTestChannel(ChannelType.PUBLIC, "Test Channel", "Test Description");
    createAndSaveTestReadStatus(user, channel);
    clearPersistenceContext();

    // when
    List<Channel> result = channelRepository.findAllWithParticipantsOnly();

    // then
    assertThat(result).hasSize(1);
    assertThat(result.get(0).getName()).isEqualTo("Test Channel");
    assertThat(result.get(0).getReadStatuses()).hasSize(1);
  }

  @Test
  @DisplayName("findAccessibleChannelsWithParticipants_구독채널ID목록_접근가능채널반환")
  void findAccessibleChannelsWithParticipants_SubscribedChannelIds_ReturnsAccessibleChannels() {
    // given
    User user = createAndSaveTestUser("user1", "user1@example.com");
    Channel publicChannel = createAndSaveTestChannel(ChannelType.PUBLIC, "Public Channel", "Public Description");
    Channel privateChannel1 = createAndSaveTestChannel(ChannelType.PRIVATE, null, null);
    Channel privateChannel2 = createAndSaveTestChannel(ChannelType.PRIVATE, null, null);
    createAndSaveTestReadStatus(user, publicChannel);
    createAndSaveTestReadStatus(user, privateChannel1);
    createAndSaveTestReadStatus(user, privateChannel2);
    clearPersistenceContext();

    List<UUID> subscribedChannelIds = Arrays.asList(privateChannel1.getId());

    // when
    List<Channel> result = channelRepository.findAccessibleChannelsWithParticipants(subscribedChannelIds);

    // then
    assertThat(result).hasSize(2); // Public channel + subscribed private channel
    assertThat(result.stream().anyMatch(c -> c.getType() == ChannelType.PUBLIC)).isTrue();
    assertThat(result.stream().anyMatch(c -> c.getId().equals(privateChannel1.getId()))).isTrue();
    assertThat(result.stream().noneMatch(c -> c.getId().equals(privateChannel2.getId()))).isTrue();
  }

  @Test
  @DisplayName("findPublicChannelsWithParticipants_공개채널조회_공개채널목록반환")
  void findPublicChannelsWithParticipants_FindPublicChannels_ReturnsPublicChannels() {
    // given
    User user = createAndSaveTestUser("user1", "user1@example.com");
    Channel publicChannel1 = createAndSaveTestChannel(ChannelType.PUBLIC, "Public Channel 1", "Description 1");
    Channel publicChannel2 = createAndSaveTestChannel(ChannelType.PUBLIC, "Public Channel 2", "Description 2");
    Channel privateChannel = createAndSaveTestChannel(ChannelType.PRIVATE, null, null);
    createAndSaveTestReadStatus(user, publicChannel1);
    createAndSaveTestReadStatus(user, publicChannel2);
    createAndSaveTestReadStatus(user, privateChannel);
    clearPersistenceContext();

    // when
    List<Channel> result = channelRepository.findPublicChannelsWithParticipants();

    // then
    assertThat(result).hasSize(2);
    assertThat(result.stream().allMatch(c -> c.getType() == ChannelType.PUBLIC)).isTrue();
    assertThat(result.stream().map(Channel::getName))
        .containsExactlyInAnyOrder("Public Channel 1", "Public Channel 2");
  }

  // === JpaRepository 기본 메서드 테스트 ===

  @Test
  @DisplayName("save_유효한채널정보_채널저장성공")
  void save_ValidChannelData_SavesSuccessfully() {
    // given
    Channel channel = TestDataBuilder.createPublicChannel();

    // when
    Channel savedChannel = channelRepository.save(channel);
    clearPersistenceContext();

    // then
    assertThat(savedChannel.getId()).isNotNull();
    assertThat(savedChannel.getCreatedAt()).isNotNull();
    assertThat(savedChannel.getUpdatedAt()).isNotNull();
    assertThat(savedChannel.getType()).isEqualTo(ChannelType.PUBLIC);
    assertThat(savedChannel.getName()).isEqualTo("Test Channel");
    assertThat(savedChannel.getDescription()).isEqualTo("Test Description");
  }

  @Test
  @DisplayName("findById_존재하는ID로조회_채널반환")
  void findById_ExistingId_ReturnsChannel() {
    // given
    Channel savedChannel = createAndSaveTestChannel(ChannelType.PUBLIC, "Test Channel", "Test Description");
    clearPersistenceContext();

    // when
    Optional<Channel> foundChannel = channelRepository.findById(savedChannel.getId());

    // then
    assertThat(foundChannel).isPresent();
    assertThat(foundChannel.get().getName()).isEqualTo("Test Channel");
    assertThat(foundChannel.get().getDescription()).isEqualTo("Test Description");
    assertThat(foundChannel.get().getType()).isEqualTo(ChannelType.PUBLIC);
  }

  @Test
  @DisplayName("deleteById_존재하는ID로삭제_삭제성공")
  void deleteById_ExistingId_DeletesSuccessfully() {
    // given
    Channel savedChannel = createAndSaveTestChannel(ChannelType.PUBLIC, "Test Channel", "Test Description");
    clearPersistenceContext();

    // 삭제 전 존재 확인
    Optional<Channel> beforeDelete = channelRepository.findById(savedChannel.getId());
    assertThat(beforeDelete).isPresent();

    // when & then - 예외 없이 실행되어야 함
    assertThatNoException().isThrownBy(() -> {
      channelRepository.deleteById(savedChannel.getId());
    });
  }

  // === 페이징 및 정렬 테스트 ===

  @Test
  @DisplayName("findAll_페이징처리_페이지네이션결과반환")
  void findAll_WithPaging_ReturnsPagedResults() {
    // given
    createAndSaveTestChannel(ChannelType.PUBLIC, "Channel 1", "Description 1");
    createAndSaveTestChannel(ChannelType.PUBLIC, "Channel 2", "Description 2");
    createAndSaveTestChannel(ChannelType.PRIVATE, null, null);
    clearPersistenceContext();

    Pageable pageable = PageRequest.of(0, 2);

    // when
    Page<Channel> channelPage = channelRepository.findAll(pageable);

    // then
    assertThat(channelPage.getContent()).hasSize(2);
    assertThat(channelPage.getTotalElements()).isEqualTo(3);
    assertThat(channelPage.getTotalPages()).isEqualTo(2);
    assertThat(channelPage.isFirst()).isTrue();
    assertThat(channelPage.hasNext()).isTrue();
  }

  @Test
  @DisplayName("findAll_채널타입기준정렬_정렬된결과반환")
  void findAll_WithSorting_ReturnsSortedResults() {
    // given
    createAndSaveTestChannel(ChannelType.PRIVATE, null, null);
    createAndSaveTestChannel(ChannelType.PUBLIC, "Public Channel", "Public Description");
    createAndSaveTestChannel(ChannelType.PRIVATE, null, null);
    clearPersistenceContext();

    Sort sort = Sort.by(Sort.Direction.ASC, "type");

    // when
    List<Channel> sortedChannels = channelRepository.findAll(sort);

    // then
    assertThat(sortedChannels).hasSize(3);
    // PRIVATE가 PUBLIC보다 앞에 정렬되어야 함 (알파벳 순)
    assertThat(sortedChannels.get(0).getType()).isEqualTo(ChannelType.PRIVATE);
    assertThat(sortedChannels.get(1).getType()).isEqualTo(ChannelType.PRIVATE);
    assertThat(sortedChannels.get(2).getType()).isEqualTo(ChannelType.PUBLIC);
  }

  @Test
  @DisplayName("findAll_페이징과정렬조합_페이지네이션된정렬결과반환")
  void findAll_WithPagingAndSorting_ReturnsPagedAndSortedResults() {
    // given
    createAndSaveTestChannel(ChannelType.PRIVATE, null, null);
    createAndSaveTestChannel(ChannelType.PUBLIC, "Public Channel 1", "Description 1");
    createAndSaveTestChannel(ChannelType.PUBLIC, "Public Channel 2", "Description 2");
    createAndSaveTestChannel(ChannelType.PRIVATE, null, null);
    clearPersistenceContext();

    Pageable pageable = PageRequest.of(0, 2, Sort.by(Sort.Direction.ASC, "type"));

    // when
    Page<Channel> channelPage = channelRepository.findAll(pageable);

    // then
    assertThat(channelPage.getContent()).hasSize(2);
    assertThat(channelPage.getContent().get(0).getType()).isEqualTo(ChannelType.PRIVATE);
    assertThat(channelPage.getContent().get(1).getType()).isEqualTo(ChannelType.PRIVATE);
    assertThat(channelPage.getTotalElements()).isEqualTo(4);
    assertThat(channelPage.isFirst()).isTrue();
    assertThat(channelPage.hasNext()).isTrue();
  }

  // === 엔티티 업데이트 테스트 ===

  @Test
  @DisplayName("update_채널정보변경_업데이트성공")
  void update_ChannelInfoChange_UpdatesSuccessfully() {
    // given
    Channel channel = createAndSaveTestChannel(ChannelType.PUBLIC, "Original Name", "Original Description");
    clearPersistenceContext();

    // when
    Channel foundChannel = channelRepository.findById(channel.getId()).orElseThrow();
    foundChannel.update("Updated Name", "Updated Description");
    channelRepository.saveAndFlush(foundChannel);
    clearPersistenceContext();

    // then
    Channel updatedChannel = channelRepository.findById(channel.getId()).orElseThrow();
    assertThat(updatedChannel.getName()).isEqualTo("Updated Name");
    assertThat(updatedChannel.getDescription()).isEqualTo("Updated Description");
    assertThat(updatedChannel.getUpdatedAt()).isNotNull();
    assertThat(updatedChannel.getUpdatedAt()).isAfter(updatedChannel.getCreatedAt());
  }

  // === 채널 타입별 테스트 ===

  @Test
  @DisplayName("channelType_공개채널생성_PUBLIC타입확인")
  void channelType_CreatePublicChannel_ReturnsPublicType() {
    // given & when
    Channel publicChannel = createAndSaveTestChannel(ChannelType.PUBLIC, "Public Channel", "Public Description");
    clearPersistenceContext();

    // then
    Channel foundChannel = channelRepository.findById(publicChannel.getId()).orElseThrow();
    assertThat(foundChannel.getType()).isEqualTo(ChannelType.PUBLIC);
    assertThat(foundChannel.getType().isPublic()).isTrue();
    assertThat(foundChannel.getType().isPrivate()).isFalse();
  }

  @Test
  @DisplayName("channelType_비공개채널생성_PRIVATE타입확인")
  void channelType_CreatePrivateChannel_ReturnsPrivateType() {
    // given & when
    Channel privateChannel = createAndSaveTestChannel(ChannelType.PRIVATE, null, null);
    clearPersistenceContext();

    // then
    Channel foundChannel = channelRepository.findById(privateChannel.getId()).orElseThrow();
    assertThat(foundChannel.getType()).isEqualTo(ChannelType.PRIVATE);
    assertThat(foundChannel.getType().isPrivate()).isTrue();
    assertThat(foundChannel.getType().isPublic()).isFalse();
  }
}