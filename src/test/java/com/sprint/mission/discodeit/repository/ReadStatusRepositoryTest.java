package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.test.context.ActiveProfiles;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;

@DataJpaTest
@EnableJpaAuditing
@ActiveProfiles("test")
@DisplayName("ReadStatusRepository 슬라이스 테스트")
class ReadStatusRepositoryTest {

  @Autowired
  private ReadStatusRepository readStatusRepository;

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private ChannelRepository channelRepository;

  @Autowired
  private TestEntityManager testEntityManager;

  @Autowired
  private EntityManager entityManager;

  // === 테스트 데이터 생성 헬퍼 메서드들 ===

  private User createAndSaveTestUser(String username, String email) {
    User user = new User(username, email, "password123", null);
    return userRepository.save(user);
  }

  private Channel createAndSaveTestChannel(ChannelType type, String name, String description) {
    Channel channel = new Channel(type, name, description);
    return channelRepository.save(channel);
  }

  private ReadStatus createAndSaveTestReadStatus(User user, Channel channel, Instant lastReadAt) {
    ReadStatus readStatus = new ReadStatus(user, channel, lastReadAt);
    return readStatusRepository.save(readStatus);
  }

  private void clearPersistenceContext() {
    testEntityManager.flush();
    testEntityManager.clear();
  }

  // === 사용자 ID 기반 조회 테스트 ===

  @Test
  @DisplayName("findAllByUserId_존재하는사용자ID_읽기상태목록반환")
  void findAllByUserId_ExistingUserId_ReturnsReadStatuses() {
    // given
    User user = createAndSaveTestUser("testuser", "test@example.com");
    Channel channel1 = createAndSaveTestChannel(ChannelType.PUBLIC, "Channel 1", "Description 1");
    Channel channel2 = createAndSaveTestChannel(ChannelType.PUBLIC, "Channel 2", "Description 2");

    Instant now = Instant.now().truncatedTo(ChronoUnit.SECONDS);
    createAndSaveTestReadStatus(user, channel1, now);
    createAndSaveTestReadStatus(user, channel2, now.plusSeconds(60));
    clearPersistenceContext();

    // when
    List<ReadStatus> readStatuses = readStatusRepository.findAllByUserId(user.getId());

    // then
    assertThat(readStatuses).hasSize(2);
    assertThat(readStatuses.stream().allMatch(rs -> rs.getUser().getId().equals(user.getId()))).isTrue();
  }

  @Test
  @DisplayName("findAllByUserId_존재하지않는사용자ID_빈목록반환")
  void findAllByUserId_NonExistingUserId_ReturnsEmptyList() {
    // given
    UUID nonExistingUserId = UUID.randomUUID();

    // when
    List<ReadStatus> readStatuses = readStatusRepository.findAllByUserId(nonExistingUserId);

    // then
    assertThat(readStatuses).isEmpty();
  }

  @Test
  @DisplayName("findAllByUserIdWithUser_사용자정보포함조회_사용자정보포함읽기상태반환")
  void findAllByUserIdWithUser_WithUserInfo_ReturnsReadStatusesWithUser() {
    // given
    User user = createAndSaveTestUser("testuser", "test@example.com");
    Channel channel = createAndSaveTestChannel(ChannelType.PUBLIC, "Test Channel", "Description");

    Instant now = Instant.now().truncatedTo(ChronoUnit.SECONDS);
    createAndSaveTestReadStatus(user, channel, now);
    clearPersistenceContext();

    // when
    List<ReadStatus> readStatuses = readStatusRepository.findAllByUserIdWithUser(user.getId());

    // then
    assertThat(readStatuses).hasSize(1);
    assertThat(readStatuses.get(0).getUser().getUsername()).isEqualTo("testuser");
    assertThat(readStatuses.get(0).getUser().getEmail()).isEqualTo("test@example.com");
  }

