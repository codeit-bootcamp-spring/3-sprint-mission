package com.sprint.mission.discodeit.service;

import static com.sprint.mission.discodeit.fixture.BinaryContentFixture.createBinaryContent;
import static com.sprint.mission.discodeit.fixture.UserFixture.createUser;
import static com.sprint.mission.discodeit.fixture.UserFixture.createUserDto;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.never;

import com.sprint.mission.discodeit.dto.BinaryContent.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.User.UserCreateRequest;
import com.sprint.mission.discodeit.dto.User.UserDto;
import com.sprint.mission.discodeit.dto.User.UserUpdateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.User.UserAlreadyExistsException;
import com.sprint.mission.discodeit.exception.User.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.basic.BasicUserService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import java.nio.charset.StandardCharsets;
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
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private BinaryContentRepository binaryContentRepository;
    @Mock
    private BinaryContentStorage binaryContentStorage;
    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private BasicUserService userService;

    @Test
    @DisplayName("유효한 생성 요청으로 프로필이 포함된 유저를 생성할 수 있다.")
    void shouldCreateUserWithProfile_whenValidRequest() {

        // given
        String name = "테스트유저";
        String email = "test@codeit.com";
        String password = "test1234";
        byte[] testBytes = "테스트 이미지".getBytes(StandardCharsets.UTF_8);

        UserCreateRequest userCreateRequest = new UserCreateRequest(name, email, password);
        BinaryContent profile = createBinaryContent("테스트 프로필", 1024L, "image/png");
        BinaryContentCreateRequest profileCreateRequest = new BinaryContentCreateRequest(
                profile.getFileName(), profile.getContentType(), testBytes);
        User user = createUser(name, email, password);
        UserDto expectedDto = createUserDto(user, profile);
        UUID profileId = profile.getId();

        given(userRepository.existsByUsername(name)).willReturn(false);
        given(userRepository.existsByEmail(email)).willReturn(false);
        given(binaryContentRepository.save(any(BinaryContent.class))).willReturn(profile);
        given(binaryContentStorage.put(any(), any())).willReturn(profileId);
        given(userMapper.toDto(any(User.class))).willReturn(expectedDto);

        // when
        UserDto result = userService.create(userCreateRequest, profileCreateRequest);

        // then
        assertThat(result.username()).isEqualTo(name);
        assertThat(result.email()).isEqualTo(email);
        assertThat(result.profile().id()).isEqualTo(profileId);

        then(binaryContentRepository).should().save(any(BinaryContent.class));
        then(binaryContentStorage).should().put(any(), eq(testBytes));
    }

    @Test
    @DisplayName("유효한 생성 요청으로 프로필이 없는 유저를 생성할 수 있다.")
    void shouldCreateUserWithoutProfile_whenValidRequest() {

        // given
        String name = "테스트유저";
        String email = "test@codeit.com";
        String password = "test1234";
        UserCreateRequest userCreateRequest = new UserCreateRequest(name, email, password);
        User user = createUser(name, email, password);
        UserDto userDto = createUserDto(user);

        given(userRepository.existsByUsername(name)).willReturn(false);
        given(userRepository.existsByEmail(email)).willReturn(false);
        given(userMapper.toDto(any(User.class))).willReturn(userDto);

        // when
        UserDto result = userService.create(userCreateRequest, null);

        // then
        assertThat(result.username()).isEqualTo(name);
        assertThat(result.email()).isEqualTo(email);
        assertThat(result.profile()).isNull();

        then(binaryContentRepository).shouldHaveNoInteractions();
        then(binaryContentStorage).shouldHaveNoInteractions();
    }

    @Test
    @DisplayName("이미 존재하는 유저명으로 유저 생성 요청시 예외가 발생한다.")
    void shouldThrowException_whenCreatingExistentUserName() {

        // given
        String name = "테스트유저";
        String email = "test@codeit.com";
        String password = "test1234";
        UserCreateRequest userCreateRequest = new UserCreateRequest(name, email, password);

        given(userRepository.existsByUsername(name)).willReturn(true);

        // when & then
        assertThatThrownBy(() -> {
            userService.create(userCreateRequest, null);
        }).isInstanceOf(UserAlreadyExistsException.class);

        then(userRepository).should().existsByUsername(name);
        then(binaryContentRepository).shouldHaveNoInteractions();
        then(binaryContentStorage).shouldHaveNoInteractions();
        then(userRepository).should(never()).save(any(User.class));
    }

    @Test
    @DisplayName("이미 존재하는 이메일로 유저 생성 요청시 예외가 발생한다.")
    void shouldThrowException_whenCreatingExistentEmail() {

        // given
        String name = "테스트유저";
        String email = "test@codeit.com";
        String password = "test1234";
        UserCreateRequest userCreateRequest = new UserCreateRequest(name, email, password);

        given(userRepository.existsByEmail(email)).willReturn(true);

        // when & then
        assertThatThrownBy(() -> {
            userService.create(userCreateRequest, null);
        }).isInstanceOf(UserAlreadyExistsException.class);

        then(userRepository).should().existsByEmail(email);
        then(binaryContentRepository).shouldHaveNoInteractions();
        then(binaryContentStorage).shouldHaveNoInteractions();
        then(userRepository).should(never()).save(any(User.class));
    }

    @Test
    @DisplayName("유효한 수정 요청으로 유저 정보를 수정할 수 있다.")
    void shouldUpdateUser_whenValidRequest() {

        // given
        User user = createUser("테스터", "tester@codeit.com", "tester1234");
        BinaryContent profile = createBinaryContent("테스트 프로필", 1024L, "image/png");
        UUID userId = user.getId();
        String newName = "(*수정)김코드";
        String newEmail = "kimkim@codeit.com";
        String newPassword = "kimkim1234";
        byte[] newProfileImage = "테스트 이미지".getBytes();
        UserUpdateRequest updateRequest = new UserUpdateRequest(newName, newEmail, newPassword);
        BinaryContentCreateRequest profileCreateRequest = new BinaryContentCreateRequest(
                profile.getFileName(), profile.getContentType(), newProfileImage);
        User newUser = createUser(newName, newEmail, newPassword);
        UserDto expectedDto = createUserDto(newUser, profile);

        given(userRepository.findById(userId)).willReturn(Optional.of(user));
        given(userRepository.existsByUsername(newName)).willReturn(false);
        given(userRepository.existsByEmail(newEmail)).willReturn(false);
        given(binaryContentRepository.save(any(BinaryContent.class))).willReturn(profile);
        given(binaryContentStorage.put(any(), eq(newProfileImage))).willReturn(
                profile.getId());
        given(userMapper.toDto(any(User.class))).willReturn(expectedDto);

        // when
        UserDto result = userService.update(userId, updateRequest, profileCreateRequest);

        // then
        assertThat(result.username()).isEqualTo(newName);
        assertThat(result.email()).isEqualTo(newEmail);
        assertThat(result.profile()).isNotNull();

        then(userRepository).should().findById(userId);
        then(userRepository).should().existsByUsername(newName);
        then(userRepository).should().existsByEmail(newEmail);
        then(binaryContentRepository).should().save(any(BinaryContent.class));
        then(binaryContentStorage).should().put(any(), eq(newProfileImage));
    }

    @Test
    @DisplayName("이미 존재하는 유저명으로 유저 수정 요청시 예외가 발생한다.")
    void shouldThrowException_whenUpdatingExistentUserName() {

        // given
        User originalUser = createUser("테스터", "tester@codeit.com", "tester1234");
        UUID userId = originalUser.getId();
        String newName = "(*수정)김코드";
        String newEmail = "kimkim@codeit.com";
        String newPassword = "kimkim1234";
        UserUpdateRequest updateRequest = new UserUpdateRequest(newName, newEmail, newPassword);

        given(userRepository.findById(userId)).willReturn(Optional.of(originalUser));
        given(userRepository.existsByUsername(newName)).willReturn(true);

        // when & then
        assertThatThrownBy(() -> {
            userService.update(userId, updateRequest, null);
        }).isInstanceOf(UserAlreadyExistsException.class);

        then(userRepository).should().existsByUsername(newName);
        then(userRepository).should(never()).existsByEmail(newEmail);
        then(binaryContentRepository).shouldHaveNoInteractions();
        then(binaryContentStorage).shouldHaveNoInteractions();
        then(userRepository).should(never()).save(any(User.class));
    }

    @Test
    @DisplayName("이미 존재하는 이메일로 유저 수정 요청시 예외가 발생한다.")
    void shouldThrowException_whenUpdatingExistentEmail() {

        // given
        User originalUser = createUser("테스터", "tester@codeit.com", "tester1234");
        UUID userId = originalUser.getId();
        String newName = "(*수정)김코드";
        String newEmail = "kimkim@codeit.com";
        String newPassword = "kimkim1234";
        UserUpdateRequest updateRequest = new UserUpdateRequest(newName, newEmail, newPassword);

        given(userRepository.findById(userId)).willReturn(Optional.of(originalUser));
        given(userRepository.existsByUsername(newName)).willReturn(false);
        given(userRepository.existsByEmail(newEmail)).willReturn(true);

        // when & then
        assertThatThrownBy(() -> {
            userService.update(userId, updateRequest, null);
        }).isInstanceOf(UserAlreadyExistsException.class);

        then(userRepository).should().existsByEmail(newEmail);
        then(binaryContentRepository).shouldHaveNoInteractions();
        then(binaryContentStorage).shouldHaveNoInteractions();
        then(userRepository).should(never()).save(any(User.class));
    }

    @Test
    @DisplayName("유효한 삭제 요청으로 유저를 삭제할 수 있다.")
    void shouldDeleteUser_whenValidRequest() {

        // given
        User user = createUser("테스터", "tester@codeit.com", "tester1234");
        UUID userId = user.getId();

        given(userRepository.existsById(userId)).willReturn(true);
        willDoNothing().given(userRepository).deleteById(userId);

        // when
        userService.delete(userId);

        // then
        then(userRepository).should().existsById(userId);
        then(userRepository).should().deleteById(userId);
    }

    @Test
    @DisplayName("존재하지 않는 유저 삭제 요청시 예외가 발생한다.")
    void shouldThrowException_whenNonExistentUser() {

        // given
        UUID userId = UUID.randomUUID();

        given(userRepository.existsById(userId)).willReturn(false);

        // when & then
        assertThatThrownBy(() -> {
            userService.delete(userId);
        }).isInstanceOf(UserNotFoundException.class);

        then(userRepository).should().existsById(userId);
        then(userRepository).should(never()).deleteById(userId);
    }
}