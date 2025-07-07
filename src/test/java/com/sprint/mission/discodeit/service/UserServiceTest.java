package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.data.BinaryContentDto;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserUpdateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.user.DuplicateUserException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.basic.BasicUserService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
@DisplayName("유저 서비스 단위 테스트")
public class UserServiceTest {

    @Mock private UserRepository userRepository;
    @Mock private BinaryContentRepository binaryContentRepository;
    @Mock private BinaryContentStorage binaryContentStorage;
    @Mock private UserMapper userMapper;

    @InjectMocks
    private BasicUserService userService;

    private BinaryContent binaryContent;
    private BinaryContentCreateRequest profileCreateRequest;
    private User user;
    private UserDto userDto;

    @BeforeEach
    void setUp() {
        byte[] profileBytes = "test image data".getBytes();

        binaryContent = new BinaryContent("test프로필", (long) profileBytes.length, "png");

        profileCreateRequest = new BinaryContentCreateRequest("test프로필", "png", profileBytes);

        user = new User("테스트유저", "test@gmail.com", "12345678", binaryContent);

        userDto = new UserDto(
                UUID.randomUUID(), "테스트유저", "test@gmail.com",
                new BinaryContentDto(UUID.randomUUID(), "test프로필", 10L, "png", profileBytes),
                null);

        // 유저 상태는 도중에 세팅되므로 무시 가능
    }

    @Test
    @DisplayName("정상적인 요청을 통해 유저를 생성할 수 있다")
    void shouldCreateUserWhenRequestValid() {
        // given
        UserCreateRequest userCreateRequest = new UserCreateRequest("테스트유저", "test@gmail.com", "12345678");
        Optional<BinaryContentCreateRequest> optionalProfile = Optional.of(profileCreateRequest);

        given(binaryContentRepository.save(any(BinaryContent.class))).willReturn(binaryContent);
        given(binaryContentStorage.put(any(), any())).willReturn(userDto.profile().id());
        given(userRepository.save(any(User.class))).willReturn(user);
        given(userMapper.toDto(any(User.class))).willReturn(userDto);

        // when
        UserDto result = userService.create(userCreateRequest, optionalProfile);

        // then
        assertThat(result).isNotNull();
        assertThat(result.username()).isEqualTo(userCreateRequest.username());
        assertThat(result.email()).isEqualTo(userCreateRequest.email());
        assertThat(result.profile().fileName()).isEqualTo("test프로필");

        verify(binaryContentStorage).put(any(), any());
        verify(binaryContentRepository).save(any(BinaryContent.class));
        verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("중복된 이메일은 생성할 수 없습니다")
    void shouldNotCreateUserWhenRequestInvalid() {
        // given
        UserCreateRequest userCreateRequest = new UserCreateRequest("테스트유저", "test@gmail.com", "12345678");
        Optional<BinaryContentCreateRequest> optionalProfile = Optional.of(profileCreateRequest);

        // 기존에 동일 이메일이 이미 존재하는 상황 가정
        given(userRepository.existsByEmail(userCreateRequest.email())).willReturn(true);

        // when & then
        assertThatThrownBy(() -> userService.create(userCreateRequest, optionalProfile))
                .isInstanceOf(DuplicateUserException.class);

    }

    @Test
    @DisplayName("정상적인 요청을 통해 유저 정보를 수정할 수 있다")
    void shouldUpdateUserWhenRequestValid() {
        // given
        UUID userId = user.getId();
        byte[] profileBytes = "update test image data".getBytes();
        profileCreateRequest = new BinaryContentCreateRequest("update-profile", "png", profileBytes);

        UserUpdateRequest userUpdateRequest = new UserUpdateRequest("update유저", "update@gmail.com", "000000000");
        Optional<BinaryContentCreateRequest> optionalProfileCreateRequest = Optional.of(profileCreateRequest);

        BinaryContent updatedProfile = new BinaryContent("update-profile", (long) profileBytes.length, "png");

        given(userRepository.findById(userId)).willReturn(Optional.of(user));
        given(binaryContentRepository.save(any())).willReturn(updatedProfile);
        given(binaryContentStorage.put(any(), any())).willReturn(UUID.randomUUID());

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        given(userMapper.toDto(userCaptor.capture())).willReturn(userDto);

        // when
        UserDto result = userService.update(userId, userUpdateRequest, optionalProfileCreateRequest);

        // then
        User updated = userCaptor.getValue();
        assertThat(updated.getUsername()).isEqualTo("update유저");
        assertThat(updated.getEmail()).isEqualTo("update@gmail.com");
        assertThat(updated.getProfile().getFileName()).isEqualTo("update-profile");

        assertThat(result).isNotNull();
        verify(userRepository).findById(userId);
        verify(binaryContentRepository).save(any());
        verify(binaryContentStorage).put(any(), any());
        verify(userMapper).toDto(any());
    }

    @Test
    @DisplayName("존재하지 않는 유저 ID로 수정 요청 시 예외가 발생한다")
    void shouldNotUpdateUserWhenUserIdNotFound() {
        // given
        UUID userId = UUID.randomUUID();
        byte[] profileBytes = "update test image data".getBytes();
        profileCreateRequest = new BinaryContentCreateRequest("update-profile", "png", profileBytes);

        UserUpdateRequest userUpdateRequest = new UserUpdateRequest("update유저", "update@gmail.com", "000000000");
        Optional<BinaryContentCreateRequest> optionalProfileCreateRequest = Optional.of(profileCreateRequest);

        // when * then
        assertThatThrownBy(() -> userService.update(userId, userUpdateRequest, optionalProfileCreateRequest))
                .isInstanceOf(UserNotFoundException.class);


    }

    @Test
    @DisplayName("정상적인 요청을 통해 유저를 삭제할 수 있다")
    void shouldDeleteUserWhenRequestValid() {
        // given
        UUID userId = user.getId();

        given(userRepository.findById(userId)).willReturn(Optional.of(user));
        doNothing().when(binaryContentRepository).deleteById(binaryContent.getId());
        given(binaryContentStorage.put(user.getProfile().getId(), null)).willReturn(UUID.randomUUID());
        // when

        userService.delete(userId);

        // then
        verify(userRepository).deleteById(userId);
        verify(binaryContentRepository).deleteById(binaryContent.getId());
        verify(binaryContentStorage).put(user.getProfile().getId(), null);
    }

    @Test
    @DisplayName("존재하지 않는 유저 ID로 삭제 요청 시 예외가 발생한다")
    void shouldNotDeleteUserWhenUserIdNotFound() {
        // given
        UUID userId = UUID.randomUUID();

        // when & then
        assertThatThrownBy(() -> userService.delete(userId)).isInstanceOf(UserNotFoundException.class);
    }
}
