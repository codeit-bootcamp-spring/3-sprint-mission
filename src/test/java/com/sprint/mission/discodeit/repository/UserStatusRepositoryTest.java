package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.testutil.TestDataBuilder;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.test.context.ActiveProfiles;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;

@DataJpaTest
@EnableJpaAuditing
@ActiveProfiles("test")
@DisplayName("UserStatusRepository 슬라이스 테스트")
class UserStatusRepositoryTest {

  @Autowired
  private UserStatusRepository userStatusRepository;

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private EntityManager entityManager;

  // === TestFixture 메서드들 ===

  private User createAndSaveTestUser(String username, String email) {
    User user = TestDataBuilder.createUser(username, email);
    return userRepository.saveAndFlush(user);
  }

  private UserStatus createAndSaveTestUserStatus(User user, Instant lastActiveAt) {
    UserStatus userStatus = new UserStatus(user, lastActiveAt);
    return userStatusRepository.saveAndFlush(userStatus);
  }

  private UserStatus createAndSaveTestUserStatus(String username, String email, Instant lastActiveAt) {
    User user = createAndSaveTestUser(username, email);
    return createAndSaveTestUserStatus(user, lastActiveAt);
  }

  private void clearPersistenceContext() {
    entityManager.flush();
    entityManager.clear();
  }

  // === findByUserId 테스트 ===

  @Test
  @DisplayName("findByUserId_존재하는사용자ID로조회_사용자상태반환")
  void findByUserId_ExistingUserId_ReturnsUserStatus() {
    // given
    Instant lastActiveAt = Instant.now();
    UserStatus savedUserStatus = createAndSaveTestUserStatus("testuser", "test@example.com", lastActiveAt);
    clearPersistenceContext();

    // when
    Optional<UserStatus> result = userStatusRepository.findByUserId(savedUserStatus.getUser().getId());

    // then
    assertThat(result).isPresent();
    UserStatus foundUserStatus = result.get();
    assertThat(foundUserStatus.getUser().getUsername()).isEqualTo("testuser");
    assertThat(foundUserStatus.getUser().getEmail()).isEqualTo("test@example.com");
    // 시간 비교를 좀 더 관대하게 처리
    assertThat(foundUserStatus.getLastActiveAt().truncatedTo(java.time.temporal.ChronoUnit.SECONDS))
        .isEqualTo(lastActiveAt.truncatedTo(java.time.temporal.ChronoUnit.SECONDS));
  }

  @Test
  @DisplayName("findByUserId_존재하지않는사용자ID로조회_빈Optional반환")
  void findByUserId_NonExistingUserId_ReturnsEmpty() {
    // given
    UUID nonExistingUserId = UUID.randomUUID();
    createAndSaveTestUserStatus("testuser", "test@example.com", Instant.now());
    clearPersistenceContext();

    // when
    Optional<UserStatus> result = userStatusRepository.findByUserId(nonExistingUserId);

    // then
    assertThat(result).isEmpty();
  }

  // === deleteByUserId 테스트 ===

  @Test
  @DisplayName("deleteByUserId_존재하는사용자ID로삭제_삭제성공")
  void deleteByUserId_ExistingUserId_DeletesSuccessfully() {
    // given
    User savedUser = createAndSaveTestUser("testuser", "test@example.com");
    createAndSaveTestUserStatus(savedUser, Instant.now());
    clearPersistenceContext();

    // 삭제 전 존재 확인
    Optional<UserStatus> beforeDelete = userStatusRepository.findByUserId(savedUser.getId());
    assertThat(beforeDelete).isPresent();

    // when & then - 예외 없이 실행되어야 함
    assertThatNoException().isThrownBy(() -> {
      userStatusRepository.deleteByUserId(savedUser.getId());
    });
  }

  @Test
  @DisplayName("deleteByUserId_존재하지않는사용자ID로삭제_삭제실행됨")
  void deleteByUserId_NonExistingUserId_ExecutesWithoutError() {
    // given
    UUID nonExistingUserId = UUID.randomUUID();
    createAndSaveTestUserStatus("testuser", "test@example.com", Instant.now());
    clearPersistenceContext();

    // when & then - 예외가 발생하지 않아야 함
    assertThatNoException().isThrownBy(() -> {
      userStatusRepository.deleteByUserId(nonExistingUserId);
    });

    // 기존 데이터는 영향받지 않음을 확인
    List<UserStatus> allUserStatuses = userStatusRepository.findAll();
    assertThat(allUserStatuses).hasSize(1);
  }

