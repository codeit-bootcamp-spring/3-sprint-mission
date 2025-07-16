package com.sprint.mission.discodeit.service.basic;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.never;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.times;

import com.sprint.mission.discodeit.dto.data.BinaryContentDto;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserUpdateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.exception.user.DuplicateEmailException;
import com.sprint.mission.discodeit.exception.user.DuplicateUserException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserService 단위 테스트")
class BasicUserServiceTest {

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

    @InjectMocks
    private BasicUserService userService;

    @BeforeEach
    void setUp() {
    }

    @Test
    @DisplayName("사용자 생성 - 성공(프로필 이미지 있음)")
    void create_Success() {
        // Given
        String username = "조현아";
        String email = "zzo@gmail.com";
        String password = "password123!";
        UUID userId = UUID.randomUUID();

        byte[] profileBytes = "testProfileImage".getBytes();
        BinaryContent binaryContent = new BinaryContent("testProfileImage",
            (long) profileBytes.length,
            "png");
        BinaryContentCreateRequest binaryContentCreateRequest = new BinaryContentCreateRequest(
            "testProfileImage", "png",
            profileBytes);

        Optional<BinaryContentCreateRequest> optionalProfile = Optional.of(
            binaryContentCreateRequest);
        BinaryContentDto testProfileDto = new BinaryContentDto(UUID.randomUUID(),
            "testProfileImage", (long) profileBytes.length, "png");

        UserCreateRequest request = new UserCreateRequest(username, email, password);
        User user = new User(request.username(), request.email(), request.password(),
            binaryContent);

        UserDto userDto = new UserDto(userId, request.username(),
            request.email(), testProfileDto, null);
        UserStatus userStatus = new UserStatus(user, Instant.now());

        given(userRepository.existsByEmail(request.email())).willReturn(false);
        given(userRepository.existsByUsername(request.username())).willReturn(false);
        given(binaryContentRepository.save(any(BinaryContent.class))).willReturn(binaryContent);
        given(binaryContentStorage.put(any(), any())).willReturn(userDto.profile().id());
        given(userRepository.save(any(User.class))).willReturn(user);
        given(userStatusRepository.save(any(UserStatus.class))).willReturn(userStatus);
        given(userMapper.toDto(any(User.class))).willReturn(userDto);

        // When
        UserDto result = userService.create(request, optionalProfile);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.username()).isEqualTo(request.username());
        assertThat(result.email()).isEqualTo(request.email());
        assertThat(result.profile().fileName()).isEqualTo("testProfileImage");

