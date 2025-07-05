package com.sprint.mission.discodeit.repository.jcf;

import static org.assertj.core.api.Assertions.assertThat;

import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.fixture.UserStatusFixture;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class JCFUserStatusRepositoryTest {

  private JCFUserStatusRepository userStatusRepository;

  @BeforeEach
  void setUp() {
    userStatusRepository = new JCFUserStatusRepository();
  }

  @Test
  @DisplayName("[JCF] ID로 사용자 상태를 찾을 수 있어야 한다")
  void findByIdShouldReturnUserStatusIfExists() {
    // given
    UUID userId = UUID.randomUUID();
    UserStatus savedStatus = userStatusRepository.save(
        UserStatusFixture.createValidUserStatus(userId));

    // when
    Optional<UserStatus> foundStatus = userStatusRepository.findById(savedStatus.getId());

    // then
    assertThat(foundStatus).isPresent();
    assertThat(foundStatus.get().getId()).isEqualTo(savedStatus.getId());
    assertThat(foundStatus.get().getUserId()).isEqualTo(userId);
  }

  @Test
  @DisplayName("[JCF] 존재하지 않는 ID로 사용자 상태를 찾으면 Optional.empty()를 반환해야 한다")
  void findByIdShouldReturnEmptyOptionalIfNotFound() {
    // given
    UUID nonExistingId = UUID.randomUUID();

    // when
    Optional<UserStatus> foundStatus = userStatusRepository.findById(nonExistingId);

    // then
    assertThat(foundStatus).isEmpty();
  }

  @Test
  @DisplayName("[JCF] 사용자 ID로 사용자 상태를 찾을 수 있어야 한다")
  void findByUserIdShouldReturnUserStatusIfExists() {
    // given
    UUID userId = UUID.randomUUID();
    UserStatus savedStatus = userStatusRepository.save(
        UserStatusFixture.createValidUserStatus(userId));

    // when
    Optional<UserStatus> foundStatus = userStatusRepository.findByUserId(userId);

    // then
    assertThat(foundStatus).isPresent();
    assertThat(foundStatus.get().getId()).isEqualTo(savedStatus.getId());
    assertThat(foundStatus.get().getUserId()).isEqualTo(userId);
  }

  @Test
  @DisplayName("[JCF] 존재하지 않는 사용자 ID로 사용자 상태를 찾으면 Optional.empty()를 반환해야 한다")
  void findByUserIdShouldReturnEmptyOptionalIfNotFound() {
    // given
    UUID nonExistingUserId = UUID.randomUUID();

    // when
    Optional<UserStatus> foundStatus = userStatusRepository.findByUserId(nonExistingUserId);

    // then
    assertThat(foundStatus).isEmpty();
  }

  @Test
  @DisplayName("[JCF] 사용자 상태를 저장해야 한다")
  void saveShouldPersistUserStatus() {
    // given
    UUID userId = UUID.randomUUID();
    UserStatus userStatusToSave = UserStatusFixture.createValidUserStatus(userId);

    // when
    UserStatus savedStatus = userStatusRepository.save(userStatusToSave);
    Optional<UserStatus> loadedStatus = userStatusRepository.findById(savedStatus.getId());

    // then
    assertThat(loadedStatus).isPresent();
    assertThat(loadedStatus.get().getId()).isEqualTo(savedStatus.getId());
    assertThat(loadedStatus.get().getUserId()).isEqualTo(userId);
  }

  @Test
  @DisplayName("[JCF] ID로 사용자 상태를 삭제해야 한다")
  void deleteShouldRemoveUserStatusIfExists() {
    // given
    UUID userId = UUID.randomUUID();
    UserStatus savedStatus = userStatusRepository.save(
        UserStatusFixture.createValidUserStatus(userId));
    UUID statusIdToDelete = savedStatus.getId();

    // when
    userStatusRepository.delete(statusIdToDelete);
    Optional<UserStatus> deletedStatus = userStatusRepository.findById(statusIdToDelete);

    // then
    assertThat(deletedStatus).isEmpty();
  }

  @Test
  @DisplayName("[JCF] 존재하지 않는 ID로 삭제를 시도해도 아무런 동작을 하지 않아야 한다")
  void deleteShouldNotPerformActionIfNotFound() {
    // given
    UUID nonExistingId = UUID.randomUUID();

    // when
    userStatusRepository.delete(nonExistingId);

    // then
    Optional<UserStatus> notFoundStatus = userStatusRepository.findById(nonExistingId);
    assertThat(notFoundStatus).isEmpty();
  }
}