  @Test
  @DisplayName("findAllByUserIdWithUserAndChannel_사용자와채널정보포함조회_모든연관정보포함읽기상태반환")
  void findAllByUserIdWithUserAndChannel_WithUserAndChannelInfo_ReturnsReadStatusesWithUserAndChannel() {
    // given
    User user = createAndSaveTestUser("testuser", "test@example.com");
    Channel channel = createAndSaveTestChannel(ChannelType.PUBLIC, "Test Channel", "Description");

    Instant now = Instant.now().truncatedTo(ChronoUnit.SECONDS);
    createAndSaveTestReadStatus(user, channel, now);
    clearPersistenceContext();

    // when
    List<ReadStatus> readStatuses = readStatusRepository.findAllByUserIdWithUserAndChannel(user.getId());

    // then
    assertThat(readStatuses).hasSize(1);
    ReadStatus readStatus = readStatuses.get(0);
    assertThat(readStatus.getUser().getUsername()).isEqualTo("testuser");
    assertThat(readStatus.getChannel().getName()).isEqualTo("Test Channel");
    assertThat(readStatus.getLastReadAt()).isEqualTo(now);
  }

  // === 채널 ID 기반 조회 테스트 ===

  @Test
  @DisplayName("findAllByChannelId_존재하는채널ID_읽기상태목록반환")
  void findAllByChannelId_ExistingChannelId_ReturnsReadStatuses() {
    // given
    User user1 = createAndSaveTestUser("user1", "user1@example.com");
    User user2 = createAndSaveTestUser("user2", "user2@example.com");
    Channel channel = createAndSaveTestChannel(ChannelType.PUBLIC, "Test Channel", "Description");

    Instant now = Instant.now().truncatedTo(ChronoUnit.SECONDS);
    createAndSaveTestReadStatus(user1, channel, now);
    createAndSaveTestReadStatus(user2, channel, now.plusSeconds(30));
    clearPersistenceContext();

    // when
    List<ReadStatus> readStatuses = readStatusRepository.findAllByChannelId(channel.getId());

    // then
    assertThat(readStatuses).hasSize(2);
    assertThat(readStatuses.stream().allMatch(rs -> rs.getChannel().getId().equals(channel.getId()))).isTrue();
  }

  @Test
  @DisplayName("findAllByChannelIdWithUser_채널ID와사용자정보포함조회_사용자정보포함읽기상태반환")
  void findAllByChannelIdWithUser_WithUserInfo_ReturnsReadStatusesWithUser() {
    // given
    User user1 = createAndSaveTestUser("user1", "user1@example.com");
    User user2 = createAndSaveTestUser("user2", "user2@example.com");
    Channel channel = createAndSaveTestChannel(ChannelType.PUBLIC, "Test Channel", "Description");

    Instant now = Instant.now().truncatedTo(ChronoUnit.SECONDS);
    createAndSaveTestReadStatus(user1, channel, now);
    createAndSaveTestReadStatus(user2, channel, now.plusSeconds(30));
    clearPersistenceContext();

    // when
    List<ReadStatus> readStatuses = readStatusRepository.findAllByChannelIdWithUser(channel.getId());

    // then
    assertThat(readStatuses).hasSize(2);
    assertThat(readStatuses.stream().map(rs -> rs.getUser().getUsername()))
        .containsExactlyInAnyOrder("user1", "user2");
  }

  // === 단일 조회 테스트 ===

  @Test
  @DisplayName("findByIdWithUserAndChannel_존재하는ID_연관정보포함읽기상태반환")
  void findByIdWithUserAndChannel_ExistingId_ReturnsReadStatusWithUserAndChannel() {
    // given
    User user = createAndSaveTestUser("testuser", "test@example.com");
    Channel channel = createAndSaveTestChannel(ChannelType.PUBLIC, "Test Channel", "Description");

    Instant now = Instant.now().truncatedTo(ChronoUnit.SECONDS);
    ReadStatus savedReadStatus = createAndSaveTestReadStatus(user, channel, now);
    clearPersistenceContext();

    // when
    Optional<ReadStatus> foundReadStatus = readStatusRepository.findByIdWithUserAndChannel(savedReadStatus.getId());

    // then
    assertThat(foundReadStatus).isPresent();
    ReadStatus readStatus = foundReadStatus.get();
    assertThat(readStatus.getUser().getUsername()).isEqualTo("testuser");
    assertThat(readStatus.getChannel().getName()).isEqualTo("Test Channel");
    assertThat(readStatus.getLastReadAt()).isEqualTo(now);
  }

