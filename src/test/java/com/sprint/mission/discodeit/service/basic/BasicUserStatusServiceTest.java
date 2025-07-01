package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.request.UserStatusCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.exception.userstatus.DuplicateUserStatusException;
import com.sprint.mission.discodeit.exception.userstatus.InvalidUserStatusException;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.testutil.TestConstants;
import com.sprint.mission.discodeit.testutil.TestDataBuilder;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.never;

@ExtendWith(MockitoExtension.class)
@DisplayName("BasicUserStatusService 단위 테스트")
class BasicUserStatusServiceTest {

  @Mock
  private UserStatusRepository userStatusRepository;

  @Mock
  private UserRepository userRepository;

  @InjectMocks
  private BasicUserStatusService basicUserStatusService;

  // === create 테스트 ===

  @Test
  @DisplayName("사용자 상태 생성 성공")
  void createUserStatus_Success() {
    // given
    UUID userId = TestDataBuilder.USER_ID_1;
    Instant lastActiveAt = Instant.now();
    UserStatusCreateRequest request = new UserStatusCreateRequest(userId, lastActiveAt);

    User mockUser = TestDataBuilder.createDefaultUser();
    setEntityId(mockUser, userId);

    UserStatus mockUserStatus = new UserStatus(mockUser, lastActiveAt);
    setEntityId(mockUserStatus, UUID.randomUUID());

    given(userRepository.findById(userId)).willReturn(Optional.of(mockUser));
    given(userStatusRepository.findByUserId(userId)).willReturn(Optional.empty());
    given(userStatusRepository.save(any(UserStatus.class))).willReturn(mockUserStatus);

    // when
    UserStatus result = basicUserStatusService.create(request);

    // then
    assertThat(result).isNotNull();
    assertThat(result.getUser()).isEqualTo(mockUser);
    assertThat(result.getLastActiveAt()).isEqualTo(lastActiveAt);
    then(userRepository).should().findById(userId);
    then(userStatusRepository).should().findByUserId(userId);
    then(userStatusRepository).should().save(any(UserStatus.class));
  }

  @Test
  @DisplayName("사용자 상태 생성 실패 - 사용자 없음")
  void createUserStatus_Fail_UserNotFound() {
    // given
    UUID nonExistentUserId = UUID.randomUUID();
    Instant lastActiveAt = Instant.now();
    UserStatusCreateRequest request = new UserStatusCreateRequest(nonExistentUserId, lastActiveAt);

    given(userRepository.findById(nonExistentUserId)).willReturn(Optional.empty());

    // when & then
    assertThatThrownBy(() -> basicUserStatusService.create(request))
        .isInstanceOf(UserNotFoundException.class);

    then(userRepository).should().findById(nonExistentUserId);
    then(userStatusRepository).should(never()).findByUserId(any());
    then(userStatusRepository).should(never()).save(any());
  }

  @Test
  @DisplayName("사용자 상태 생성 실패 - 중복 사용자 상태")
  void createUserStatus_Fail_DuplicateUserStatus() {
    // given
    UUID userId = TestDataBuilder.USER_ID_1;
    Instant lastActiveAt = Instant.now();
    UserStatusCreateRequest request = new UserStatusCreateRequest(userId, lastActiveAt);

    User mockUser = TestDataBuilder.createDefaultUser();
    setEntityId(mockUser, userId);
    UserStatus existingUserStatus = new UserStatus(mockUser, lastActiveAt);

    given(userRepository.findById(userId)).willReturn(Optional.of(mockUser));
    given(userStatusRepository.findByUserId(userId)).willReturn(Optional.of(existingUserStatus));

    // when & then
    assertThatThrownBy(() -> basicUserStatusService.create(request))
        .isInstanceOf(DuplicateUserStatusException.class);

    then(userRepository).should().findById(userId);
    then(userStatusRepository).should().findByUserId(userId);
    then(userStatusRepository).should(never()).save(any());
  }

  // === find 테스트 ===

  @Test
  @DisplayName("사용자 상태 조회 성공")
  void findUserStatus_Success() {
    // given
    UUID userStatusId = UUID.randomUUID();
    User mockUser = TestDataBuilder.createDefaultUser();
    UserStatus mockUserStatus = new UserStatus(mockUser, Instant.now());
    setEntityId(mockUserStatus, userStatusId);

    given(userStatusRepository.findById(userStatusId)).willReturn(Optional.of(mockUserStatus));

    // when
    UserStatus result = basicUserStatusService.find(userStatusId);

    // then
    assertThat(result).isNotNull();
    assertThat(result).isEqualTo(mockUserStatus);
    then(userStatusRepository).should().findById(userStatusId);
  }

  @Test
  @DisplayName("사용자 상태 조회 실패 - 존재하지 않는 ID")
  void findUserStatus_Fail_NotFound() {
    // given
    UUID nonExistentId = UUID.randomUUID();
    given(userStatusRepository.findById(nonExistentId)).willReturn(Optional.empty());

    // when & then
    assertThatThrownBy(() -> basicUserStatusService.find(nonExistentId))
        .isInstanceOf(InvalidUserStatusException.class);

    then(userStatusRepository).should().findById(nonExistentId);
  }

  // === findAll 테스트 ===

  @Test
  @DisplayName("모든 사용자 상태 조회 성공")
  void findAllUserStatuses_Success() {
    // given
    User user1 = TestDataBuilder.createUser("user1", "user1@example.com");
    User user2 = TestDataBuilder.createUser("user2", "user2@example.com");
    UserStatus userStatus1 = new UserStatus(user1, Instant.now());
    UserStatus userStatus2 = new UserStatus(user2, Instant.now());
    List<UserStatus> mockUserStatuses = Arrays.asList(userStatus1, userStatus2);

    given(userStatusRepository.findAll()).willReturn(mockUserStatuses);

    // when
    List<UserStatus> result = basicUserStatusService.findAll();

    // then
    assertThat(result).isNotNull();
    assertThat(result).hasSize(2);
    assertThat(result).containsExactlyElementsOf(mockUserStatuses);
    then(userStatusRepository).should().findAll();
  }

