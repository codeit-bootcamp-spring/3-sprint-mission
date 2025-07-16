package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentDto;
import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentResponseDto;
import com.sprint.mission.discodeit.dto.user.UserRequestDto;
import com.sprint.mission.discodeit.dto.user.UserResponseDto;
import com.sprint.mission.discodeit.dto.user.UserUpdateDto;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.exception.user.DuplicateEmailException;
import com.sprint.mission.discodeit.exception.user.DuplicateNameException;
import com.sprint.mission.discodeit.exception.user.NotFoundUserException;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.mapper.struct.BinaryContentStructMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.not;
import static org.assertj.core.api.AssertionsForClassTypes.catchThrowable;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("User 서비스 단위 테스트")
class BasicUserServiceTest {

    @InjectMocks
    private BasicUserService userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserStatusRepository userStatusRepository;

    @Mock
    private BinaryContentRepository binaryContentRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private BinaryContentStorage binaryContentStorage;

    @Mock
    private BinaryContentStructMapper binaryContentMapper;

    @Test
    @DisplayName("정상적인 User 생성 시 올바른 비즈니스 로직이 수행되어야 한다.")
    void givenValidRequestAndProfile_whenCreateUser_thenCreateSuccessfully() {

        // given
        String username = "test";
        String email = "test@test.com";
        String password = "pwd1234";
        UserRequestDto request = new UserRequestDto(username, email, password);
        byte[] imageBytes = new byte[]{1, 2, 3};

        BinaryContentDto binaryContentDto = new BinaryContentDto("profile.png", 3L,
                "image/png", imageBytes);

        BinaryContent binaryContent = new BinaryContent("profile.png", 3L, "image/png");

        UUID profileId = UUID.randomUUID();
        ReflectionTestUtils.setField(binaryContent, "id", profileId);

        User user = User.builder()
                .username(username)
                .email(email)
                .password(password)
                .build();

        UserStatus userStatus = UserStatus.builder()
                .user(user)
                .lastActiveAt(Instant.now())
                .build();

        UUID userId = UUID.randomUUID();
        UUID userStatusId = UUID.randomUUID();

        ReflectionTestUtils.setField(user, "id", userId);
        ReflectionTestUtils.setField(userStatus, "id", userStatusId);

        BinaryContentResponseDto profile = new BinaryContentResponseDto(profileId, "profile.png", 3L,
                "image/png");

        UserResponseDto response = new UserResponseDto(userId, username, email, profile, null);

        given(binaryContentMapper.toEntity(binaryContentDto)).willReturn(binaryContent);
        given(userRepository.save(any(User.class))).willReturn(user);
        given(userMapper.toDto(user)).willReturn(response);
        given(userRepository.existsByUsername(username)).willReturn(false);
        given(userRepository.existsByEmail(email)).willReturn(false);

        // when
        UserResponseDto result = userService.create(request, binaryContentDto);

        // then
        assertNotNull(result);
        assertEquals(username, result.username());
        assertEquals(email, result.email());
        assertEquals(profile, result.profile());
        verify(userStatusRepository).save(any(UserStatus.class));
        verify(binaryContentRepository).save(binaryContent);
        verify(binaryContentStorage).put(profileId, imageBytes);
    }

