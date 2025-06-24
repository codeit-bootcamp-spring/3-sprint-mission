package com.sprint.mission.discodeit.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.mock;
import static org.mockito.BDDMockito.then;

import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserUpdateRequest;
import com.sprint.mission.discodeit.dto.response.UserResponse;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.user.DuplicateEmailException;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.basic.BasicUserService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class BasicUserServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private UserStatusRepository userStatusRepository;
    @Mock
    private BinaryContentRepository binaryContentRepository;
    @Mock
    private BinaryContentStorage binaryContentStorage;
    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private BasicUserService userService;

    private AutoCloseable closeable;

    @BeforeEach
    void setUp() {
        closeable = MockitoAnnotations.openMocks(this);
    }

    @AfterEach
    void tearDown() throws Exception {
        closeable.close();
    }

    @Test
    @DisplayName("사용자 생성 성공")
    void shouldCreateUserSuccessfully() {
        UserCreateRequest request = new UserCreateRequest("testuser", "test@email.com", "password");
        BinaryContentCreateRequest profileReq = new BinaryContentCreateRequest("test.png",
            "img/png", new byte[]{1, 2, 3});
        BinaryContent profile = new BinaryContent("test.png", 3L, "img/png");
        User user = new User("testuser", "test@email.com", "password", profile);

        given(userRepository.existsByUsername(any())).willReturn(false);
        given(userRepository.existsByEmail(any())).willReturn(false);
        given(binaryContentRepository.save(any())).willReturn(profile);
        given(userRepository.save(any())).willReturn(user);
        given(userMapper.toResponse(any())).willReturn(
            new UserResponse(UUID.randomUUID(), "testuser", "test@email.com", null, true));

        UserResponse result = userService.create(request, Optional.of(profileReq));
        assertThat(result.username()).isEqualTo("testuser");
    }

    @Test
    @DisplayName("사용자 조회 성공")
    void shouldFindUserSuccessfully() {
        UUID userId = UUID.randomUUID();
        User user = mock(User.class);
        given(userRepository.findById(userId)).willReturn(Optional.of(user));
        given(userMapper.toResponse(user)).willReturn(
            new UserResponse(userId, "u", "e", null, true));

        UserResponse result = userService.find(userId);
        assertThat(result.id()).isEqualTo(userId);
    }

    @Test
    @DisplayName("이메일 중복 시 사용자 수정 실패")
    void shouldThrowDuplicateEmailException_whenEmailIsDuplicateForUpdate() {
        UUID userId = UUID.randomUUID();
        User user = new User("old", "old@email.com", "password", null);
        UserUpdateRequest request = new UserUpdateRequest("old", "new@email.com", "password");

        given(userRepository.findById(userId)).willReturn(Optional.of(user));
        given(userRepository.existsByEmail("new@email.com")).willReturn(true);

        assertThatThrownBy(() -> userService.update(userId, request, Optional.empty()))
            .isInstanceOf(DuplicateEmailException.class);
    }

    @Test
    @DisplayName("사용자 삭제 성공")
    void shouldDeleteUserSuccessfully() {
        UUID userId = UUID.randomUUID();
        User user = new User("user", "email", "password", null);

        given(userRepository.findById(userId)).willReturn(Optional.of(user));
        given(userMapper.toResponse(user)).willReturn(
            new UserResponse(userId, "user", "email", null, true));

        UserResponse result = userService.delete(userId);

        assertThat(result.id()).isEqualTo(userId);
        then(userRepository).should().deleteById(userId);
        then(userRepository).should().delete(user);
    }
}