  // === update 테스트 ===

  @Test
  @DisplayName("사용자 상태 업데이트 성공")
  void updateUserStatus_Success() {
    // given
    UUID userStatusId = UUID.randomUUID();
    Instant newLastActiveAt = Instant.now();
    UserStatusUpdateRequest request = new UserStatusUpdateRequest(newLastActiveAt);

    User mockUser = TestDataBuilder.createDefaultUser();
    UserStatus mockUserStatus = new UserStatus(mockUser, Instant.now().minusSeconds(3600));
    setEntityId(mockUserStatus, userStatusId);

    given(userStatusRepository.findById(userStatusId)).willReturn(Optional.of(mockUserStatus));

    // when
    UserStatus result = basicUserStatusService.update(userStatusId, request);

    // then
    assertThat(result).isNotNull();
    assertThat(result.getLastActiveAt()).isEqualTo(newLastActiveAt);
    then(userStatusRepository).should().findById(userStatusId);
  }

  @Test
  @DisplayName("사용자 상태 업데이트 실패 - 존재하지 않는 ID")
  void updateUserStatus_Fail_NotFound() {
    // given
    UUID nonExistentId = UUID.randomUUID();
    Instant newLastActiveAt = Instant.now();
    UserStatusUpdateRequest request = new UserStatusUpdateRequest(newLastActiveAt);

    given(userStatusRepository.findById(nonExistentId)).willReturn(Optional.empty());

    // when & then
    assertThatThrownBy(() -> basicUserStatusService.update(nonExistentId, request))
        .isInstanceOf(InvalidUserStatusException.class);

    then(userStatusRepository).should().findById(nonExistentId);
  }

  // === updateByUserId 테스트 ===

  @Test
  @DisplayName("사용자 ID로 상태 업데이트 성공")
  void updateUserStatusByUserId_Success() {
    // given
    UUID userId = TestDataBuilder.USER_ID_1;
    Instant newLastActiveAt = Instant.now();
    UserStatusUpdateRequest request = new UserStatusUpdateRequest(newLastActiveAt);

    User mockUser = TestDataBuilder.createDefaultUser();
    setEntityId(mockUser, userId);
    UserStatus mockUserStatus = new UserStatus(mockUser, Instant.now().minusSeconds(3600));

    given(userStatusRepository.findByUserId(userId)).willReturn(Optional.of(mockUserStatus));

    // when
    UserStatus result = basicUserStatusService.updateByUserId(userId, request);

    // then
    assertThat(result).isNotNull();
    assertThat(result.getLastActiveAt()).isEqualTo(newLastActiveAt);
    then(userStatusRepository).should().findByUserId(userId);
  }

  @Test
  @DisplayName("사용자 ID로 상태 업데이트 실패 - 존재하지 않는 사용자 ID")
  void updateUserStatusByUserId_Fail_NotFound() {
    // given
    UUID nonExistentUserId = UUID.randomUUID();
    Instant newLastActiveAt = Instant.now();
    UserStatusUpdateRequest request = new UserStatusUpdateRequest(newLastActiveAt);

    given(userStatusRepository.findByUserId(nonExistentUserId)).willReturn(Optional.empty());

    // when & then
    assertThatThrownBy(() -> basicUserStatusService.updateByUserId(nonExistentUserId, request))
        .isInstanceOf(InvalidUserStatusException.class);

    then(userStatusRepository).should().findByUserId(nonExistentUserId);
  }

  // === delete 테스트 ===

  @Test
  @DisplayName("사용자 상태 삭제 성공")
  void deleteUserStatus_Success() {
    // given
    UUID userStatusId = UUID.randomUUID();
    given(userStatusRepository.existsById(userStatusId)).willReturn(true);

    // when
    basicUserStatusService.delete(userStatusId);

    // then
    then(userStatusRepository).should().existsById(userStatusId);
    then(userStatusRepository).should().deleteById(userStatusId);
  }

  @Test
  @DisplayName("사용자 상태 삭제 실패 - 존재하지 않는 ID")
  void deleteUserStatus_Fail_NotFound() {
    // given
    UUID nonExistentId = UUID.randomUUID();
    given(userStatusRepository.existsById(nonExistentId)).willReturn(false);

    // when & then
    assertThatThrownBy(() -> basicUserStatusService.delete(nonExistentId))
        .isInstanceOf(InvalidUserStatusException.class);

    then(userStatusRepository).should().existsById(nonExistentId);
    then(userStatusRepository).should(never()).deleteById(any());
  }

  // === 헬퍼 메서드 ===

  /**
   * 리플렉션을 사용하여 엔티티의 ID를 설정하는 헬퍼 메서드
   */
  private void setEntityId(Object entity, UUID id) {
    try {
      // BaseEntity의 protected setId 메서드를 사용
      Field idField = entity.getClass().getSuperclass().getDeclaredField("id");
      idField.setAccessible(true);
      idField.set(entity, id);
    } catch (NoSuchFieldException e) {
      // BaseUpdateableEntity인 경우 한 단계 더 올라가서 찾기
      try {
        Field idField = entity.getClass().getSuperclass().getSuperclass().getDeclaredField("id");
        idField.setAccessible(true);
        idField.set(entity, id);
      } catch (Exception ex) {
        throw new RuntimeException("ID 설정 실패", ex);
      }
    } catch (Exception e) {
      throw new RuntimeException("ID 설정 실패", e);
    }
  }
}
