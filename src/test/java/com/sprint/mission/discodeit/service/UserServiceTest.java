
package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.domain.User;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserUpdateRequest;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    public UserServiceTest() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("사용자 생성 성공")
    void createUser_success() {
        UserCreateRequest request = new UserCreateRequest("홍길동", "test@example.com", "password123");
        User user = new User(request.name(), request.email(), request.password());

        given(userRepository.save(BDDMockito.any(User.class))).willReturn(user);

        User result = userService.create(request);

        assertThat(result.getName()).isEqualTo("홍길동");
        assertThat(result.getEmail()).isEqualTo("test@example.com");
    }

    @Test
    @DisplayName("존재하지 않는 사용자 수정 실패")
    void updateUser_notFound() {
        UUID id = UUID.randomUUID();
        UserUpdateRequest request = new UserUpdateRequest("수정이름", "updated@example.com");

        given(userRepository.findById(id)).willReturn(Optional.empty());

        assertThatThrownBy(() -> userService.update(id, request))
                .isInstanceOf(UserNotFoundException.class);
    }
}