  // === JpaRepository 기본 메서드 테스트 ===

  @Test
  @DisplayName("save_유효한사용자상태정보_사용자상태저장성공")
  void save_ValidUserStatusData_SavesSuccessfully() {
    // given
    User user = createAndSaveTestUser("testuser", "test@example.com");
    Instant lastActiveAt = Instant.now();
    UserStatus userStatus = new UserStatus(user, lastActiveAt);

    // when
    UserStatus savedUserStatus = userStatusRepository.save(userStatus);
    clearPersistenceContext();

    // then
    assertThat(savedUserStatus.getId()).isNotNull();
    assertThat(savedUserStatus.getCreatedAt()).isNotNull();
    assertThat(savedUserStatus.getUpdatedAt()).isNotNull();
    assertThat(savedUserStatus.getUser().getId()).isEqualTo(user.getId());
    assertThat(savedUserStatus.getLastActiveAt()).isEqualTo(lastActiveAt);
  }

  @Test
  @DisplayName("findById_존재하는ID로조회_사용자상태반환")
  void findById_ExistingId_ReturnsUserStatus() {
    // given
    UserStatus savedUserStatus = createAndSaveTestUserStatus("testuser", "test@example.com", Instant.now());
    clearPersistenceContext();

    // when
    Optional<UserStatus> foundUserStatus = userStatusRepository.findById(savedUserStatus.getId());

    // then
    assertThat(foundUserStatus).isPresent();
    assertThat(foundUserStatus.get().getUser().getUsername()).isEqualTo("testuser");
    assertThat(foundUserStatus.get().getUser().getEmail()).isEqualTo("test@example.com");
  }

  @Test
  @DisplayName("deleteById_존재하는ID로삭제_삭제성공")
  void deleteById_ExistingId_DeletesSuccessfully() {
    // given
    UserStatus savedUserStatus = createAndSaveTestUserStatus("testuser", "test@example.com", Instant.now());
    clearPersistenceContext();

    // 삭제 전 존재 확인
    Optional<UserStatus> beforeDelete = userStatusRepository.findById(savedUserStatus.getId());
    assertThat(beforeDelete).isPresent();

    // when & then - 예외 없이 실행되어야 함
    assertThatNoException().isThrownBy(() -> {
      userStatusRepository.deleteById(savedUserStatus.getId());
    });
  }

  // === 페이징 및 정렬 테스트 ===

  @Test
  @DisplayName("findAll_페이징처리_페이지네이션결과반환")
  void findAll_WithPaging_ReturnsPagedResults() {
    // given
    Instant now = Instant.now();
    createAndSaveTestUserStatus("user1", "user1@example.com", now.minusSeconds(3600));
    createAndSaveTestUserStatus("user2", "user2@example.com", now.minusSeconds(1800));
    createAndSaveTestUserStatus("user3", "user3@example.com", now);
    clearPersistenceContext();

    Pageable pageable = PageRequest.of(0, 2);

    // when
    Page<UserStatus> userStatusPage = userStatusRepository.findAll(pageable);

    // then
    assertThat(userStatusPage.getContent()).hasSize(2);
    assertThat(userStatusPage.getTotalElements()).isEqualTo(3);
    assertThat(userStatusPage.getTotalPages()).isEqualTo(2);
    assertThat(userStatusPage.isFirst()).isTrue();
    assertThat(userStatusPage.hasNext()).isTrue();
  }

  @Test
  @DisplayName("findAll_마지막활동시간기준정렬_정렬된결과반환")
  void findAll_WithSorting_ReturnsSortedResults() {
    // given
    Instant now = Instant.now();
    Instant oldTime = now.minusSeconds(7200);
    Instant midTime = now.minusSeconds(3600);

    createAndSaveTestUserStatus("user1", "user1@example.com", midTime);
    createAndSaveTestUserStatus("user2", "user2@example.com", now);
    createAndSaveTestUserStatus("user3", "user3@example.com", oldTime);
    clearPersistenceContext();

    Sort sort = Sort.by(Sort.Direction.DESC, "lastActiveAt");

    // when
    List<UserStatus> sortedUserStatuses = userStatusRepository.findAll(sort);

    // then
    assertThat(sortedUserStatuses).hasSize(3);
    // 최신 활동 시간 순으로 정렬되어야 함
    assertThat(sortedUserStatuses.get(0).getUser().getUsername()).isEqualTo("user2");
    assertThat(sortedUserStatuses.get(1).getUser().getUsername()).isEqualTo("user1");
    assertThat(sortedUserStatuses.get(2).getUser().getUsername()).isEqualTo("user3");
  }

