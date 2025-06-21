package com.sprint.mission.discodeit.service.user;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;
import static org.mockito.Mockito.*;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.response.BinaryContentResponse;
import com.sprint.mission.discodeit.dto.response.UserResponse;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.exception.ErrorCode;
import com.sprint.mission.discodeit.exception.user.DuplicateEmailException;
import com.sprint.mission.discodeit.exception.user.DuplicateNameException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.fixture.BinaryContentFixture;
import com.sprint.mission.discodeit.fixture.UserFixture;
import com.sprint.mission.discodeit.fixture.UserStatusFixture;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.basic.BasicUserService;
import com.sprint.mission.discodeit.service.command.CreateUserCommand;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import com.sprint.mission.discodeit.vo.BinaryContentData;

@ExtendWith(MockitoExtension.class)
class BasicUserServiceTest {

  @Mock
  private UserRepository userRepository;

  @Mock
  private BinaryContentRepository binaryContentRepository;

  @Mock
  private UserStatusRepository userStatusRepository;

  @Mock
  private UserMapper userMapper;

  @Mock
  private BinaryContentStorage binaryContentStorage;

  @InjectMocks
  private BasicUserService basicUserService;

  @BeforeEach
  void setUp() {
    Mockito.lenient().when(userMapper.toResponse(any(User.class)))
        .thenAnswer(invocation -> {
          User u = invocation.getArgument(0);

          // user의 profile 엔티티를 DTO로 변환하거나 null 처리
          var profileEntity = u.getProfile(); // BinaryContent or null
          var profileResponse = (profileEntity == null) ? null
              : new BinaryContentResponse(
                  profileEntity.getId(),
                  profileEntity.getFileName(),
                  profileEntity.getContentType(),
                  profileEntity.getSize());

          return new UserResponse(
              u.getId(),
              u.getUsername(),
              u.getEmail(),
              profileResponse,
              false);
        });
  }

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

      given(userRepository.findByEmail(email)).willReturn(Optional.empty());
      given(userRepository.findByUsername(name)).willReturn(Optional.empty());
      given(userRepository.save(any(User.class))).willReturn(savedUser);
      given(userStatusRepository.save(any(UserStatus.class))).willReturn(savedUserStatus);

      UserResponse createdUserResponse = basicUserService.create(command);

      assertNotNull(createdUserResponse);
      assertEquals(email, createdUserResponse.email());
      assertEquals(name, createdUserResponse.username());
      assertNotNull(createdUserResponse.id());

