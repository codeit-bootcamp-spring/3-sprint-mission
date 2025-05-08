package com.sprint.mission.discodeit.repository.file;

import static org.assertj.core.api.Assertions.assertThat;

import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.fixture.UserStatusFixture;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.repository.storage.FileStorageImpl;
import java.nio.file.Path;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class FileUserStatusRepositoryTest {

  @TempDir
  static Path tempDir;

  private UserStatusRepository userStatusRepository;

  // 각 테스트가 실행될 때마다 새로 생성되도록 초기화
  @BeforeEach
  void setUp() {
    // FileStorageImpl 객체를 통해 저장소 초기화
    userStatusRepository = FileUserStatusRepository.create(new FileStorageImpl(tempDir.toString()));
  }

  @Nested
  class Create {

    @Test
    void 사용자_상태를_저장해야_한다() {
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
  }

  @Nested
  class Read {

    @Test
    void ID로_사용자_상태를_찾을_수_있어야_한다() {
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
    void 존재하지_않는_ID로_사용자_상태를_찾으면_비어_있는_Optional을_반환해야_한다() {
      // given
      UUID nonExistingId = UUID.randomUUID();

      // when
      Optional<UserStatus> foundStatus = userStatusRepository.findById(nonExistingId);

      // then
      assertThat(foundStatus).isEmpty();
    }

    @Test
    void 사용자_ID로_사용자_상태를_찾을_수_있어야_한다() {
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
    void 존재하지_않는_사용자_ID로_사용자_상태를_찾으면_비어_있는_Optional을_반환해야_한다() {
      // given
      UUID nonExistingUserId = UUID.randomUUID();

      // when
      Optional<UserStatus> foundStatus = userStatusRepository.findByUserId(nonExistingUserId);

      // then
      assertThat(foundStatus).isEmpty();
    }
  }

  @Nested
  class Delete {

    @Test
    void ID로_사용자_상태를_삭제해야_한다() {
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
  }
}