    @Test
    @DisplayName("중복된 username이 존재하는 경우 DuplicateNameException이 발생해야 한다.")
    void givenDuplicateUsername_whenCreateUser_thenThrowDuplicateNameException() {

        // given
        String username = "test";
        String email = "test@test.com";
        String password = "pwd1234";

        UserRequestDto request = new UserRequestDto(username, email, password);
        given(userRepository.existsByUsername(username)).willReturn(true);

        // when
        Throwable thrown = catchThrowable(() -> userService.create(request, null));

        // then
        assertThat(thrown)
                .isInstanceOf(DuplicateNameException.class)
                .hasMessageContaining("존재");
        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("중복된 email이 존재하는 경우 DuplicateEmailException이 발생해야 한다.")
    void givenDuplicateEmail_whenCreateUser_thenThrowDuplicateEmailException() {

        // given
        String username = "test";
        String email = "test@test.com";
        String password = "pwd1234";

        UserRequestDto request = new UserRequestDto(username, email, password);
        given(userRepository.existsByEmail(email)).willReturn(true);

        // when
        Throwable thrown = catchThrowable(() -> userService.create(request, null));

        // then
        assertThat(thrown)
                .isInstanceOf(DuplicateEmailException.class)
                .hasMessageContaining("존재");
        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("등록된 사용자 ID로 조회하면 올바르게 조회되어야 한다.")
    void givenRegisteredUserId_whenFindById_thenReturnUserResponseDto() {

        // given
        String username = "test";
        String email = "test@test.com";
        String password = "pwd1234";
        UUID userId = UUID.randomUUID();

        User user = User.builder()
                .username(username)
                .email(email)
                .password(password)
                .build();

        ReflectionTestUtils.setField(user, "id", userId);

        UserStatus userStatus = UserStatus.builder()
                .user(user)
                .lastActiveAt(Instant.now())
                .build();

        UserResponseDto expectedUser = new UserResponseDto(userId, username, email,
                null, null);

        given(userRepository.findById(userId)).willReturn(Optional.of(user));
        given(userStatusRepository.findByUserId(userId)).willReturn(Optional.of(userStatus));
        given(userMapper.toDto(user)).willReturn(expectedUser);

        // when
        UserResponseDto actualUser = userService.findById(userId);

        // then
        assertEquals(expectedUser, actualUser);
        verify(userRepository).findById(userId);
        verify(userStatusRepository).findByUserId(userId);
        verify(userMapper).toDto(user);
    }

    @Test
    @DisplayName("등록되지 않은 ID로 사용자를 조회하면 NotFoundUserException이 발생해야한다.")
    void givenNonexistentUserId_whenFindById_thenThrowNotFoundUserException() {

        // given
        UUID notExistId = UUID.randomUUID();
        given(userRepository.findById(notExistId)).willReturn(Optional.empty());

        // when
        Throwable thrown = catchThrowable(() -> userService.findById(notExistId));

        // then
        assertThat(thrown)
                .isInstanceOf(NotFoundUserException.class)
                .hasMessageContaining("사용자");
        verify(userRepository).findById(notExistId);
        verifyNoInteractions(userStatusRepository, userMapper); // 사용자 없으면 이후 로직 없어야 함
    }

    @Test
    @DisplayName("정상적인 정보로 사용자를 업데이트하면 올바른 비즈니스 로직이 수행되어야 한다.")
    void givenValidUpdateRequest_whenUpdateUser_thenUpdateSuccessfully() {

        // given
        UUID userId = UUID.randomUUID();
        String newUsername = "test2";
        String newEmail = "test2@test.com";
        String newPassword = "pwd12345";

        BinaryContent oldProfile = new BinaryContent("old.png", 2L,
                "image/png");
        UUID oldProfileId = UUID.randomUUID();
        ReflectionTestUtils.setField(oldProfile, "id", oldProfileId);

        UserUpdateDto updateRequest = new UserUpdateDto(newUsername, newEmail, newPassword);

        User existingUser = User.builder()
                .username("test")
                .email("test@test.com")
                .password("pwd1234")
                .profile(oldProfile)
                .build();

        ReflectionTestUtils.setField(existingUser, "id", userId);

        User updatedUser = User.builder()
                .username(newUsername)
                .email(newEmail)
                .password(newPassword)
                .build();

        ReflectionTestUtils.setField(updatedUser, "id", userId);

        UserResponseDto expectedResponse = new UserResponseDto(
                userId, newUsername, newEmail, null, null);

        given(userRepository.findById(userId)).willReturn(Optional.of(existingUser));
        given(userRepository.findByUsername(newUsername)).willReturn(Optional.empty());
        given(userRepository.findByEmail(newEmail)).willReturn(Optional.empty());
        given(userRepository.save(any(User.class))).willReturn(updatedUser);
        given(userMapper.toDto(any(User.class))).willReturn(expectedResponse);

        // when
        UserResponseDto result = userService.update(userId, updateRequest, null);

        // then
        assertNotNull(result);
        assertEquals(expectedResponse, result);
        verify(userRepository).save(any(User.class));
        verify(binaryContentRepository).deleteById(oldProfileId); // 기존 프로필 사진 정보 삭제
    }

    @Test
    @DisplayName("이미 존재하는 username으로 변경을 시도하면 DuplicateNameException이 발생해야한다.")
    void givenDuplicateUsername_whenUpdateUser_thenThrowDuplicateNameException() {

        // given
        UUID userId = UUID.randomUUID();
        String existName = "existName";

        User targetUser = User.builder()
                .username("test")
                .email("test@test.com")
                .password("pwd1234")
                .build();

        User existingUser = User.builder()
                .username("existName")
                .email("exist@test.com")
                .password("pwd12")
                .build();

        ReflectionTestUtils.setField(targetUser, "id", userId);
        ReflectionTestUtils.setField(existingUser, "id", UUID.randomUUID());

        UserUpdateDto updateRequest = new UserUpdateDto(existName, "test@test.com", "pwd1234");

        given(userRepository.findById(userId)).willReturn(Optional.of(targetUser));
        given(userRepository.findByUsername(existName)).willReturn(Optional.of(existingUser));

        // when
        Throwable thrown = catchThrowable(() -> userService.update(userId, updateRequest, null));

        // then
        assertThat(thrown)
                .isInstanceOf(DuplicateNameException.class)
                .hasMessageContaining("존재");
        verify(userRepository).findById(userId);
        verify(userRepository).findByUsername(existName);
        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("이미 존재하는 email로 변경을 시도하면 DuplicateEmailException이 발생해야한다.")
    void givenDuplicateEmail_whenUpdateUser_thenThrowDuplicateEmailException() {

        // given
        UUID userId = UUID.randomUUID();
        String existEmail = "exist@exist.com";

        User targetUser = User.builder()
                .username("test")
                .email("test@test.com")
                .password("pwd1234")
                .build();

        User existingUser = User.builder()
                .username("existName")
                .email("exist@exist.com")
                .password("pwd12")
                .build();

        ReflectionTestUtils.setField(targetUser, "id", userId);
        ReflectionTestUtils.setField(existingUser, "id", UUID.randomUUID());

        UserUpdateDto updateRequest = new UserUpdateDto("test", existEmail,
                "pwd1234");

        given(userRepository.findById(userId)).willReturn(Optional.of(targetUser));
        given(userRepository.findByEmail(existEmail)).willReturn(Optional.of(existingUser));

        // when
        Throwable thrown = catchThrowable(() -> userService.update(userId, updateRequest, null));

        // then
        assertThat(thrown)
                .isInstanceOf(DuplicateEmailException.class)
                .hasMessageContaining("존재");
        verify(userRepository).findById(userId);
        verify(userRepository).findByEmail(existEmail);
        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("등록된 사용자 ID로 삭제하면 관련 정보가 모두 삭제되어야 한다.")
    void givenRegisteredUserId_whenDeleteUser_thenDeleteUserAndRelatedInfo() {

        // given
        UUID userId = UUID.randomUUID();
        BinaryContent profileImage = new BinaryContent("profile.jpg", 3L,
                "image/jpeg");
        ReflectionTestUtils.setField(profileImage, "id", UUID.randomUUID());

        User user = User.builder()
                .username("test")
                .email("test@test.com")
                .password("pwd1234")
                .profile(profileImage)
                .build();

        ReflectionTestUtils.setField(user, "id", userId);

        given(userRepository.findById(userId)).willReturn(Optional.of(user));

        // when
        userService.deleteById(userId);

        // then
        verify(userRepository).deleteById(userId);
        verify(userStatusRepository).deleteByUserId(userId);
        verify(binaryContentRepository).deleteById(user.getProfile().getId());
    }

    @Test
    @DisplayName("등록되지 않은 사용자 ID로 삭제하면 NotFoundUserException이 발생해야 한다.")
    void givenNonexistentUserId_whenDeleteUser_thenThrowNotFoundUserException() {

        // given
        UUID notExistId = UUID.randomUUID();
        given(userRepository.findById(notExistId)).willReturn(Optional.empty());

        // when
        Throwable thrown = catchThrowable(() -> userService.deleteById(notExistId));

        // then
        assertThat(thrown)
                .isInstanceOf(NotFoundUserException.class)
                .hasMessageContaining("사용자");
        verify(userRepository).findById(notExistId);
        verifyNoMoreInteractions(userStatusRepository, binaryContentRepository);
    }
}