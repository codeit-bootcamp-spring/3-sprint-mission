package com.sprint.mission.discodeit.unit;

import com.sprint.mission.discodeit.dto.binaryContent.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.user.JpaUserResponse;
import com.sprint.mission.discodeit.dto.user.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.user.request.UserUpdateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.exception.UserAlreadyExistsException;
import com.sprint.mission.discodeit.helper.FileUploadUtils;
import com.sprint.mission.discodeit.mapper.advanced.UserMapper;
import com.sprint.mission.discodeit.repository.jpa.JpaBinaryContentRepository;
import com.sprint.mission.discodeit.repository.jpa.JpaUserRepository;
import com.sprint.mission.discodeit.repository.jpa.JpaUserStatusRepository;
import com.sprint.mission.discodeit.storage.LocalBinaryContentStorage;
import com.sprint.mission.discodeit.unit.basic.BasicUserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.*;

/**
 * PackageName  : com.sprint.mission.discodeit.service
 * FileName     : UserServiceTest
 * Author       : dounguk
 * Date         : 2025. 6. 17.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("User unit 테스트")
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
        given(binaryContentRepository.save(any(BinaryContent.class))).willReturn(savedBinaryContent);
        given(userMapper.toDto(any(User.class))).willReturn(mock(JpaUserResponse.class));

        // when
        userService.create(request, profile);

        // then
        then(binaryContentRepository).should(times(1)).save(argThat(bc ->
            bc.getFileName().equals("profile.jpg") && bc.getContentType().equals("image/jpeg")
        ));

        then(binaryContentStorage).should(times(1)).put(any(), eq(fileBytes));
    }
    @DisplayName("프로필이 있을 때 Binary Content가 생성되지 않는다.")
    @Test
    void whenProfileNotFound_thenShouldNotCreateBinaryContent() {
        // given
        UserCreateRequest request = new UserCreateRequest("paul", "duplicate@email.com", "password123");
        given(userMapper.toDto(any(User.class))).willReturn(mock(JpaUserResponse.class));

        // when
        userService.create(request, Optional.empty());

        // then
        then(binaryContentRepository).should(times(0)).save(any(BinaryContent.class));
        then(binaryContentStorage).should(times(0)).get(any());
    }

    @DisplayName("email은 중복되어선 안된다.")
    @Test
    void whenEmailIsNotUnique_thenThrowsIllegalArgument() {
        // given
        UserCreateRequest request = new UserCreateRequest("paul", "duplicate@email.com", "password123");

        given(userRepository.existsByEmail(request.email())).willReturn(true);

        // when & then
        assertThatThrownBy(() -> userService.create(request, Optional.empty()))
            .isInstanceOf(UserAlreadyExistsException.class);

        then(userRepository).should().existsByEmail(request.email());
        then(userRepository).should().existsByUsername(request.username());
        then(userRepository).shouldHaveNoMoreInteractions();
    }

    @DisplayName("username은 중복되어선 안된다.")
    @Test
    void whenUsernameIsNotUnique_thenThrowsIllegalArgument() {
        UserCreateRequest request = new UserCreateRequest("paul", "duplicate@email.com", "password123");

        given(userRepository.existsByUsername(request.username())).willReturn(true);

        // when & then
        assertThatThrownBy(() -> userService.create(request, Optional.empty()))
            .isInstanceOf(UserAlreadyExistsException.class);

        then(userRepository).should().existsByEmail(request.email());
        then(userRepository).should().existsByUsername(request.username());
        then(userRepository).shouldHaveNoMoreInteractions();
    }

    @DisplayName("유저 생성시 user status가 생성되어야 한다.")
    @Test
    void createUser_create_UserStatus() {
        // given
        UserCreateRequest request = new UserCreateRequest("paul", "duplicate@email.com", "password123");

        // when
        userService.create(request, Optional.empty());

        // then
        then(userStatusRepository).should(times(1)).save(any(UserStatus.class));
    }

    /*
    * 0+프로필이 없을경우 삭제는 생략
    *  +userStatus가 삭제되어야 한다.
    * */

    @DisplayName("올바른 파라미터가 아닐경우 NoSuchElementException 반환 해야 한다.")
    @Test
    void whenParameterIsNotValid_thenThrowsNoSuchElementException() {
        UUID id = UUID.randomUUID();
        // given
        given(userRepository.findById(id)).willThrow(new NoSuchElementException(id + " not found"));

        // when n then
        assertThatThrownBy(() -> userService.deleteUser(id))
            .isInstanceOf(NoSuchElementException.class)
            .hasMessageContaining(id + " not found");

        then(userRepository).should(times(1)).findById(id);
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

        given(userRepository.findById(id)).willReturn(Optional.ofNullable(user));

        // when
        userService.deleteUser(id);

        // then
        then(userRepository).should(times(1)).delete(user);
        then(fileUploadUtils).should(times(1)).getUploadPath(anyString());
        then(binaryContentRepository).shouldHaveNoMoreInteractions();
    }

    //0+찾는 유저가 없으면 UserNotFound를 반환 해야 한다.
    // +프로필이 없을 경우 프로필 삭제 로직으로 가면 안된다.

    @DisplayName("중복된 username 으로 변경시 UserAlreadyExistsException을 반환 해야한다.")
    @Test
    void whenUpdatingWithExistUsername_thenThrowsUserAlreadyExistsException() {
        // given
        UUID id = UUID.randomUUID();
        User user = User.builder()
            .username("daniel")
            .password("password123")
            .email("paul@gmail.com")
            .build();
        UserUpdateRequest request = new UserUpdateRequest("paul", "paul@gmail.com", "newPass");


        given(userRepository.existsByUsername(request.newUsername())).willReturn(true);
        given(userRepository.findById(id)).willReturn(Optional.ofNullable(user));

        // when
        assertThatThrownBy(() -> userService.update(id, request, null))
            .isInstanceOf(UserAlreadyExistsException.class);

        then(userRepository).should().findById(id);
        then(userRepository).should().existsByUsername(request.newUsername());

        then(userRepository).shouldHaveNoMoreInteractions();
        then(fileUploadUtils).shouldHaveNoMoreInteractions();
        then(binaryContentRepository).shouldHaveNoMoreInteractions();
        then(binaryContentStorage).shouldHaveNoMoreInteractions();
    }

    @DisplayName("프로필이 있는 상태에서 업데이트를 할 경우 기존의 사진은 지워야 한다.")
    @Test
    void whenUpdatingProfile_thenDeleteOldProfile() throws IOException {

        // given
        UUID id = UUID.randomUUID();
        byte[] fileBytes = new byte[]{1, 2};

        BinaryContent savedBinaryContent = new BinaryContent("test.jpg", (long) fileBytes.length, "image/png", ".png");
        User user = User.builder()
            .username("daniel")
            .password("password123")
            .email("paul@gmail.com")
            .profile(savedBinaryContent)
            .build();

        MultipartFile file = new MockMultipartFile("profile", "test.png", "image/png", fileBytes);


        String uploadDir = Files.createTempDirectory("test-profile").toFile().getAbsolutePath();
        String oldFileName = savedBinaryContent.getId() + savedBinaryContent.getExtension();
        File oldFile = new File(uploadDir, oldFileName);
        Files.write(oldFile.toPath(), new byte[]{1, 2});

        given(userRepository.findById(any()))
            .willReturn(Optional.of(user));
        given(fileUploadUtils.getUploadPath("img"))
            .willReturn(uploadDir);
        given(binaryContentRepository.save(any()))
            .willAnswer(inv -> inv.getArgument(0));
        given(userRepository.existsByUsername(any()))
            .willReturn(false);
        given(userRepository.existsByEmail(any()))
            .willReturn(false);

        userService.update(id, new UserUpdateRequest("daniel", "dan@mail.com", null), file);

        then(binaryContentRepository).should().delete(savedBinaryContent);
    }
}