        then(binaryContentRepository).should().save(any(BinaryContent.class));
        then(binaryContentStorage).should().put(any(), eq(profileBytes));
        then(userStatusRepository).should().save(any(UserStatus.class));
        then(userRepository).should().save(any(User.class));
    }

    @Test
    @DisplayName("사용자 생성 - 실패(이메일 중복)")
    void create_Fail_DuplicateEmail() {
        // Given
        UserCreateRequest userCreateRequest = new UserCreateRequest("조현아", "zzo@email.com",
            "password123!");
        given(userRepository.existsByEmail(userCreateRequest.email())).willReturn(true);

        // When & Then
        assertThatThrownBy(() -> userService.create(userCreateRequest, Optional.empty()))
            .isInstanceOf(DuplicateEmailException.class);

        then(userRepository).should().existsByEmail("zzo@email.com");
        then(userRepository).should(never()).save(any(User.class));
    }

    @Test
    @DisplayName("사용자 생성 - 실패(사용자명 중복)")
    void create_Fail_DuplicateUsername() {
        // Given
        UserCreateRequest userCreateRequest = new UserCreateRequest("조현아", "zzo@email.com",
            "password123!");
        given(userRepository.existsByUsername(userCreateRequest.username())).willReturn(true);

        // When & Then
        assertThatThrownBy(() -> userService.create(userCreateRequest, Optional.empty()))
            .isInstanceOf(DuplicateUserException.class);

        then(userRepository).should().existsByUsername("조현아");
        then(userRepository).should(never()).save(any(User.class));
    }

    @Test
    @DisplayName("사용자 조회 - 성공")
    void find_Success() {
        // Given
        UUID userId = UUID.randomUUID();
        User user = new User("조현아", "zzo@email.com", "password123!", null);
        ReflectionTestUtils.setField(user, "id", userId);
        UserDto userDto = new UserDto(userId, user.getUsername(), user.getEmail(), null, null);

        given(userRepository.findById(userId)).willReturn(Optional.of(user));
        given(userMapper.toDto(user)).willReturn(userDto);

        // When
        UserDto result = userService.find(userId);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(userId);
        assertThat(result.username()).isEqualTo("조현아");

        then(userRepository).should().findById(userId);
        then(userMapper).should().toDto(user);
    }

    @Test
    @DisplayName("사용자 조회 - 실패(존재하지 않는 사용자)")
    void find_Fail_UserNotFound() {
        // Given
        UUID userId = UUID.randomUUID();
        given(userRepository.findById(userId)).willReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> userService.find(userId))
            .isInstanceOf(UserNotFoundException.class);

        then(userRepository).should().findById(userId);
        then(userMapper).should(never()).toDto(any(User.class));
    }

    @Test
    @DisplayName("전체 사용자 조회 - 성공")
    void findAll_Success() {
        // Given
        User user1 = new User("조현아", "zzo@email.com", "password123!", null);
        User user2 = new User("투현아", "2hyun@email.com", "password123!", null);
        List<User> users = List.of(user1, user2);
        UserDto userDto1 = new UserDto(UUID.randomUUID(), "조현아", "zzo@email.com", null, null);
        UserDto userDto2 = new UserDto(UUID.randomUUID(), "투현아", "2hyun@email.com", null, null);

        given(userRepository.findAll()).willReturn(users);
        given(userMapper.toDto(user1)).willReturn(userDto1);
        given(userMapper.toDto(user2)).willReturn(userDto2);

        // When
        List<UserDto> result = userService.findAll();

        // Then
        assertThat(result).hasSize(2);
        assertThat(result).isNotEmpty();

        then(userRepository).should().findAll();
        then(userMapper).should(times(2)).toDto(any(User.class));
    }

    @Test
    @DisplayName("사용자 수정 - 성공")
    void update_Success() {
        // Given
        UUID userId = UUID.randomUUID();
        User user = new User("조현아", "zzo@email.com", "password123!", null);
        ReflectionTestUtils.setField(user, "id", userId);

        UserUpdateRequest testUpdateRequest = new UserUpdateRequest("뉴현아", "zzo@email.com",
            "newPassword123");
        User updateUser = new User("뉴현아", "zzo@email.com", "newPassword123!", null);
        UserDto testUpdateUserDto = new UserDto(
            userId,
            updateUser.getUsername(),
            updateUser.getEmail(),
            null, null
        );

        given(userRepository.existsByEmail(testUpdateRequest.newEmail())).willReturn(false);
        given(userRepository.existsByUsername(testUpdateRequest.newUsername())).willReturn(false);
        given(userRepository.findById(userId)).willReturn(Optional.of(updateUser));
        given(userMapper.toDto(any(User.class))).willReturn(testUpdateUserDto);

        // When
        UserDto result = userService.update(userId, testUpdateRequest, Optional.empty());

        // Then
        assertThat(result.id()).isEqualTo(userId);
        assertThat(result.username()).isEqualTo("뉴현아");
        assertThat(result.email()).isEqualTo("zzo@email.com");

        then(userRepository).should().findById(userId);
        then(userRepository).should().existsByEmail(testUpdateRequest.newEmail());
        then(userRepository).should().existsByUsername(testUpdateRequest.newUsername());
        then(userMapper).should().toDto(updateUser);
    }

    @Test
    @DisplayName("사용자 수정 - 실패(이메일 중복)")
    void update_Fail_DuplicateEmail() {
        // Given
        UUID userId = UUID.randomUUID();
        UserUpdateRequest testUpdateRequest = new UserUpdateRequest("뉴현아", "zzo@email.com",
            "newPassword123");
        Optional<BinaryContentCreateRequest> emptyProfile = Optional.empty();
        User user = new User("뉴현아", "zzo@email.com", "newPassword123!", null);
        ReflectionTestUtils.setField(user, "id", userId);

        given(userRepository.findById(userId)).willReturn(Optional.of(user));
        given(userRepository.existsByEmail(testUpdateRequest.newEmail())).willReturn(true);

        // When & Then
        assertThatThrownBy(() -> userService.update(userId, testUpdateRequest, emptyProfile))
            .isInstanceOf(DuplicateEmailException.class);

        then(userRepository).should().findById(userId);
        then(userRepository).should().existsByEmail("zzo@email.com");
    }

    @Test
    @DisplayName("사용자 삭제 - 성공")
    void delete_Success() {
        // Given
        UUID userId = UUID.randomUUID();
        User user = new User("조현아", "zzo@gmail.com", "password123!", null);
        ReflectionTestUtils.setField(user, "id", userId);
        given(userRepository.findById(userId)).willReturn(Optional.of(user));

        // When
        userService.delete(userId);

        // Then
        then(userRepository).should().findById(userId);
        then(userStatusRepository).should().deleteByUserId(userId);
        then(userRepository).should().deleteById(userId);
    }

    @Test
    @DisplayName("사용자 삭제 - 실패(존재하지 않는 사용자)")
    void delete_Fail_UserNotFound() {
        // Given
        UUID userId = UUID.randomUUID();
        given(userRepository.findById(userId)).willReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> userService.delete(userId))
            .isInstanceOf(UserNotFoundException.class);

        then(userRepository).should().findById(userId);
        then(userRepository).should(never()).deleteById(any());
        then(userStatusRepository).should(never()).deleteByUserId(any());
    }

    @Test
    @DisplayName("사용자 삭제 - 성공(프로필 이미지 있음)")
    void delete_Success_WithProfile() {
        // Given
        UUID userId = UUID.randomUUID();
        byte[] profileBytes = "testProfileImage".getBytes();

        BinaryContent binaryContent = new BinaryContent(
            "testProfileImage",
            (long) profileBytes.length,
            "png");

        User user = new User("조현아", "zzo@gmail.com", "password123!", binaryContent);
        ReflectionTestUtils.setField(user, "id", userId);

        given(userRepository.findById(userId)).willReturn(Optional.of(user));

        // When
        userService.delete(userId);

        // Then
        then(userRepository).should().findById(userId);
        then(binaryContentRepository).should().delete(binaryContent);
        then(userStatusRepository).should().deleteByUserId(userId);
        then(userRepository).should().deleteById(userId);
    }
}