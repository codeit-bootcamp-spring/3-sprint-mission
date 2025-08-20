package com.sprint.mission.discodeit.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.*;

import com.sprint.mission.discodeit.dto.data.BinaryContentDto;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserUpdateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.Role;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.user.DuplicateUserException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.basic.BasicUserService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
@DisplayName("사용자 서비스 단위 테스트")
public class UserServiceTest {

    @Mock private UserRepository userRepository;
    @Mock private BinaryContentRepository binaryContentRepository;
    @Mock private BinaryContentStorage binaryContentStorage;
    @Mock private UserMapper userMapper;
    @Mock private PasswordEncoder passwordEncoder;

    @InjectMocks
    private BasicUserService userService;

    private User user;
    private UserDto userDto;
    private UserCreateRequest userCreateRequest;
    private byte[] profileBytes;
    private BinaryContent binaryContent;
    private BinaryContentCreateRequest profileCreateRequest;

    @BeforeEach
    void setUp() {
        profileBytes = "tom profile data".getBytes();
        binaryContent = new BinaryContent("tomProfile", (long) profileBytes.length, "png");
        profileCreateRequest = new BinaryContentCreateRequest("tomProfile", "png", profileBytes);

        userCreateRequest = new UserCreateRequest("tom", "tom@test.com", "pw123456");
        user = new User("tom", "tom@test.com", "pw123456", binaryContent);
        userDto = new UserDto(
            UUID.randomUUID(), "tom", "tom@test.com",
            new BinaryContentDto(UUID.randomUUID(), "tomProfile", 16L, "png"), false, Role.USER);
    }

    @Test
    @DisplayName("프로필 없는 신규 사용자 생성 성공")
    void createUserWithoutProfile() {
        User nullProfileUser = new User("tom", "tom@test.com", "pw123456", null);
        UserDto nullProfileUserDto = new UserDto(
            nullProfileUser.getId(), "tom", "tom@test.com",null,false, Role.USER
        );

        // given
        given(userRepository.existsByUsername("tom")).willReturn(false);
        given(userRepository.existsByEmail("tom@test.com")).willReturn(false);
        given(userRepository.save(any(User.class))).willReturn(nullProfileUser);
        given(userMapper.toDto(nullProfileUser)).willReturn(nullProfileUserDto);

        // when
        UserDto result = userService.create(userCreateRequest, Optional.empty());

        // then
        then(userRepository).should().existsByUsername("tom");
        then(userRepository).should().existsByEmail("tom@test.com");
        then(userRepository).should().save(any(User.class));
        then(userMapper).should().toDto(nullProfileUser);

        assertThat(result).isNotNull();
        assertThat(result.username()).isEqualTo(userCreateRequest.username());
        assertThat(result.email()).isEqualTo(userCreateRequest.email());
        assertThat(result.profile()).isNull();
    }

    @Test
    @DisplayName("프로필 포함한 신규 사용자 생성 성공")
    void createUserWithProfile() {
        // given
        given(userRepository.existsByUsername("tom")).willReturn(false);
        given(userRepository.existsByEmail("tom@test.com")).willReturn(false);

        given(binaryContentRepository.save(any(BinaryContent.class))).willReturn(binaryContent);
        given(binaryContentStorage.put(binaryContent.getId(), profileBytes)).willReturn(binaryContent.getId());

        given(userRepository.save(any(User.class))).willReturn(user);
        given(userMapper.toDto(user)).willReturn(userDto);

        // when
        UserDto result = userService.create(userCreateRequest, Optional.of(profileCreateRequest));

        // then
        then(binaryContentRepository).should().save(any(BinaryContent.class));
        then(binaryContentStorage).should().put(eq(binaryContent.getId()), eq(profileBytes));
        then(userRepository).should().save(any(User.class));
        then(userMapper).should().toDto(user);

        assertThat(result).isNotNull();
        assertThat(result.username()).isEqualTo(userCreateRequest.username());
        assertThat(result.email()).isEqualTo(userCreateRequest.email());
        assertThat(result.profile()).isNotNull();
        assertThat(result.profile().fileName()).isEqualTo(profileCreateRequest.fileName());
        assertThat(result.profile().size()).isEqualTo(profileBytes.length);
        assertThat(result.profile().contentType()).isEqualTo(profileCreateRequest.contentType());
    }