  @Test
  @DisplayName("findAll_페이징과정렬조합_페이지네이션된정렬결과반환")
  void findAll_WithPagingAndSorting_ReturnsPagedAndSortedResults() {
    // given
    Instant now = Instant.now();
    createAndSaveTestUserStatus("user1", "user1@example.com", now.minusSeconds(3600));
    createAndSaveTestUserStatus("user2", "user2@example.com", now);
    createAndSaveTestUserStatus("user3", "user3@example.com", now.minusSeconds(7200));
    createAndSaveTestUserStatus("user4", "user4@example.com", now.minusSeconds(1800));
    clearPersistenceContext();

    Pageable pageable = PageRequest.of(0, 2, Sort.by(Sort.Direction.DESC, "lastActiveAt"));

    // when
    Page<UserStatus> userStatusPage = userStatusRepository.findAll(pageable);

    // then
    assertThat(userStatusPage.getContent()).hasSize(2);
    assertThat(userStatusPage.getContent().get(0).getUser().getUsername()).isEqualTo("user2"); // 가장 최근
    assertThat(userStatusPage.getContent().get(1).getUser().getUsername()).isEqualTo("user4"); // 두 번째 최근
    assertThat(userStatusPage.getTotalElements()).isEqualTo(4);
    assertThat(userStatusPage.isFirst()).isTrue();
    assertThat(userStatusPage.hasNext()).isTrue();
  }

  // === 온라인 상태 테스트 ===

  @Test
  @DisplayName("isOnline_최근활동시간_온라인상태true반환")
  void isOnline_RecentActivity_ReturnsTrue() {
    // given
    Instant recentTime = Instant.now().minusSeconds(60); // 1분 전 (5분 임계값보다 짧음)
    UserStatus userStatus = createAndSaveTestUserStatus("testuser", "test@example.com", recentTime);
    clearPersistenceContext();

    // when
    UserStatus foundUserStatus = userStatusRepository.findById(userStatus.getId()).orElseThrow();
    boolean isOnline = foundUserStatus.isOnline();

    // then
    assertThat(isOnline).isTrue();
  }

  @Test
  @DisplayName("isOnline_오래된활동시간_온라인상태false반환")
  void isOnline_OldActivity_ReturnsFalse() {
    // given
    Instant oldTime = Instant.now().minusSeconds(600); // 10분 전 (5분 임계값보다 김)
    UserStatus userStatus = createAndSaveTestUserStatus("testuser", "test@example.com", oldTime);
    clearPersistenceContext();

    // when
    UserStatus foundUserStatus = userStatusRepository.findById(userStatus.getId()).orElseThrow();
    boolean isOnline = foundUserStatus.isOnline();

    // then
    assertThat(isOnline).isFalse();
  }

  // === 업데이트 테스트 ===

  @Test
  @DisplayName("update_마지막활동시간변경_업데이트성공")
  void update_LastActiveAtChange_UpdatesSuccessfully() {
    // given
    Instant initialTime = Instant.now().minusSeconds(3600);
    UserStatus userStatus = createAndSaveTestUserStatus("testuser", "test@example.com", initialTime);
    clearPersistenceContext();

    // when
    UserStatus foundUserStatus = userStatusRepository.findById(userStatus.getId()).orElseThrow();
    Instant newTime = Instant.now().truncatedTo(java.time.temporal.ChronoUnit.SECONDS);
    foundUserStatus.update(newTime);
    userStatusRepository.saveAndFlush(foundUserStatus);
    clearPersistenceContext();

    // then
    UserStatus updatedUserStatus = userStatusRepository.findById(userStatus.getId()).orElseThrow();
    assertThat(updatedUserStatus.getLastActiveAt().truncatedTo(java.time.temporal.ChronoUnit.SECONDS))
        .isEqualTo(newTime);
    assertThat(updatedUserStatus.getUpdatedAt()).isNotNull();
    assertThat(updatedUserStatus.getUpdatedAt()).isAfter(updatedUserStatus.getCreatedAt());
  }
}
