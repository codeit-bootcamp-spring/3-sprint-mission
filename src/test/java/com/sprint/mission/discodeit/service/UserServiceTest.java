package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.user.request.UserCreateRequest;
import com.sprint.mission.discodeit.mapper.original.UserMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.repository.jpa.JpaUserRepository;
import com.sprint.mission.discodeit.service.basic.BasicUserService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

/**
 * PackageName  : com.sprint.mission.discodeit.service
 * FileName     : UserServiceTest
 * Author       : dounguk
 * Date         : 2025. 6. 17.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("회원 관리 관리 테스트")
public class UserServiceTest {
    @Mock
    private JpaUserRepository userRepository;

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

    //0 사용자 생성/Users/dounguk/Desktop/workspace/codit/mission/some/path/3-sprint-mission/src/test/java/com/sprint/mission/discodeit/service/UserServiceTest.java:46: error: ';' expected
    // 파라미터 이미지 파일(nullable), username(Unique), email(unique), password

    // 0 1.이메일은 유니크 해야한다.
    //   2.username은 유니크 해야한다.
    //  4. 유저 생성시 user status가 생성되어야 한다.
    //  5.profile이 있을경우 binary content가 생성되어야 한다.

    @DisplayName("email은 중복되어선 안된다.")
    @Test
    void createUserWith_email_IllegalArgument_ThrowsException() {
        // given
        UserCreateRequest request = new UserCreateRequest("paul", "duplicate@email.com", "password123");

        when(userRepository.existsByEmail(request.email())).thenReturn(true);
        when(userRepository.existsByUsername(request.username())).thenReturn(false);

        // when & then
        assertThatThrownBy(() -> userService.create(request, Optional.empty()))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("User with email " + request.email() + " already exists");

        verify(userRepository).existsByEmail(request.email());
        verify(userRepository).existsByUsername(request.username());
        verifyNoMoreInteractions(userRepository);
    }

    @DisplayName("username은 중복되어선 안된다.")
    @Test
    void createUserWith_Username_IllegalArgument_ThrowsException() {
        // given

        // when

        // then
    }

    @DisplayName("유저 생성시 user status가 생성되어야 한다.")
    @Test
    void createUser_create_UserStatus() {

    }
}