    @Test
    @DisplayName("사용자 생성 중 중복된 사용자명으로 예외 발생")
    void createUserWithDuplicateUsername() {
        // given
        given(userRepository.existsByUsername("tom")).willReturn(true);

        // when
        assertThatThrownBy(() ->
            userService.create(userCreateRequest, Optional.empty())
        ).isInstanceOf(DuplicateUserException.class);

        // then
        then(userRepository).should(never()).save(any(User.class));
        then(binaryContentRepository).should(never()).save(any(BinaryContent.class));
        then(binaryContentStorage).should(never()).put(any(), any());
        then(userMapper).should(never()).toDto(any());
    }

    @Test
    @DisplayName("사용자 정보 수정 성공")
    void updateUser() {
        // given
        UUID anyId = UUID.randomUUID();
        given(userRepository.findById(any(UUID.class))).willReturn(Optional.of(user));
        given(userRepository.existsByUsername("tom2")).willReturn(false);
        given(userRepository.existsByEmail("tom2@test.com")).willReturn(false);

        UserDto updatedDto = new UserDto(user.getId(), "tom2", "tom2@test.com",null, false, Role.USER);
        given(userMapper.toDto(user)).willReturn(updatedDto);

        UserUpdateRequest userUpdateRequest = new UserUpdateRequest("tom2", "tom2@test.com", "password12");

        // when
        UserDto result = userService.update(anyId, userUpdateRequest, Optional.empty());

        // then
        then(userRepository).should().findById(any(UUID.class));
        then(userRepository).should().existsByUsername(userUpdateRequest.newUsername());
        then(userRepository).should().existsByEmail(userUpdateRequest.newEmail());
        then(userMapper).should().toDto(user);

        assertThat(result).isSameAs(updatedDto);
        assertThat(result.username()).isEqualTo(userUpdateRequest.newUsername());
        assertThat(result.email()).isEqualTo(userUpdateRequest.newEmail());
    }

    @Test
    @DisplayName("사용자 정보 수정 중 UserNotFoundException 발생")
    void updateUserWithUserNotFoundException() {
        // given
        given(userRepository.findById(any(UUID.class))).willReturn(Optional.empty());

        // when, then
        assertThatThrownBy(() ->
            userService.update(UUID.randomUUID(),
                new UserUpdateRequest("tom2","tom2@test.com","password12"), Optional.empty())
        ).isInstanceOf(UserNotFoundException.class);

        then(userRepository).should().findById(any(UUID.class));
        then(userRepository).should(never()).existsByUsername(anyString());
        then(userRepository).should(never()).existsByEmail(anyString());
    }

    @Test
    @DisplayName("사용자 삭제 성공")
    void deleteUser_success() {
        // given
        UUID anyId = UUID.randomUUID();
        given(userRepository.findById(any(UUID.class))).willReturn(Optional.of(user));
        willDoNothing().given(binaryContentRepository).delete(any(BinaryContent.class));
        willDoNothing().given(userRepository).delete(any(User.class));

        // when
        userService.delete(anyId);

        // then
        then(userRepository).should().findById(any(UUID.class));
        then(binaryContentRepository).should().delete(any(BinaryContent.class));
        then(userRepository).should().delete(any(User.class));
    }

    @Test
    @DisplayName("사용자 삭제 중 UserNotFoundException 발생")
    void deleteUser_fail_notFound() {
        // given
        given(userRepository.findById(any(UUID.class))).willReturn(Optional.empty());

        // when, then
        assertThatThrownBy(() -> userService.delete(UUID.randomUUID()))
            .isInstanceOf(UserNotFoundException.class);

        then(userRepository).should().findById(any(UUID.class));
        then(binaryContentRepository).should(never()).delete(any());
        then(userRepository).should(never()).delete(any());
    }
}