  @Test
  @DisplayName("findByUserIdAndChannelIdWithUserAndChannel_존재하는사용자와채널_읽기상태반환")
  void findByUserIdAndChannelIdWithUserAndChannel_ExistingUserAndChannel_ReturnsReadStatus() {
    // given
    User user = createAndSaveTestUser("testuser", "test@example.com");
    Channel channel = createAndSaveTestChannel(ChannelType.PUBLIC, "Test Channel", "Description");

    Instant now = Instant.now().truncatedTo(ChronoUnit.SECONDS);
    createAndSaveTestReadStatus(user, channel, now);
    clearPersistenceContext();

    // when
    Optional<ReadStatus> foundReadStatus = readStatusRepository
        .findByUserIdAndChannelIdWithUserAndChannel(user.getId(), channel.getId());

    // then
    assertThat(foundReadStatus).isPresent();
    ReadStatus readStatus = foundReadStatus.get();
    assertThat(readStatus.getUser().getId()).isEqualTo(user.getId());
    assertThat(readStatus.getChannel().getId()).isEqualTo(channel.getId());
    assertThat(readStatus.getLastReadAt()).isEqualTo(now);
  }

  @Test
  @DisplayName("findByUserIdAndChannelIdWithUserAndChannel_존재하지않는조합_빈Optional반환")
  void findByUserIdAndChannelIdWithUserAndChannel_NonExistingCombination_ReturnsEmpty() {
    // given
    UUID nonExistingUserId = UUID.randomUUID();
    UUID nonExistingChannelId = UUID.randomUUID();

    // when
    Optional<ReadStatus> foundReadStatus = readStatusRepository
        .findByUserIdAndChannelIdWithUserAndChannel(nonExistingUserId, nonExistingChannelId);

    // then
    assertThat(foundReadStatus).isEmpty();
  }

  // === 존재 여부 확인 테스트 ===

  @Test
  @DisplayName("existsByUserIdAndChannelId_존재하는조합_true반환")
  void existsByUserIdAndChannelId_ExistingCombination_ReturnsTrue() {
    // given
    User user = createAndSaveTestUser("testuser", "test@example.com");
    Channel channel = createAndSaveTestChannel(ChannelType.PUBLIC, "Test Channel", "Description");

    Instant now = Instant.now().truncatedTo(ChronoUnit.SECONDS);
    createAndSaveTestReadStatus(user, channel, now);
    clearPersistenceContext();

    // when
    boolean exists = readStatusRepository.existsByUserIdAndChannelId(user.getId(), channel.getId());

    // then
    assertThat(exists).isTrue();
  }

  @Test
  @DisplayName("existsByUserIdAndChannelId_존재하지않는조합_false반환")
  void existsByUserIdAndChannelId_NonExistingCombination_ReturnsFalse() {
    // given
    UUID nonExistingUserId = UUID.randomUUID();
    UUID nonExistingChannelId = UUID.randomUUID();

    // when
    boolean exists = readStatusRepository.existsByUserIdAndChannelId(nonExistingUserId, nonExistingChannelId);

    // then
    assertThat(exists).isFalse();
  }

  // === 채널 ID 목록 조회 테스트 ===

  @Test
  @DisplayName("findChannelIdsByUserId_사용자가구독한채널들_채널ID목록반환")
  void findChannelIdsByUserId_UserSubscribedChannels_ReturnsChannelIds() {
    // given
    User user = createAndSaveTestUser("testuser", "test@example.com");
    Channel channel1 = createAndSaveTestChannel(ChannelType.PUBLIC, "Channel 1", "Description 1");
    Channel channel2 = createAndSaveTestChannel(ChannelType.PUBLIC, "Channel 2", "Description 2");
    Channel channel3 = createAndSaveTestChannel(ChannelType.PUBLIC, "Channel 3", "Description 3");

    Instant now = Instant.now().truncatedTo(ChronoUnit.SECONDS);
    createAndSaveTestReadStatus(user, channel1, now);
    createAndSaveTestReadStatus(user, channel2, now.plusSeconds(30));
    // channel3는 구독하지 않음
    clearPersistenceContext();

    // when
    List<UUID> channelIds = readStatusRepository.findChannelIdsByUserId(user.getId());

    // then
    assertThat(channelIds).hasSize(2);
    assertThat(channelIds).containsExactlyInAnyOrder(channel1.getId(), channel2.getId());
    assertThat(channelIds).doesNotContain(channel3.getId());
  }

  // === JpaRepository 기본 메서드 테스트 ===

