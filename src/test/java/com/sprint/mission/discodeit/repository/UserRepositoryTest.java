package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.User;
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

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@EnableJpaAuditing
@ActiveProfiles("test")
@DisplayName("UserRepository 슬라이스 테스트")
class UserRepositoryTest {

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private EntityManager entityManager;

  // === TestFixture 메서드들 ===

  private User createAndSaveTestUser(String username, String email) {
    User user = TestDataBuilder.createUser(username, email);
    return userRepository.saveAndFlush(user);
  }

  private void clearPersistenceContext() {
    entityManager.flush();
    entityManager.clear();
  }

  // === findByUsername 테스트 ===

  @Test
  @DisplayName("findByUsername_존재하는유저명으로조회_유저반환")
  void findByUsername_ExistingUsername_ReturnsUser() {
    // given
    String testUsername = "testuser";
    String testEmail = "test@example.com";
    User savedUser = createAndSaveTestUser(testUsername, testEmail);
    clearPersistenceContext();

    // when
    Optional<User> foundUser = userRepository.findByUsername(testUsername);

    // then
    assertThat(foundUser).isPresent();
    assertThat(foundUser.get().getUsername()).isEqualTo(testUsername);
    assertThat(foundUser.get().getEmail()).isEqualTo(testEmail);
    assertThat(foundUser.get().getId()).isEqualTo(savedUser.getId());
  }

  @Test
  @DisplayName("findByUsername_존재하지않는유저명으로조회_빈Optional반환")
  void findByUsername_NonExistingUsername_ReturnsEmpty() {
    // given
    String nonExistingUsername = "nonexistent";
    createAndSaveTestUser("testuser", "test@example.com");
    clearPersistenceContext();

    // when
    Optional<User> foundUser = userRepository.findByUsername(nonExistingUsername);

    // then
    assertThat(foundUser).isEmpty();
  }

  // === existsByEmail 테스트 ===

  @Test
  @DisplayName("existsByEmail_존재하는이메일로확인_true반환")
  void existsByEmail_ExistingEmail_ReturnsTrue() {
    // given
    String testEmail = "test@example.com";
    createAndSaveTestUser("testuser", testEmail);
    clearPersistenceContext();

    // when
    boolean exists = userRepository.existsByEmail(testEmail);

    // then
    assertThat(exists).isTrue();
  }

  @Test
  @DisplayName("existsByEmail_존재하지않는이메일로확인_false반환")
  void existsByEmail_NonExistingEmail_ReturnsFalse() {
    // given
    String nonExistingEmail = "nonexistent@example.com";
    createAndSaveTestUser("testuser", "test@example.com");
    clearPersistenceContext();

    // when
    boolean exists = userRepository.existsByEmail(nonExistingEmail);

    // then
    assertThat(exists).isFalse();
  }

  // === existsByUsername 테스트 ===

  @Test
  @DisplayName("existsByUsername_존재하는유저명으로확인_true반환")
  void existsByUsername_ExistingUsername_ReturnsTrue() {
    // given
    String testUsername = "testuser";
    createAndSaveTestUser(testUsername, "test@example.com");
    clearPersistenceContext();

    // when
    boolean exists = userRepository.existsByUsername(testUsername);

    // then
    assertThat(exists).isTrue();
  }

  @Test
  @DisplayName("existsByUsername_존재하지않는유저명으로확인_false반환")
  void existsByUsername_NonExistingUsername_ReturnsFalse() {
    // given
    String nonExistingUsername = "nonexistent";
    createAndSaveTestUser("testuser", "test@example.com");
    clearPersistenceContext();

    // when
    boolean exists = userRepository.existsByUsername(nonExistingUsername);

    // then
    assertThat(exists).isFalse();
  }

  // === JpaRepository 기본 메서드 테스트 ===

  @Test
  @DisplayName("save_유효한유저정보_유저저장성공")
  void save_ValidUserData_SavesSuccessfully() {
    // given
    User user = TestDataBuilder.createUser("newuser", "newuser@example.com");

    // when
    User savedUser = userRepository.save(user);
    clearPersistenceContext();

    // then
    assertThat(savedUser.getId()).isNotNull();
    assertThat(savedUser.getCreatedAt()).isNotNull();
    assertThat(savedUser.getUpdatedAt()).isNotNull();
    assertThat(savedUser.getUsername()).isEqualTo("newuser");
    assertThat(savedUser.getEmail()).isEqualTo("newuser@example.com");
  }

