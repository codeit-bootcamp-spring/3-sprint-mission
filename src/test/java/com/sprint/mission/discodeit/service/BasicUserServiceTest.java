package com.sprint.mission.discodeit.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserUpdateRequest;
import com.sprint.mission.discodeit.dto.response.UserResponse;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.Role;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.user.DuplicateEmailException;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.basic.BasicUserService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class BasicUserServiceTest {

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

    @Mock
    PasswordEncoder passwordEncoder;

    @Test
    @DisplayName("사용자 생성 성공")
    void shouldCreateUserSuccessfully() {
        UserCreateRequest request = new UserCreateRequest("testuser", "test@email.com", "password");
        BinaryContentCreateRequest profileReq = new BinaryContentCreateRequest("test.png",
            "img/png", new byte[]{1, 2, 3});
        BinaryContent profile = new BinaryContent("test.png", 3L, "img/png");
        User user = new User("testuser", "test@email.com", "password", profile, Role.USER);

        given(userRepository.existsByUsername(any())).willReturn(false);
        given(userRepository.existsByEmail(any())).willReturn(false);
        given(binaryContentRepository.save(any())).willReturn(profile);
        given(userRepository.save(any())).willReturn(user);
        given(userMapper.toResponse(any()))
            .willReturn(
                new UserResponse(UUID.randomUUID(), "testuser", "test@email.com", null, true,
                    Role.USER));

        UserResponse result = userService.create(request, Optional.of(profileReq));
        assertThat(result.username()).isEqualTo("testuser");
    }

    @Test
    @DisplayName("이메일 중복 시 사용자 수정 실패")
    void shouldThrowDuplicateEmailException_whenEmailIsDuplicateForUpdate() {
        UUID userId = UUID.randomUUID();
        User user = new User("old", "old@email.com", "password", null, Role.USER);
        UserUpdateRequest request = new UserUpdateRequest("old", "new@email.com", "password");

        given(userRepository.findById(userId)).willReturn(Optional.of(user));
        given(userRepository.existsByEmail("new@email.com")).willReturn(true);

        assertThatThrownBy(() -> userService.update(userId, request, Optional.empty()))
            .isInstanceOf(DuplicateEmailException.class)
            .hasMessageContaining("email");
    }
}
