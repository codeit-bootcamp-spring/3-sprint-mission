package com.sprint.mission.discodeit.repository.jcf;

import static org.assertj.core.api.Assertions.assertThat;

import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.fixture.UserStatusFixture;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class JCFUserStatusRepositoryTest {

  private JCFUserStatusRepository userStatusRepository;

  @BeforeEach
  void setUp() {
    userStatusRepository = new JCFUserStatusRepository();
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
    void 존재하지_않는_ID로_사용자_상태를_찾으면_비어_있는_Optional를_반환해야_한다() {
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
    void 존재하지_않는_사용자_ID로_사용자_상태를_찾으면_비어_있는_Optional를_반환해야_한다() {
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

    @Test
    void 존재하지_않는_ID로_삭제를_시도해도_아무런_동작을_하지_않아야_한다() {
      // given
      UUID nonExistingId = UUID.randomUUID();

      // when
      userStatusRepository.delete(nonExistingId);

      // then
      Optional<UserStatus> notFoundStatus = userStatusRepository.findById(nonExistingId);
      assertThat(notFoundStatus).isEmpty();
    }
  }
}