  @Test
  @DisplayName("save_유효한읽기상태정보_읽기상태저장성공")
  void save_ValidReadStatusData_SavesSuccessfully() {
    // given
    User user = createAndSaveTestUser("testuser", "test@example.com");
    Channel channel = createAndSaveTestChannel(ChannelType.PUBLIC, "Test Channel", "Description");

    Instant now = Instant.now().truncatedTo(ChronoUnit.SECONDS);
    ReadStatus readStatus = new ReadStatus(user, channel, now);

    // when
    ReadStatus savedReadStatus = readStatusRepository.save(readStatus);
    clearPersistenceContext();

    // then
    assertThat(savedReadStatus.getId()).isNotNull();
    assertThat(savedReadStatus.getCreatedAt()).isNotNull();
    assertThat(savedReadStatus.getUpdatedAt()).isNotNull();
    assertThat(savedReadStatus.getUser().getId()).isEqualTo(user.getId());
    assertThat(savedReadStatus.getChannel().getId()).isEqualTo(channel.getId());
    assertThat(savedReadStatus.getLastReadAt()).isEqualTo(now);
  }

  @Test
  @DisplayName("findById_존재하는ID로조회_읽기상태반환")
  void findById_ExistingId_ReturnsReadStatus() {
    // given
    User user = createAndSaveTestUser("testuser", "test@example.com");
    Channel channel = createAndSaveTestChannel(ChannelType.PUBLIC, "Test Channel", "Description");

    Instant now = Instant.now().truncatedTo(ChronoUnit.SECONDS);
    ReadStatus savedReadStatus = createAndSaveTestReadStatus(user, channel, now);
    clearPersistenceContext();

    // when
    Optional<ReadStatus> foundReadStatus = readStatusRepository.findById(savedReadStatus.getId());

    // then
    assertThat(foundReadStatus).isPresent();
    assertThat(foundReadStatus.get().getUser().getId()).isEqualTo(user.getId());
    assertThat(foundReadStatus.get().getChannel().getId()).isEqualTo(channel.getId());
    assertThat(foundReadStatus.get().getLastReadAt()).isEqualTo(now);
  }

  @Test
  @DisplayName("deleteById_존재하는ID로삭제_삭제성공")
  void deleteById_ExistingId_DeletesSuccessfully() {
    // given
    User user = createAndSaveTestUser("testuser", "test@example.com");
    Channel channel = createAndSaveTestChannel(ChannelType.PUBLIC, "Test Channel", "Description");

    Instant now = Instant.now().truncatedTo(ChronoUnit.SECONDS);
    ReadStatus savedReadStatus = createAndSaveTestReadStatus(user, channel, now);
    clearPersistenceContext();

    // 삭제 전 존재 확인
    Optional<ReadStatus> beforeDelete = readStatusRepository.findById(savedReadStatus.getId());
    assertThat(beforeDelete).isPresent();

    // when & then - 예외 없이 실행되어야 함
    assertThatNoException().isThrownBy(() -> {
      readStatusRepository.deleteById(savedReadStatus.getId());
    });
  }

  @Test
  @DisplayName("deleteAllByChannelId_채널의모든읽기상태삭제_삭제성공")
  void deleteAllByChannelId_DeleteAllReadStatusesInChannel_DeletesSuccessfully() {
    // given
    User user1 = createAndSaveTestUser("user1", "user1@example.com");
    User user2 = createAndSaveTestUser("user2", "user2@example.com");
    Channel channel1 = createAndSaveTestChannel(ChannelType.PUBLIC, "Channel 1", "Description 1");
    Channel channel2 = createAndSaveTestChannel(ChannelType.PUBLIC, "Channel 2", "Description 2");

    Instant now = Instant.now().truncatedTo(ChronoUnit.SECONDS);
    createAndSaveTestReadStatus(user1, channel1, now);
    createAndSaveTestReadStatus(user2, channel1, now.plusSeconds(30));
    createAndSaveTestReadStatus(user1, channel2, now.plusSeconds(60));
    clearPersistenceContext();

    // 삭제 전 확인
    List<ReadStatus> beforeDelete = readStatusRepository.findAllByChannelId(channel1.getId());
    assertThat(beforeDelete).hasSize(2);

    // when & then - 예외 없이 실행되어야 함
    assertThatNoException().isThrownBy(() -> {
      readStatusRepository.deleteAllByChannelId(channel1.getId());
    });

    // 다른 채널의 읽기상태는 영향받지 않음 확인
    List<ReadStatus> channel2ReadStatuses = readStatusRepository.findAllByChannelId(channel2.getId());
    assertThat(channel2ReadStatuses).hasSize(1);
  }

  // === 엔티티 업데이트 테스트 ===

