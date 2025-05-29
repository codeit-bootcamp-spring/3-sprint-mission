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
import com.sprint.mission.discodeit.exception.ErrorCode;
import com.sprint.mission.discodeit.exception.UserException;
import com.sprint.mission.discodeit.fixture.BinaryContentFixture;
import com.sprint.mission.discodeit.fixture.UserFixture;
import com.sprint.mission.discodeit.fixture.UserStatusFixture;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.basic.BasicUserService;
import com.sprint.mission.discodeit.service.command.CreateUserCommand;
import com.sprint.mission.discodeit.vo.BinaryContentData;
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
    void 새로운_사용자를_생성하고_UserStatus를_함께_생성한다() {
      String email = "test@test.com";
      String name = "길동쓰";
      String password = "pwd123";
      CreateUserCommand command = new CreateUserCommand(email, name,
          password, null);

      User savedUser = UserFixture.createCustomUserWithId(email, name, password, null);
      UserStatus savedUserStatus = UserStatusFixture.createWithId(savedUser);

      when(userRepository.findByEmail(email)).thenReturn(Optional.empty());
      when(userRepository.findByUsername(name)).thenReturn(Optional.empty());
      when(userRepository.save(any(User.class))).thenReturn(savedUser);
      when(userStatusRepository.save(any(UserStatus.class))).thenReturn(savedUserStatus);

      UserResponse createdUserResponse = basicUserService.create(command);

      assertNotNull(createdUserResponse);
      assertEquals(email, createdUserResponse.email());
      assertEquals(name, createdUserResponse.username());
      assertNotNull(createdUserResponse.id());

      verify(userRepository).findByEmail(email);
      verify(userRepository).findByUsername(name);
      verify(userRepository).save(any(User.class));
      verify(userStatusRepository).save(any(UserStatus.class));
    }

    @Test
    void 새로운_사용자를_생성하며_프로필_이미지를_같이_등록할_수_있다() {
      String email = "test@test.com";
      String name = "길동쓰";
      String password = "pwd123";
      BinaryContent binaryContent = BinaryContentFixture.createValid();
      BinaryContentData profile = new BinaryContentData(
          binaryContent.getFileName(),
          binaryContent.getContentType(),
          binaryContent.getBytes()
      );
      CreateUserCommand command = new CreateUserCommand(email, name, password, profile);

      User savedUser = UserFixture.createCustomUserWithId(email, name, password, binaryContent);
      savedUser.updateProfile(binaryContent);
      UserStatus savedUserStatus = UserStatusFixture.createWithId(savedUser);

      when(userRepository.findByEmail(email)).thenReturn(Optional.empty());
      when(userRepository.findByUsername(name)).thenReturn(Optional.empty());
      when(userRepository.save(any(User.class))).thenReturn(savedUser);
      when(userStatusRepository.save(any(UserStatus.class))).thenReturn(savedUserStatus);
      when(binaryContentRepository.save(any(BinaryContent.class))).thenReturn(binaryContent);

      ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);

      UserResponse createdUserResponse = basicUserService.create(command);

      assertNotNull(createdUserResponse);
      assertEquals(email, createdUserResponse.email());
      assertEquals(name, createdUserResponse.username());
      assertEquals(binaryContent.getId(), createdUserResponse.profile().id());
      assertNotNull(createdUserResponse.id());

      verify(userRepository).findByEmail(email);
      verify(userRepository).findByUsername(name);
      verify(userRepository, times(2)).save(userCaptor.capture());
      assertEquals(binaryContent.getId(), userCaptor.getValue().getProfile().getId());
      verify(userStatusRepository).save(any(UserStatus.class));
    }

    @Test
    void 이미_사용_중인_이메일로_사용자를_생성하려_하면_예외가_발생한다() {
      UserCreateRequest request = new UserCreateRequest("test@test.com", "길동쓰", "pwd123");
      CreateUserCommand command = new CreateUserCommand(request.email(), request.username(),
          request.password(), null);

      User existingUser = User.create("test@test.com", "다른사람", "pwd123", null);

      when(userRepository.findByEmail(request.email())).thenReturn(Optional.of(existingUser));

      UserException exception = assertThrows(UserException.class,
          () -> basicUserService.create(command));
      assertEquals(ErrorCode.ALREADY_EXISTS, exception.getErrorCode());

      verify(userRepository).findByEmail(request.email());
      verify(userRepository, never()).findByUsername(anyString());
      verify(userRepository, never()).save(any());
      verify(userStatusRepository, never()).save(any());
      verify(binaryContentRepository, never()).save(any());
    }

    @Test
    void 이미_사용_중인_username으로_사용자를_생성하려_하면_예외가_발생한다() {
      UserCreateRequest request = new UserCreateRequest("test@test.com", "길동쓰", "pwd123");
      CreateUserCommand command = new CreateUserCommand(request.email(), request.username(),
          request.password(), null);

      User existingUser = User.create("다른@test.com", "길동쓰", "pwd123", null);

      when(userRepository.findByEmail(request.email())).thenReturn(Optional.empty());
      when(userRepository.findByUsername(request.username())).thenReturn(Optional.of(existingUser));

      UserException exception = assertThrows(UserException.class,
          () -> basicUserService.create(command));
      assertEquals(ErrorCode.ALREADY_EXISTS, exception.getErrorCode());

      verify(userRepository).findByEmail(request.email());
      verify(userRepository).findByUsername(request.username());
      verify(userRepository, never()).save(any());
      verify(userStatusRepository, never()).save(any());
      verify(binaryContentRepository, never()).save(any());
    }
  }

  @Nested
  class Delete {

    @Test
    void 프로필_이미지가_있는_사용자를_삭제하면_프로필이미지도_함께_삭제한다() {
      BinaryContent profileImage = BinaryContentFixture.createValid();
      User userToDelete = UserFixture.createCustomUser("test@test.com", "길동쓰", "pwd123", null);
      UUID userId = userToDelete.getId();
      UserStatus userStatusToDelete = UserStatusFixture.createValid(userToDelete);
      userToDelete.updateProfile(profileImage);

      when(userRepository.findById(userId)).thenReturn(Optional.of(userToDelete));
      when(userStatusRepository.findByUserId(userId)).thenReturn(Optional.of(userStatusToDelete));

      basicUserService.delete(userId);

      verify(userRepository).deleteById(userId);
      verify(binaryContentRepository).deleteById(userToDelete.getProfile().getId());
      verify(userStatusRepository).deleteById(userStatusToDelete.getId());
    }

    @Test
    void 프로필_이미지가_없는_사용자를_삭제해도_UserStatus는_삭제해야_한다() {
      User userToDelete = UserFixture.createValidUser();
      UUID userId = userToDelete.getId();
      UserStatus userStatusToDelete = UserStatusFixture.createValid(userToDelete);

      when(userRepository.findById(userId)).thenReturn(Optional.of(userToDelete));
      when(userStatusRepository.findByUserId(userId)).thenReturn(Optional.of(userStatusToDelete));

      basicUserService.delete(userId);

      verify(userRepository).deleteById(userId);
      verify(userStatusRepository).deleteById(userStatusToDelete.getId());
      verify(binaryContentRepository, never()).deleteById(any());
    }

    @Test
    void 존재하지_않는_사용자를_삭제하려고_하면_예외() {
      UUID userId = UUID.randomUUID();
      when(userRepository.findById(userId)).thenReturn(Optional.empty());

      assertThrows(UserException.class, () -> basicUserService.delete(userId));

      verify(userRepository, never()).deleteById(any());
      verify(binaryContentRepository, never()).deleteById(any());
      verify(userStatusRepository, never()).deleteById(any());
    }
  }
}