  @Test
  @DisplayName("findById_존재하는ID로조회_유저반환")
  void findById_ExistingId_ReturnsUser() {
    // given
    User savedUser = createAndSaveTestUser("testuser", "test@example.com");
    clearPersistenceContext();

    // when
    Optional<User> foundUser = userRepository.findById(savedUser.getId());

    // then
    assertThat(foundUser).isPresent();
    assertThat(foundUser.get().getUsername()).isEqualTo("testuser");
    assertThat(foundUser.get().getEmail()).isEqualTo("test@example.com");
  }

  @Test
  @DisplayName("deleteById_존재하는ID로삭제_삭제성공")
  void deleteById_ExistingId_DeletesSuccessfully() {
    // given
    User savedUser = createAndSaveTestUser("testuser", "test@example.com");
    clearPersistenceContext();

    // when
    userRepository.deleteById(savedUser.getId());
    clearPersistenceContext();

    // then
    Optional<User> deletedUser = userRepository.findById(savedUser.getId());
    assertThat(deletedUser).isEmpty();
  }

  // === 페이징 및 정렬 테스트 ===

  @Test
  @DisplayName("findAll_페이징처리_페이지네이션결과반환")
  void findAll_WithPaging_ReturnsPagedResults() {
    // given
    createAndSaveTestUser("user1", "user1@example.com");
    createAndSaveTestUser("user2", "user2@example.com");
    createAndSaveTestUser("user3", "user3@example.com");
    clearPersistenceContext();

    Pageable pageable = PageRequest.of(0, 2);

    // when
    Page<User> userPage = userRepository.findAll(pageable);

    // then
    assertThat(userPage.getContent()).hasSize(2);
    assertThat(userPage.getTotalElements()).isEqualTo(3);
    assertThat(userPage.getTotalPages()).isEqualTo(2);
    assertThat(userPage.isFirst()).isTrue();
    assertThat(userPage.hasNext()).isTrue();
  }

  @Test
  @DisplayName("findAll_유저명기준정렬_정렬된결과반환")
  void findAll_WithSorting_ReturnsSortedResults() {
    // given
    createAndSaveTestUser("charlie", "charlie@example.com");
    createAndSaveTestUser("alice", "alice@example.com");
    createAndSaveTestUser("bob", "bob@example.com");
    clearPersistenceContext();

    Sort sort = Sort.by(Sort.Direction.ASC, "username");

    // when
    List<User> sortedUsers = userRepository.findAll(sort);

    // then
    assertThat(sortedUsers).hasSize(3);
    assertThat(sortedUsers.get(0).getUsername()).isEqualTo("alice");
    assertThat(sortedUsers.get(1).getUsername()).isEqualTo("bob");
    assertThat(sortedUsers.get(2).getUsername()).isEqualTo("charlie");
  }

  @Test
  @DisplayName("findAll_페이징과정렬조합_페이지네이션된정렬결과반환")
  void findAll_WithPagingAndSorting_ReturnsPagedAndSortedResults() {
    // given
    createAndSaveTestUser("user3", "user3@example.com");
    createAndSaveTestUser("user1", "user1@example.com");
    createAndSaveTestUser("user2", "user2@example.com");
    createAndSaveTestUser("user4", "user4@example.com");
    clearPersistenceContext();

    Pageable pageable = PageRequest.of(0, 2, Sort.by(Sort.Direction.ASC, "username"));

    // when
    Page<User> userPage = userRepository.findAll(pageable);

    // then
    assertThat(userPage.getContent()).hasSize(2);
    assertThat(userPage.getContent().get(0).getUsername()).isEqualTo("user1");
    assertThat(userPage.getContent().get(1).getUsername()).isEqualTo("user2");
    assertThat(userPage.getTotalElements()).isEqualTo(4);
    assertThat(userPage.isFirst()).isTrue();
    assertThat(userPage.hasNext()).isTrue();
  }
}