      then(userRepository).should().findByEmail(email);
      then(userRepository).should().findByUsername(name);
      then(userRepository).should().save(any(User.class));
      then(userStatusRepository).should().save(any(UserStatus.class));
    }

    @Test
    void 새로운_사용자를_생성하며_프로필_이미지를_같이_등록할_수_있다() {
      byte[] mockBytes = BinaryContentFixture.getDefaultData();

      String email = "test@test.com";
      String name = "길동쓰";
      String password = "pwd123";
      BinaryContent binaryContent = BinaryContentFixture.createValid();
      BinaryContentData profile = new BinaryContentData(
          binaryContent.getFileName(),
          binaryContent.getContentType(),
          mockBytes);
      binaryContent.assignIdForTest(binaryContent.getId());
      CreateUserCommand command = new CreateUserCommand(email, name, password, profile);

      User savedUser = UserFixture.createCustomUserWithId(email, name, password, binaryContent);
      savedUser.updateProfile(binaryContent);
      UserStatus savedUserStatus = UserStatusFixture.createWithId(savedUser);

      given(userRepository.findByEmail(email)).willReturn(Optional.empty());
      given(userRepository.findByUsername(name)).willReturn(Optional.empty());
      given(userRepository.save(any(User.class))).willReturn(savedUser);
      given(userStatusRepository.save(any(UserStatus.class))).willReturn(savedUserStatus);
      given(binaryContentRepository.save(any(BinaryContent.class))).willReturn(binaryContent);

      ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);

      UserResponse createdUserResponse = basicUserService.create(command);

      assertNotNull(createdUserResponse);
      assertEquals(email, createdUserResponse.email());
      assertEquals(name, createdUserResponse.username());
      assertEquals(binaryContent.getId(), createdUserResponse.profile().id());
      assertNotNull(createdUserResponse.id());

      then(userRepository).should().findByEmail(email);
      then(userRepository).should().findByUsername(name);
      then(userRepository).should(times(2)).save(userCaptor.capture());
      then(userStatusRepository).should().save(any(UserStatus.class));
    }

    @Test
    void 이미_사용_중인_이메일로_사용자를_생성하려_하면_예외가_발생한다() {
      UserCreateRequest request = new UserCreateRequest("test@test.com", "길동쓰", "pwd123");
      CreateUserCommand command = new CreateUserCommand(request.email(), request.username(),
          request.password(), null);

      User existingUser = User.create("test@test.com", "다른사람", "pwd123", null);

      given(userRepository.findByEmail(request.email())).willReturn(Optional.of(existingUser));

      DuplicateEmailException exception = assertThrows(DuplicateEmailException.class,
          () -> basicUserService.create(command));
      assertEquals(ErrorCode.USER_ALREADY_EXISTS, exception.getErrorCode());

      then(userRepository).should().findByEmail(request.email());
      then(userRepository).should(never()).findByUsername(anyString());
      then(userRepository).should(never()).save(any());
      then(userStatusRepository).should(never()).save(any());
      then(binaryContentRepository).should(never()).save(any());
    }

    @Test
    void 이미_사용_중인_username으로_사용자를_생성하려_하면_예외가_발생한다() {
      UserCreateRequest request = new UserCreateRequest("test@test.com", "길동쓰", "pwd123");
      CreateUserCommand command = new CreateUserCommand(request.email(), request.username(),
          request.password(), null);

      User existingUser = User.create("다른@test.com", "길동쓰", "pwd123", null);

      given(userRepository.findByEmail(request.email())).willReturn(Optional.empty());
      given(userRepository.findByUsername(request.username())).willReturn(Optional.of(existingUser));

      DuplicateNameException exception = assertThrows(DuplicateNameException.class,
          () -> basicUserService.create(command));
      assertEquals(ErrorCode.USER_ALREADY_EXISTS, exception.getErrorCode());

      then(userRepository).should().findByEmail(request.email());
      then(userRepository).should().findByUsername(request.username());
      then(userRepository).should(never()).save(any());
      then(userStatusRepository).should(never()).save(any());
      then(binaryContentRepository).should(never()).save(any());
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

      given(userRepository.findById(userId)).willReturn(Optional.of(userToDelete));
      given(userStatusRepository.findByUserId(userId)).willReturn(Optional.of(userStatusToDelete));

      basicUserService.delete(userId);

      then(userRepository).should().deleteById(userId);
      then(binaryContentRepository).should().deleteById(userToDelete.getProfile().getId());
      then(userStatusRepository).should().deleteById(userStatusToDelete.getId());
    }

    @Test
    void 프로필_이미지가_없는_사용자를_삭제해도_UserStatus는_삭제해야_한다() {
      User userToDelete = UserFixture.createValidUser();
      UUID userId = userToDelete.getId();
      UserStatus userStatusToDelete = UserStatusFixture.createValid(userToDelete);

      given(userRepository.findById(userId)).willReturn(Optional.of(userToDelete));
      given(userStatusRepository.findByUserId(userId)).willReturn(Optional.of(userStatusToDelete));

      basicUserService.delete(userId);

      then(userRepository).should().deleteById(userId);
      then(userStatusRepository).should().deleteById(userStatusToDelete.getId());
      then(binaryContentRepository).should(never()).deleteById(any());
    }

    @Test
    void 존재하지_않는_사용자를_삭제하려고_하면_예외() {
      UUID userId = UUID.randomUUID();
      given(userRepository.findById(userId)).willReturn(Optional.empty());

      assertThrows(UserNotFoundException.class, () -> basicUserService.delete(userId));

      then(userRepository).should(never()).deleteById(any());
      then(binaryContentRepository).should(never()).deleteById(any());
      then(userStatusRepository).should(never()).deleteById(any());
    }
  }
}
