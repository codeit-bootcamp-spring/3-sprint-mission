package com.sprint.mission.discodeit.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.response.UserResponse;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.exception.UserException;
import com.sprint.mission.discodeit.fixture.BinaryContentFixture;
import com.sprint.mission.discodeit.fixture.UserFixture;
import com.sprint.mission.discodeit.fixture.UserStatusFixture;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.basic.BasicUserService;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class BasicUserServiceTest {

  @Mock
  private UserRepository userRepository;

  @Mock
  private BinaryContentRepository binaryContentRepository;

  @Mock
  private UserStatusRepository userStatusRepository;

  @InjectMocks
  private BasicUserService basicUserService;

  @Nested
  class Create {

    @Test
    void DTO를_이용하여_새로운_사용자를_생성하고_UserStatus를_함께_생성한다() {
      // given
      UserCreateRequest request = new UserCreateRequest("test@test.com", "길동쓰", "pwd123", null);
      User savedUser = UserFixture.createCustomUser(request);
      UserStatus savedUserStatus = UserStatusFixture.createValidUserStatus(savedUser.getId());

      when(userRepository.findByEmail(request.email())).thenReturn(Optional.empty());
      when(userRepository.findByName(request.name())).thenReturn(Optional.empty());
      when(userRepository.save(any(User.class))).thenReturn(savedUser);
      when(userStatusRepository.save(any(UserStatus.class))).thenReturn(savedUserStatus);

      // when
      UserResponse createdUserResponse = basicUserService.create(request);

      // then
      assertNotNull(createdUserResponse);
      assertEquals(request.email(), createdUserResponse.email());
      assertEquals(request.name(), createdUserResponse.name());
      assertNotNull(createdUserResponse.id());

      verify(userRepository, times(1)).findByEmail(request.email());
      verify(userRepository, times(1)).findByName(request.name());
      verify(userRepository, times(1)).save(any(User.class));
      verify(userStatusRepository, times(1)).save(any(UserStatus.class));
    }

    @Test
    void DTO로_새로운_사용자를_생성하며_선택적으로_프로필_이미지를_같이_등록할_수_있다() {
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

      ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);

      // when
      UserResponse createdUserResponse = basicUserService.create(request);

      // then
      assertNotNull(createdUserResponse);
      assertEquals(request.email(), createdUserResponse.email());
      assertEquals(request.name(), createdUserResponse.name());
      assertEquals(profileImage.getId(), createdUserResponse.profileImageId()); // 응답 DTO 확인
      assertNotNull(createdUserResponse.id());

      verify(userRepository, times(1)).findByEmail(request.email());
      verify(userRepository, times(1)).findByName(request.name());
      verify(userRepository, times(2)).save(userCaptor.capture()); // User 저장 시 캡처
      assertEquals(profileImage.getId(),
          userCaptor.getValue().getProfileImageId()); // 저장된 User의 profileImageId 확인
      verify(userStatusRepository, times(1)).save(any(UserStatus.class));
    }

    @Test
    void 이미_사용_중인_이메일로_사용자를_생성하려_하면_예외가_발생한다() {
      // given
      UserCreateRequest request = new UserCreateRequest("test@test.com", "길동쓰", "pwd123", null);
      User existingUser = User.create("test@test.com", "새로운 길동쓰", "pwd123");
      when(userRepository.findByEmail(request.email())).thenReturn(Optional.of(existingUser));

      // when & Then
      UserException exception = assertThrows(UserException.class,
          () -> basicUserService.create(request));
      assertEquals(UserException.DUPLICATE_EMAIL, exception.getErrorCode());

      verify(userRepository, times(1)).findByEmail(request.email());
      verify(userRepository, never()).findByName(anyString());
      verify(userRepository, never()).save(any(User.class));
      verify(userStatusRepository, never()).save(any(UserStatus.class));
      verify(binaryContentRepository, never()).save(any(BinaryContent.class));
    }

    @Test
    void 이미_사용_중인_username으로_사용자를_생성하려_하면_예외가_발생한다() {
      // given
      UserCreateRequest request = new UserCreateRequest("test@test.com", "길동쓰", "pwd123", null);
      User existingUser = User.create("test1@test.com", "길동쓰", "pwd123");
      when(userRepository.findByEmail(request.email())).thenReturn(Optional.empty());
      when(userRepository.findByName(request.name())).thenReturn(Optional.of(existingUser));

      // when & Then
      UserException exception = assertThrows(UserException.class,
          () -> basicUserService.create(request));
      assertEquals(UserException.DUPLICATE_NAME, exception.getErrorCode());

      verify(userRepository, times(1)).findByEmail(request.email());
      verify(userRepository, times(1)).findByName(request.name());
      verify(userRepository, never()).save(any(User.class));
      verify(userStatusRepository, never()).save(any(UserStatus.class));
      verify(binaryContentRepository, never()).save(any(BinaryContent.class));
    }
  }

  @Nested
  class Delete {

    @Test
    void 프로필_이미지가_있는_사용자를_삭제하면_관련_도메인_데이터도_함께_삭제해야_한다() {
      // given
      UUID userId = UUID.randomUUID();
      User userToDelete = UserFixture.createCustomUser(
          new UserCreateRequest("test@test.com", "길동쓰", "pwd123",
              BinaryContentFixture.createValidProfileImage(userId)));

      when(userRepository.findById(userId)).thenReturn(Optional.of(userToDelete));

      // when
      basicUserService.delete(userId);

      // then
      verify(userRepository, times(1)).delete(userId);
      verify(binaryContentRepository, times(1)).delete(userToDelete.getProfileImageId());
      verify(userStatusRepository, times(1)).delete(userId);
    }

    @Test
    void 프로필_이미지가_없는_사용자를_삭제해도_관련_UserStatus는_삭제해야_한다() {
      // given
      UUID userId = UUID.randomUUID();
      User userToDelete = UserFixture.createValidUser();

      when(userRepository.findById(userId)).thenReturn(Optional.of(userToDelete));

      // when
      basicUserService.delete(userId);

      // then
      verify(userRepository, times(1)).delete(userId);
      verify(userStatusRepository, times(1)).delete(userId);
    }

    @Test
    void 존재하지_않는_사용자를_삭제하려고_하면_아무_작업도_수행하지_않아야_한다() {
      // given
      UUID nonExistingUserId = UUID.randomUUID();
      when(userRepository.findById(nonExistingUserId)).thenReturn(Optional.empty());

      // when
      basicUserService.delete(nonExistingUserId);

      // then
      verify(userRepository, never()).delete(any());
      verify(binaryContentRepository, never()).delete(any());
      verify(userStatusRepository, never()).delete(any());
    }
  }
}