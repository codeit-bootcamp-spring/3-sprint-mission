package com.sprint.mission.discodeit.service;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;

import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.exception.user.UserEmailAlreadyExistsException;
import com.sprint.mission.discodeit.exception.user.UserNameAlreadyExistsException;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.basic.BasicUserService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserService 단위 테스트")
public class BasicUserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private UserStatusRepository userStatusRepository;

    @Mock
    private BinaryContentRepository binaryContentRepository;

    @Mock
    private BinaryContentStorage binaryContentStorage;

    @InjectMocks
    private BasicUserService userService;

    @Test
    @DisplayName("사용자 생성 성공 테스트")
    void createUserSuccess() {
        // Given
        UserCreateRequest userCreateRequest = new UserCreateRequest("TESTMAN", "test1234@test.com", "44444");
        UUID mockUserId = UUID.randomUUID();

        User mockUser = new User(
            userCreateRequest.username(),
            userCreateRequest.email(),
            userCreateRequest.password(),
            null
        );

        UserDto expectedUserDto = new UserDto(
            mockUserId,
            mockUser.getUsername(),
            mockUser.getEmail(),
            null,
            true
        );

        // UserStatus Mock 추가
        UserStatus mockUserStatus = new UserStatus(mockUser, Instant.now());

        // Repository Mock 설정
        given(userRepository.existsByUsername(userCreateRequest.username())).willReturn(false);
        given(userRepository.existsByEmail(userCreateRequest.email())).willReturn(false);
        given(userRepository.save(any(User.class))).willReturn(mockUser);
        given(userStatusRepository.save(any(UserStatus.class))).willReturn(mockUserStatus); // 추가
        given(userMapper.toDto(any(User.class))).willReturn(expectedUserDto);

        // When
        UserDto result = userService.create(userCreateRequest, Optional.empty());

        // Then
        assertThat(result).isNotNull();
        assertThat(result.username()).isEqualTo("TESTMAN");
        assertThat(result.email()).isEqualTo("test1234@test.com");
        assertThat(result.online()).isTrue();

        // Verify interactions
        verify(userRepository, times(1)).existsByUsername("TESTMAN");
        verify(userRepository, times(1)).existsByEmail("test1234@test.com");
        verify(userRepository, times(1)).save(any(User.class));
        verify(userStatusRepository, times(1)).save(any(UserStatus.class)); // 추가
        verify(userMapper, times(1)).toDto(any(User.class));
    }


    @Test
    @DisplayName("사용자 이름 중복 시 예외 발생 테스트")
    void createUserWithDuplicateUsername() {
        // Given
        UserCreateRequest userCreateRequest = new UserCreateRequest("TESTMAN", "test1234@test.com", "44444");

        given(userRepository.existsByUsername("TESTMAN")).willReturn(true);

        // When & Then
        assertThatThrownBy(() -> userService.create(userCreateRequest, Optional.empty()))
            .isInstanceOf(UserNameAlreadyExistsException.class);

        verify(userRepository, times(1)).existsByUsername("TESTMAN");
        verify(userRepository, times(0)).save(any(User.class));
    }

    @Test
    @DisplayName("이메일 중복 시 예외 발생 테스트")
    void createUserWithDuplicateEmail() {
        // Given
        UserCreateRequest userCreateRequest = new UserCreateRequest("TESTMAN", "test1234@test.com", "44444");

        given(userRepository.existsByEmail("test1234@test.com")).willReturn(true);

        // When & Then
        assertThatThrownBy(() -> userService.create(userCreateRequest, Optional.empty()))
            .isInstanceOf(UserEmailAlreadyExistsException.class);

        verify(userRepository, times(1)).existsByEmail("test1234@test.com");
        verify(userRepository, times(0)).save(any(User.class));
    }
}


