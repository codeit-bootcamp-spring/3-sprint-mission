package com.sprint.mission.discodeit.service.basic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.sprint.mission.discodeit.common.exception.UserException;
import com.sprint.mission.discodeit.dto.data.UserResponse;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.fixture.BinaryContentFixture;
import com.sprint.mission.discodeit.fixture.UserFixture;
import com.sprint.mission.discodeit.fixture.UserStatusFixture;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class BasicUserServiceTest_mock {

  @Mock
  private UserRepository userRepository;

  @Mock
  private BinaryContentRepository binaryContentRepository;

  @Mock
  private UserStatusRepository userStatusRepository;

  @InjectMocks
  private BasicUserService basicUserService;

  @Nested
  @DisplayName("유저 생성")
  class Create {

    @Test
    @DisplayName("DTO를 이용하여 새로운 사용자를 생성하고 UserStatus를 함께 생성한다.")
    void createUser_withDtoAndUserStatus() {
      // given
      UserCreateRequest request = new UserCreateRequest("test@test.com", "길동쓰", "pwd123", null);
      User savedUser = UserFixture.createCustomUser(request);
      UserStatus savedUserStatus = UserStatusFixture.createValidUserStatus(savedUser.getId());

      when(userRepository.findByEmail(request.email())).thenReturn(Optional.empty());
      when(userRepository.findByName(request.name())).thenReturn(Optional.empty());
      when(userRepository.save(any(User.class))).thenReturn(savedUser);
      when(userStatusRepository.save(any(UserStatus.class))).thenReturn(savedUserStatus);
      when(userStatusRepository.findById(savedUser.getId())).thenReturn(
          Optional.of(savedUserStatus));

      // when
      UserResponse createdUserResponse = basicUserService.createUser(request);

      // then
      assertNotNull(createdUserResponse);
      assertEquals(request.email(), createdUserResponse.email());
      assertEquals(request.name(), createdUserResponse.name());
      assertNotNull(createdUserResponse.id());
      assertTrue(createdUserResponse.isActive());

      verify(userRepository, times(1)).findByEmail(request.email());
      verify(userRepository, times(1)).findByName(request.name());
      verify(userRepository, times(1)).save(any(User.class));
      verify(userStatusRepository, times(1)).save(any(UserStatus.class));
    }

    @Test
    @DisplayName("DTO로 새로운 사용자를 생성하며 선택적으로 프로필 이미지를 같이 등록할 수 있다.")
    void createUser_withDtoAndProfileImage() {
      // given
      BinaryContent profileImage = BinaryContentFixture.createValidProfileImage(UUID.randomUUID());
      UserCreateRequest request = new UserCreateRequest("test@test.com", "길동쓰", "pwd123",
          profileImage);
      User savedUser = UserFixture.createCustomUser(request);
      savedUser.updateProfileImageId(profileImage.getId()); // User 객체에 프로필 이미지 ID 설정
      UserStatus savedUserStatus = UserStatusFixture.createValidUserStatus(savedUser.getId());

      when(userRepository.findByEmail(request.email())).thenReturn(Optional.empty());
      when(userRepository.findByName(request.name())).thenReturn(Optional.empty());
      when(userRepository.save(any(User.class))).thenReturn(savedUser);
      when(userStatusRepository.save(any(UserStatus.class))).thenReturn(savedUserStatus);
      when(userStatusRepository.findById(savedUser.getId())).thenReturn(
          Optional.of(savedUserStatus));

      ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);

      // when
      UserResponse createdUserResponse = basicUserService.createUser(request);

      // then
      assertNotNull(createdUserResponse);
      assertEquals(request.email(), createdUserResponse.email());
      assertEquals(request.name(), createdUserResponse.name());
      assertEquals(profileImage.getId(), createdUserResponse.profileImageId()); // 응답 DTO 확인
      assertNotNull(createdUserResponse.id());
      assertTrue(createdUserResponse.isActive());

      verify(userRepository, times(1)).findByEmail(request.email());
      verify(userRepository, times(1)).findByName(request.name());
      verify(userRepository, times(2)).save(userCaptor.capture()); // User 저장 시 캡처
      assertEquals(profileImage.getId(),
          userCaptor.getValue().getProfileImageId()); // 저장된 User의 profileImageId 확인
      verify(userStatusRepository, times(1)).save(any(UserStatus.class));
    }

    @Test
    @DisplayName("이미 사용 중인 이메일로 사용자를 생성하려 하면 예외가 발생한다.")
    void createUser_duplicateEmail() {
      // given
      UserCreateRequest request = new UserCreateRequest("test@test.com", "길동쓰", "pwd123", null);
      User existingUser = User.create("test@test.com", "새로운 길동쓰", "pwd123");
      when(userRepository.findByEmail(request.email())).thenReturn(Optional.of(existingUser));

      // when & Then
      UserException exception = assertThrows(UserException.class,
          () -> basicUserService.createUser(request));
      assertEquals(UserException.DUPLICATE_EMAIL, exception.getErrorCode());

      verify(userRepository, times(1)).findByEmail(request.email());
      verify(userRepository, never()).findByName(anyString());
      verify(userRepository, never()).save(any(User.class));
      verify(userStatusRepository, never()).save(any(UserStatus.class));
      verify(binaryContentRepository, never()).save(any(BinaryContent.class));
    }

    @Test
    @DisplayName("이미 사용 중인 username으로 사용자를 생성하려 하면 예외가 발생한다.")
    void createUser_duplicateUsername() {
      // given
      UserCreateRequest request = new UserCreateRequest("test@test.com", "길동쓰", "pwd123", null);
      User existingUser = User.create("test1@test.com", "길동쓰", "pwd123");
      when(userRepository.findByEmail(request.email())).thenReturn(Optional.empty());
      when(userRepository.findByName(request.name())).thenReturn(Optional.of(existingUser));

      // when & Then
      UserException exception = assertThrows(UserException.class,
          () -> basicUserService.createUser(request));
      assertEquals(UserException.DUPLICATE_NAME, exception.getErrorCode());

      verify(userRepository, times(1)).findByEmail(request.email());
      verify(userRepository, times(1)).findByName(request.name());
      verify(userRepository, never()).save(any(User.class));
      verify(userStatusRepository, never()).save(any(UserStatus.class));
      verify(binaryContentRepository, never()).save(any(BinaryContent.class));
    }
  }

  @Nested
  @DisplayName("유저 삭제")
  class Delete {

    @Test
    @DisplayName("프로필 이미지가 있는 사용자를 삭제하면 관련 BinaryContent와 UserStatus도 함께 삭제해야 한다.")
    void deleteUser_shouldDeleteRelatedEntities() {
      // given
      UUID userId = UUID.randomUUID();
      User userToDelete = UserFixture.createCustomUser(
          new UserCreateRequest("test@test.com", "길동쓰", "pwd123",
              BinaryContentFixture.createValidProfileImage(userId)));

      when(userRepository.findById(userId)).thenReturn(Optional.of(userToDelete));

      // when
      basicUserService.deleteUser(userId);

      // then
      verify(userRepository, times(1)).deleteById(userId);
      verify(binaryContentRepository, times(1)).deleteById(userToDelete.getProfileImageId());
      verify(userStatusRepository, times(1)).deleteById(userId);
    }

    @Test
    @DisplayName("프로필 이미지가 없는 사용자를 삭제해도 관련 UserStatus는 삭제해야 한다.")
    void deleteUser_shouldDeleteUserStatusEvenWithoutProfileImage() {
      // given
      UUID userId = UUID.randomUUID();
      User userToDelete = UserFixture.createValidUser();

      when(userRepository.findById(userId)).thenReturn(Optional.of(userToDelete));

      // when
      basicUserService.deleteUser(userId);

      // then
      verify(userRepository, times(1)).deleteById(userId);
      verify(userStatusRepository, times(1)).deleteById(userId);
    }

    @Test
    @DisplayName("존재하지 않는 사용자를 삭제하려고 하면 아무 작업도 수행하지 않아야 한다.")
    void deleteUser_shouldDoNothingIfUserNotFound() {
      // given
      UUID nonExistingUserId = UUID.randomUUID();
      when(userRepository.findById(nonExistingUserId)).thenReturn(Optional.empty());

      // when
      basicUserService.deleteUser(nonExistingUserId);

      // then
      verify(userRepository, never()).deleteById(any());
      verify(binaryContentRepository, never()).deleteById(any());
      verify(userStatusRepository, never()).deleteById(any());
    }
  }
}