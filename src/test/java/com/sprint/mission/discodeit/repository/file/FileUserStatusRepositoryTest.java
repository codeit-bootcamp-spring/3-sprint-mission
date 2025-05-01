package com.sprint.mission.discodeit.repository.file;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.fixture.UserStatusFixture;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import java.nio.file.Path;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class FileUserStatusRepositoryTest {

  @TempDir
  static Path tempDir;

  private UserStatusRepository userStatusRepository;
  private Path filePath;

  @BeforeEach
  void setUp() {
    filePath = tempDir.resolve("user-status.ser");
    userStatusRepository = FileUserStatusRepository.from(filePath.toString());
  }

  @Test
  @DisplayName("[File] ID로 사용자 상태를 찾을 수 있어야 한다")
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
  @DisplayName("[File] 존재하지 않는 ID로 사용자 상태를 찾으면 Optional.empty()를 반환해야 한다")
  void findByIdShouldReturnEmptyOptionalIfNotFound() {
    // given
    UUID nonExistingId = UUID.randomUUID();

    // when
    Optional<UserStatus> foundStatus = userStatusRepository.findById(nonExistingId);

    // then
    assertThat(foundStatus).isEmpty();
  }

  @Test
  @DisplayName("[File] 사용자 ID로 사용자 상태를 찾을 수 있어야 한다")
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
  @DisplayName("[File] 존재하지 않는 사용자 ID로 사용자 상태를 찾으면 Optional.empty()를 반환해야 한다")
  void findByUserIdShouldReturnEmptyOptionalIfNotFound() {
    // given
    UUID nonExistingUserId = UUID.randomUUID();

    // when
    Optional<UserStatus> foundStatus = userStatusRepository.findByUserId(nonExistingUserId);

    // then
    assertThat(foundStatus).isEmpty();
  }

  @Test
  @DisplayName("[File] 사용자 상태를 저장해야 한다")
  void saveShouldPersistUserStatus() {
    // given
    UUID userId = UUID.randomUUID();
    UserStatus userStatusToSave = UserStatusFixture.createValidUserStatus(userId);

    // when
    UserStatus savedStatus = userStatusRepository.save(userStatusToSave);
    Optional<UserStatus> loadedStatus = FileUserStatusRepository.from(filePath.toString())
        .findById(savedStatus.getId());

    // then
    assertThat(loadedStatus).isPresent();
    assertThat(loadedStatus.get().getId()).isEqualTo(savedStatus.getId());
    assertThat(loadedStatus.get().getUserId()).isEqualTo(userId);
  }

  @Test
  @DisplayName("[File] ID로 사용자 상태를 삭제해야 한다")
  void deleteShouldRemoveUserStatusIfExists() {
    // given
    UUID userId = UUID.randomUUID();
    UserStatus savedStatus = userStatusRepository.save(
        UserStatusFixture.createValidUserStatus(userId));
    UUID statusIdToDelete = savedStatus.getId();

    // when
    userStatusRepository.delete(statusIdToDelete);
    Optional<UserStatus> deletedStatus = FileUserStatusRepository.from(filePath.toString())
        .findById(statusIdToDelete);

    // then
    assertThat(deletedStatus).isEmpty();
  }

  @Test
  @DisplayName("[File] 존재하지 않는 ID로 삭제를 시도해도 예외가 발생하지 않아야 한다")
  void deleteShouldNotThrowExceptionIfNotFound() {
    // given
    UUID nonExistingId = UUID.randomUUID();

    // when & then
    userStatusRepository.delete(nonExistingId);
    assertTrue(true, "예외가 발생하지 않았음");
  }
}