  @Test
  @DisplayName("update_마지막읽은시간변경_업데이트성공")
  void update_LastReadAtChange_UpdatesSuccessfully() {
    // given
    User user = createAndSaveTestUser("testuser", "test@example.com");
    Channel channel = createAndSaveTestChannel(ChannelType.PUBLIC, "Test Channel", "Description");

    Instant originalTime = Instant.now().truncatedTo(ChronoUnit.SECONDS);
    ReadStatus readStatus = createAndSaveTestReadStatus(user, channel, originalTime);
    clearPersistenceContext();

    // when
    ReadStatus foundReadStatus = readStatusRepository.findById(readStatus.getId()).orElseThrow();
    Instant newTime = originalTime.plusSeconds(3600); // 1시간 후
    foundReadStatus.update(newTime);
    ReadStatus savedReadStatus = readStatusRepository.saveAndFlush(foundReadStatus);

    // then
    assertThat(savedReadStatus.getLastReadAt()).isEqualTo(newTime);
    assertThat(savedReadStatus.getLastReadAt()).isNotEqualTo(originalTime);
    assertThat(savedReadStatus.getId()).isEqualTo(readStatus.getId());
    assertThat(savedReadStatus.getUser().getId()).isEqualTo(user.getId());
    assertThat(savedReadStatus.getChannel().getId()).isEqualTo(channel.getId());
  }

  // === 유니크 제약조건 테스트 ===

  @Test
  @DisplayName("uniqueConstraint_동일사용자와채널조합_중복저장시예외발생")
  void uniqueConstraint_SameUserAndChannelCombination_ThrowsExceptionOnDuplicate() {
    // given
    User user = createAndSaveTestUser("testuser", "test@example.com");
    Channel channel = createAndSaveTestChannel(ChannelType.PUBLIC, "Test Channel", "Description");

    Instant now = Instant.now().truncatedTo(ChronoUnit.SECONDS);
    createAndSaveTestReadStatus(user, channel, now);
    clearPersistenceContext();

    // when & then - 동일한 사용자와 채널 조합으로 다시 저장 시도하면 예외 발생
    ReadStatus duplicateReadStatus = new ReadStatus(user, channel, now.plusSeconds(60));

    assertThatThrownBy(() -> {
      readStatusRepository.saveAndFlush(duplicateReadStatus);
    }).isInstanceOf(Exception.class); // 정확한 예외 타입은 JPA 구현체에 따라 다를 수 있음
  }

  // === 복합 조건 테스트 ===

  @Test
  @DisplayName("complexQuery_여러사용자와채널조합_정확한결과반환")
  void complexQuery_MultipleUsersAndChannels_ReturnsCorrectResults() {
    // given
    User user1 = createAndSaveTestUser("user1", "user1@example.com");
    User user2 = createAndSaveTestUser("user2", "user2@example.com");
    Channel channel1 = createAndSaveTestChannel(ChannelType.PUBLIC, "Channel 1", "Description 1");
    Channel channel2 = createAndSaveTestChannel(ChannelType.PRIVATE, "Channel 2", "Description 2");

    Instant now = Instant.now().truncatedTo(ChronoUnit.SECONDS);
    createAndSaveTestReadStatus(user1, channel1, now);
    createAndSaveTestReadStatus(user1, channel2, now.plusSeconds(30));
    createAndSaveTestReadStatus(user2, channel1, now.plusSeconds(60));
    clearPersistenceContext();

    // when & then - 사용자1의 읽기상태 조회
    List<ReadStatus> user1ReadStatuses = readStatusRepository.findAllByUserId(user1.getId());
    assertThat(user1ReadStatuses).hasSize(2);

    // when & then - 채널1의 읽기상태 조회
    List<ReadStatus> channel1ReadStatuses = readStatusRepository.findAllByChannelId(channel1.getId());
    assertThat(channel1ReadStatuses).hasSize(2);

    // when & then - 사용자1이 구독한 채널 ID 조회
    List<UUID> user1ChannelIds = readStatusRepository.findChannelIdsByUserId(user1.getId());
    assertThat(user1ChannelIds).containsExactlyInAnyOrder(channel1.getId(), channel2.getId());

    // when & then - 사용자2가 구독한 채널 ID 조회
    List<UUID> user2ChannelIds = readStatusRepository.findChannelIdsByUserId(user2.getId());
    assertThat(user2ChannelIds).containsExactly(channel1.getId());
  }
}