package com.sprint.mission.discodeit.service;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.mock;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;

import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserUpdateRequest;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.Common.ResourceNotFoundException;
import com.sprint.mission.discodeit.exception.User.UserAlreadyExistException;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.basic.BasicUserService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

@TestMethodOrder(MethodOrderer.MethodName.class)
public class UserServiceTest {

  private UserService userService;
  private UserRepository userRepository = mock(UserRepository.class);
  private BinaryContentRepository binaryContentRepository = mock(BinaryContentRepository.class);
  private BinaryContentStorage binaryContentStorage = mock(BinaryContentStorage.class);
  private UserMapper userMapper = mock(UserMapper.class);

  @BeforeEach
  void setUp() {
    userService = new BasicUserService(userRepository, binaryContentRepository,
        binaryContentStorage, userMapper);
  }

  @AfterEach
  void tearDown() {
    //로그 또는 자원 해제 처리
  }


  @Nested
  @DisplayName("유저 생성 테스트")
  class 유저생성 {

    @DisplayName("프로필 이미지 없는 사용자 생성 성공 후 dto를 반환한다")
    @Test
    void whenCreateUserwithoutProfileImage_thenReturnUserDto() {

      //given
      UserCreateRequest userCreateRequest = new UserCreateRequest("test", "test@naver.com", "1234");
      User user = new User(userCreateRequest.username(), userCreateRequest.email(),
          userCreateRequest.password(), null);
      given(userRepository.existsByUsernameOrEmail(anyString(), anyString())).willReturn(false);
      given(userRepository.save(any(User.class))).willReturn(user);
      UserDto expectedUserDto = new UserDto(UUID.randomUUID(), user.getUsername(), user.getEmail(),
          null, true,
          Instant.now(), Instant.now());
      given(userMapper.toDto(any(User.class))).willReturn(expectedUserDto);

      //when
      UserDto userDto = userService.create(userCreateRequest, Optional.empty());

      // then: 해당 메서드가 특정 인자로 호출되었는지를 검증
      then(userRepository).should().existsByUsernameOrEmail("test", "test@naver.com");
      then(userRepository).should().save(any(User.class));
      then(binaryContentRepository).shouldHaveNoInteractions();
      then(binaryContentStorage).shouldHaveNoInteractions();
      assertAll(
          "User 정보 검증",
          () -> assertEquals(userCreateRequest.email(), userDto.getEmail()),
          () -> assertEquals(userCreateRequest.username(), userDto.getUsername())
      );

    }

    @DisplayName("이미 존재하는 이름과 아이디로 사용자 생성을 시도하여 실패 후 에러를 반환한다")
    @Test
    void whenCreateUserwithExistNameAndEmail_thenThrowUserAlreadyExistException() {

      //given
      UserCreateRequest userCreateRequest = new UserCreateRequest("test", "test@naver.com", "1234");
      User user = new User(userCreateRequest.username(), userCreateRequest.email(),
          userCreateRequest.password(), null);
      given(userRepository.existsByUsernameOrEmail(anyString(), anyString())).willReturn(true);

      //when & then
      assertThrows(UserAlreadyExistException.class,
          () -> userService.create(userCreateRequest, Optional.empty()));

      // then: 해당 메서드가 특정 인자로 호출되었는지를 검증
      then(userRepository).should().existsByUsernameOrEmail("test", "test@naver.com");
      then(binaryContentRepository).shouldHaveNoInteractions();
      then(binaryContentStorage).shouldHaveNoInteractions();
      then(userMapper).shouldHaveNoInteractions();


    }
  }

  @Nested
  @DisplayName("유저 업데이트 테스트")
  class 유저업데이트 {

    @DisplayName("Id값으로 유저 정보를 업데이트 성공한다")
    @Test
    void whenUpdateUserWithId_thenReturnUserDto() {

      //given
      UUID userId = UUID.randomUUID();
      UserUpdateRequest userUpdateRequest = new UserUpdateRequest("newTest", "newTest@naver.com",
          "1234");
      User user = new User("oldName", "oldEmail@mail.com",
          "oldpw", null);
      UserDto expectedUserDto = new UserDto(userId, userUpdateRequest.newUsername(),
          userUpdateRequest.newEmail(),
          null, true,
          Instant.now(), Instant.now());

      given(userRepository.existsByUsernameOrEmail(userUpdateRequest.newUsername(),
          userUpdateRequest.newEmail())).willReturn(false);
      given(userRepository.findById(userId)).willReturn((Optional.of(user)));
      given(userMapper.toDto(any(User.class))).willReturn(expectedUserDto);

      //when
      UserDto userDto = userService.update(userId, userUpdateRequest, Optional.empty());

      // then: 해당 메서드가 특정 인자로 호출되었는지를 검증
      then(userRepository).should().existsByUsernameOrEmail("newTest", "newTest@naver.com");
      then(userRepository).should().findById(userId);
      then(binaryContentRepository).shouldHaveNoInteractions();
      then(binaryContentStorage).shouldHaveNoInteractions();
      assertAll(
          "User 정보 검증",
          () -> assertEquals(userUpdateRequest.newEmail(), user.getEmail()),
          () -> assertEquals(userUpdateRequest.newUsername(), user.getUsername()),
          () -> assertEquals(userUpdateRequest.newEmail(), userDto.getEmail()),
          () -> assertEquals(userUpdateRequest.newUsername(), userDto.getUsername())
      );

    }

    @DisplayName("Id값으로 유저 정보를 업데이트 실패한다")
    @Test
    void whenUpdateUserWithId_thenThrowUserAlreadyExistException() {

      //given
      UUID userId = UUID.randomUUID();
      UserUpdateRequest userUpdateRequest = new UserUpdateRequest("newTest", "newTest@naver.com",
          "1234");
      User user = new User("oldName", "oldEmail@mail.com",
          "oldpw", null);
      UserDto expectedUserDto = new UserDto(userId, userUpdateRequest.newUsername(),
          userUpdateRequest.newEmail(),
          null, true,
          Instant.now(), Instant.now());

      given(userRepository.existsByUsernameOrEmail(userUpdateRequest.newUsername(),
          userUpdateRequest.newEmail())).willReturn(true);

      //when&then
      assertThrows(UserAlreadyExistException.class, () -> {
        userService.update(userId, userUpdateRequest, Optional.empty());
      });

    }


  }

  @Nested
  @DisplayName("유저 삭제 테스트")
  class 유저삭제 {

    @DisplayName("유저 id로 유저 삭제를 성공한다")
    @Test
    void whenDeleteUser() {
      UUID UserId = UUID.fromString("a1b42ab8-5d3e-4571-af57-422064cb39ff");
      //given
      given(userRepository.existsById(UserId)).willReturn(true);

      //when
      userService.delete(UserId);

      // then: 해당 메서드가 특정 인자로 호출되었는지를 검증
      then(userRepository).should().deleteById(UserId);
    }

    @DisplayName("유저 id로 유저를 삭제할때, 존재하지 않은 id이므로 실패한다")
    @Test
    void whenDeleteUserwithNotExistId_thenThrowUserAlreadyExistException() {
      UUID UserId = UUID.fromString("a1b42ab8-5d3e-4571-af57-422064cb39ff");
      //given
      given(userRepository.existsById(UserId)).willReturn(false);

      //when&then
      assertThrows(ResourceNotFoundException.class, () -> {
        userService.delete(UserId);
      });

      then(userRepository).should(never()).deleteById(any());
    }
  }

}
