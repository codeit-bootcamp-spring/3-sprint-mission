package com.sprint.mission.discodeit.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.verify;

import com.sprint.mission.discodeit.dto.BinaryContentDto;
import com.sprint.mission.discodeit.dto.UserDto;
import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserUpdateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.exception.user.UserEmailAlreadyExistException;
import com.sprint.mission.discodeit.exception.user.UserNameAlreadyExistException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.basic.BasicUserService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import java.nio.charset.StandardCharsets;
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
@DisplayName("BasicUserService 단위 테스트")
public class UserServiceTest {

    @InjectMocks
    private BasicUserService userService;
    @Mock
    private UserRepository userRepository;
    @Mock
    private BinaryContentRepository binaryContentRepository;
    @Mock
    private UserStatusRepository userStatusRepository;
    @Mock
    private MessageRepository messageRepository;
    @Mock
    private BinaryContentStorage binaryContentStorage;
    @Mock
    private UserMapper userMapper;

    private UUID userId;
    private UUID profileId;
    private String username;
    private String email;
    private String password;
    private byte[] imageBytes;

    private User userWithProfile;
    private User user;
    private BinaryContent profileImage;

    private UserDto userWithProfileDto;
    private UserDto userDto;
    private BinaryContentDto profileDto;
    private BinaryContentCreateRequest profileRequest;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        profileId = UUID.randomUUID();
        username = "testUser";
        email = "testuser@abc.com";
        password = "1234";
        imageBytes = "이미지".getBytes(StandardCharsets.UTF_8);

        profileImage = new BinaryContent("img.png", (long) imageBytes.length, "image/png");
        ReflectionTestUtils.setField(profileImage, "id", profileId);

        user = new User(username, email, password, null);
        ReflectionTestUtils.setField(user, "id", userId);

        userWithProfile = new User(username, email, password, profileImage);
        ReflectionTestUtils.setField(userWithProfile, "id", userId);

        profileDto = new BinaryContentDto(profileId, "img.png", (long) imageBytes.length,
            "image/png");
        userWithProfileDto = new UserDto(userId, username, email, profileDto, true);
        userDto = new UserDto(userId, username, email, null, true);

        profileRequest = new BinaryContentCreateRequest("img.png", (long) imageBytes.length,
            "image/png", imageBytes);
    }

    @Test
    @DisplayName("프로필이미지가 있는 사용자를 생성할 수 있다.")
    void createUser_ProfileImage_Success() {
        // given
        UserCreateRequest userCreateRequest = new UserCreateRequest(username, email, password);

        given(userRepository.existsByEmail(email)).willReturn(false);
        given(userRepository.existsByUsername(username)).willReturn(false);
        given(binaryContentRepository.save(any(BinaryContent.class))).willReturn(profileImage);
        given(binaryContentStorage.put(eq(profileId), eq(imageBytes))).willReturn(profileId);
        given(userRepository.save(any(User.class))).willReturn(userWithProfile);
        given(userStatusRepository.save(any(UserStatus.class))).willReturn(any());
        given(userMapper.toDto(userWithProfile)).willReturn(userWithProfileDto);

        // when
        UserDto result = userService.createUser(userCreateRequest, Optional.of(profileRequest));

        // then
        assertThat(result).isNotNull();
        assertThat(result.username()).isEqualTo(username);
        assertThat(result.email()).isEqualTo(email);
        assertThat(result.profile()).isNotNull();
        assertThat(result.profile().id()).isEqualTo(profileId);
    }

    @Test
    @DisplayName("프로필이미지가 없는 사용자를 생성할 수 있다.")
    void createUser_NoProfileImage_Success() {
        //given
        UserCreateRequest userCreateRequest = new UserCreateRequest(username, email, password);
        UserDto userDto = new UserDto(userId, username, email, null, true);

        given(userRepository.existsByEmail(email)).willReturn(false);
        given(userRepository.existsByUsername(username)).willReturn(false);
        given(userRepository.save(any(User.class))).willReturn(user);
        given(userStatusRepository.save(any(UserStatus.class))).willReturn(any());
        given(userMapper.toDto(user)).willReturn(userDto);

        //when
        UserDto result = userService.createUser(userCreateRequest, Optional.empty());

        //then
        assertThat(result).isNotNull();
        assertThat(result.username()).isEqualTo(username);
        assertThat(result.email()).isEqualTo(email);
        assertThat(result.profile()).isNull();
    }

    @Test
    @DisplayName("이미 존재하는 이메일로 사용자를 생성할 수 없다.")
    void createUser_EmailAlreadyExists_Fail() {
        //given
        UserCreateRequest userCreateRequest = new UserCreateRequest(username, email, password);

        given(userRepository.existsByEmail(email)).willReturn(true);

        //when, then
        assertThatThrownBy(() -> userService.createUser(userCreateRequest, Optional.empty()))
            .isInstanceOf(UserEmailAlreadyExistException.class);
    }

    @Test
    @DisplayName("이미 존재하는 사용자명으로 사용자를 생성할 수 없다.")
    void createUser_UsernameAlreadyExists_Fail() {
        // given
        UserCreateRequest request = new UserCreateRequest(username, email, password);

        given(userRepository.existsByEmail(email)).willReturn(false);
        given(userRepository.existsByUsername(username)).willReturn(true); // 이미 사용자명 존재

        // when & then
        assertThatThrownBy(() -> userService.createUser(request, Optional.empty()))
            .isInstanceOf(UserNameAlreadyExistException.class);
    }

    @Test
    @DisplayName("새로운 사용자 입력값으로 수정할 수 있다.")
    void updateUser_Success() {
        // given
        String newUsername = "newUsername";
        String newEmail = "new@abc.com";
        String newPassword = "newPassword";
        UserUpdateRequest request = new UserUpdateRequest(newUsername, newEmail, newPassword);

        given(userRepository.findById(eq(userId))).willReturn(Optional.of(user));
        given(userRepository.existsByEmail(eq(newEmail))).willReturn(false);
        given(userRepository.existsByUsername(eq(newUsername))).willReturn(false);
        given(userRepository.save(any(User.class))).willReturn(user);
        given(userMapper.toDto(user)).willReturn(userDto);

        // when
        UserDto result = userService.update(userId, request, Optional.empty());

        // then
        assertThat(result).isNotNull();
        assertThat(result.username()).isEqualTo(userDto.username());
        assertThat(result.email()).isEqualTo(userDto.email());
    }

    @Test
    @DisplayName("존재하지 않는 사용자는 정보를 수정할 수 없다.")
    void updateUser_WithNonExistentId_Fali() {
        //given
        UserUpdateRequest request = new UserUpdateRequest("newName", "newEmail@abc.com",
            "newPassword");
        given(userRepository.findById(eq(userId))).willReturn(Optional.empty());

        //when, then
        assertThatThrownBy(() -> userService.update(userId, request, Optional.empty()))
            .isInstanceOf(UserNotFoundException.class);

    }

    @Test
    @DisplayName("사용자를 삭제할 수 있다.")
    void deleteUser_Success() {
        //given
        given(userRepository.findById(userId)).willReturn(Optional.of(user));
        willDoNothing().given(messageRepository).deleteByAuthorId(userId);

        //when
        userService.delete(userId);

        //then
        verify(messageRepository).deleteByAuthorId(userId);
        verify(userRepository).deleteById(userId);
    }

    @Test
    @DisplayName("존재하지 않는 사용자는 삭제할 수 없다.")
    void deleteUser_WithNonExistentId_Fali() {
        // given
        given(userRepository.findById((userId))).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> userService.delete(userId))
            .isInstanceOf(UserNotFoundException.class);
    }

}