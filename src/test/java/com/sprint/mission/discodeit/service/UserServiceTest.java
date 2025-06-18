package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.binaryContent.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.user.JpaUserResponse;
import com.sprint.mission.discodeit.dto.user.request.UserCreateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.helper.FileUploadUtils;
import com.sprint.mission.discodeit.mapper.advanced.UserMapper;
import com.sprint.mission.discodeit.mapper.advanced.UserStatusMapper;
import com.sprint.mission.discodeit.repository.jpa.JpaBinaryContentRepository;
import com.sprint.mission.discodeit.repository.jpa.JpaUserRepository;
import com.sprint.mission.discodeit.repository.jpa.JpaUserStatusRepository;
import com.sprint.mission.discodeit.service.basic.BasicUserService;
import com.sprint.mission.discodeit.storage.LocalBinaryContentStorage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

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
    private FileUploadUtils fileUploadUtils;

    @Mock
    private JpaUserRepository userRepository;

    @Mock
    private JpaUserStatusRepository userStatusRepository;

    @Mock
    private JpaBinaryContentRepository binaryContentRepository;

    @Mock
    private LocalBinaryContentStorage binaryContentStorage;

    @Mock
    private UserMapper userMapper;

    @Mock
    private UserStatusMapper userStatusMapper;

    @InjectMocks
    private BasicUserService userService;


    @DisplayName("프로필이 있을 때 Binary Content가 생성된다.")
    @Test
    void whenProfileExists_thenShouldCreateBinaryContent() {
        // given
        UserCreateRequest request = new UserCreateRequest("paul", "test@email.com", "password123");

        byte[] fileBytes = "test file content".getBytes();
        BinaryContentCreateRequest profileRequest = new BinaryContentCreateRequest("profile.jpg", "image/jpeg", fileBytes);
        Optional<BinaryContentCreateRequest> profile = Optional.of(profileRequest);

        BinaryContent savedBinaryContent = new BinaryContent("profile.jpg", (long) fileBytes.length, "image/jpeg", ".jpg");
        when(binaryContentRepository.save(any(BinaryContent.class))).thenReturn(savedBinaryContent);
        when(userMapper.toDto(any(User.class))).thenReturn(mock(JpaUserResponse.class));

        // when
        userService.create(request, profile);

        // then - BinaryContent 생성 관련만 검증
        verify(binaryContentRepository, times(1)).save(argThat(bc ->
            bc.getFileName().equals("profile.jpg") && bc.getContentType().equals("image/jpeg")
        ));
        verify(binaryContentStorage, times(1)).put(any(), eq(fileBytes));
    }
    @DisplayName("프로필이 있을 때 Binary Content가 생성되지 않는다.")
    @Test
    void whenProfileNotFound_thenShouldNotCreateBinaryContent() {
        // given
        UserCreateRequest request = new UserCreateRequest("paul", "duplicate@email.com", "password123");
        when(userMapper.toDto(any(User.class))).thenReturn(mock(JpaUserResponse.class));

        // when
        userService.create(request, Optional.empty());

        // then
        verify(binaryContentRepository, times(0)).save(any(BinaryContent.class));
        verify(binaryContentStorage, times(0)).get(any());
    }

    @DisplayName("email은 중복되어선 안된다.")
    @Test
    void whenEmailIsNotUnique_thenThrowsIllegalArgument() {
        // given
        UserCreateRequest request = new UserCreateRequest("paul", "duplicate@email.com", "password123");

        when(userRepository.existsByEmail(request.email())).thenReturn(true);

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
    void whenUsernameIsNotUnique_thenThrowsIllegalArgument() {
        UserCreateRequest request = new UserCreateRequest("paul", "duplicate@email.com", "password123");

        when(userRepository.existsByUsername(request.username())).thenReturn(true);

        // when & then
        assertThatThrownBy(() -> userService.create(request, Optional.empty()))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("User with username " + request.username() + " already exists");

        verify(userRepository).existsByEmail(request.email());
        verify(userRepository).existsByUsername(request.username());
        verifyNoMoreInteractions(userRepository);
    }

    @DisplayName("유저 생성시 user status가 생성되어야 한다.")
    @Test
    void createUser_create_UserStatus() {
        // given
        UserCreateRequest request = new UserCreateRequest("paul", "duplicate@email.com", "password123");

        // when
        userService.create(request, Optional.empty());

        // then
        verify(userStatusRepository, times(1)).save(any(UserStatus.class));
    }


    /*
    * 0올바른 파라미터가 아닐경우 NoSuchElementException 반환
    *  프로필이 있을경우 프로필 삭제
    *  프로필이 없을경우 프로필 삭제
    *  userStatus가 삭제되어야 한다.
    * */

    @DisplayName("올바른 파라미터가 아닐경우 NoSuchElementException 반환")
    @Test
    void whenParameterIsNotValid_thenThrowsNoSuchElementException() {
        UUID id = UUID.randomUUID();
        // given
        when(userRepository.findById(id)).thenThrow(new NoSuchElementException(id + " not found"));

        // when n then
        assertThatThrownBy(() -> userService.deleteUser(id))
            .isInstanceOf(NoSuchElementException.class)
            .hasMessageContaining(id + " not found");

        verify(userRepository, times(1)).findById(id);
    }

    @DisplayName("프로필이 있을경우 바이너리 컨텐츠가 삭제 되어야 한다.")
    @Test
    void whenDeleteUser_thenDeleteBinaryContent() {
        // given
        UUID id = UUID.randomUUID();
        BinaryContent binaryContent = mock(BinaryContent.class);

        User user = User.builder()
            .username("paul")
            .password("password123")
            .email("paul@gmail.com")
            .profile(binaryContent)
            .build();

        when(userRepository.findById(id)).thenReturn(Optional.ofNullable(user));


        // when
        userService.deleteUser(id);

        // then
        verify(userRepository, times(1)).delete(user);
        verify(fileUploadUtils, times(1)).getUploadPath(anyString());
        verifyNoMoreInteractions(binaryContentRepository);
    }
}
