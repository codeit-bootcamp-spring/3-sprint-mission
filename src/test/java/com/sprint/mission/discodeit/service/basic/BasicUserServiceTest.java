package com.sprint.mission.discodeit.service.basic;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.never;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.times;

import com.sprint.mission.discodeit.dto.data.BinaryContentDto;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.RoleUpdateRequest;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserUpdateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.Role;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.user.DuplicateEmailException;
import com.sprint.mission.discodeit.exception.user.DuplicateUserException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.fixture.BinaryContentFixture;
import com.sprint.mission.discodeit.fixture.UserFixture;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.security.DiscodeitUserDetails;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserService 단위 테스트")
class BasicUserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private BinaryContentRepository binaryContentRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private BinaryContentStorage binaryContentStorage;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private SessionRegistry sessionRegistry;

    @Mock
    private JdbcTemplate jdbcTemplate;

    @Mock
    private UserDetailsService userDetailsService;

    @InjectMocks
    private BasicUserService userService;

    @Test
    @DisplayName("사용자 생성 - 성공(프로필 이미지 있음)")
    void create_Success() {
        // Given
        BinaryContent profileEntity = BinaryContentFixture.entity();
        BinaryContentDto profileDto = BinaryContentFixture.dto();
        BinaryContentCreateRequest binaryContentCreateRequest = BinaryContentFixture.createRequest();
        Optional<BinaryContentCreateRequest> optionalProfile = Optional.of(
            binaryContentCreateRequest);
        User user_profile = UserFixture.entity("프로필유저", "profile@email.com", profileEntity);
        UserDto userDto2 = UserFixture.dto("프로필유저", "profile@email.com", profileDto, Role.USER);
        UserCreateRequest request = UserFixture.createRequest("프로필유저", "profile@email.com");
        given(userRepository.existsByEmail(request.email())).willReturn(false);
        given(userRepository.existsByUsername(request.username())).willReturn(false);
        given(binaryContentRepository.save(any(BinaryContent.class))).willReturn(profileEntity);
        given(binaryContentStorage.put(any(), any())).willReturn(userDto2.profile().id());
        given(userRepository.save(any(User.class))).willReturn(user_profile);
        given(userMapper.toDto(any(User.class))).willReturn(userDto2);
        given(passwordEncoder.encode(any())).willReturn("encodedPw");

        // When
        UserDto result = userService.create(request, optionalProfile);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.username()).isEqualTo(request.username());
        assertThat(result.email()).isEqualTo(request.email());
        assertThat(result.profile().fileName()).isEqualTo("testImage");
        then(binaryContentRepository).should().save(any(BinaryContent.class));
        then(binaryContentStorage).should().put(any(), any());
        then(userRepository).should().save(any(User.class));
    }

    @Test
    @DisplayName("사용자 생성 - 실패(이메일 중복)")
    void create_Fail_DuplicateEmail() {
        // Given
        UserCreateRequest userCreateRequest = UserFixture.createRequestZzo();
        given(userRepository.existsByEmail(userCreateRequest.email())).willReturn(true);

        // When
        Throwable thrown = catchThrowable(
            () -> userService.create(userCreateRequest, Optional.empty()));

        // Then
        assertThat(thrown)
            .isInstanceOf(DuplicateEmailException.class);
        then(userRepository).should().existsByEmail("zzo@email.com");
        then(userRepository).should(never()).save(any(User.class));
    }

    @Test
    @DisplayName("사용자 생성 - 실패(사용자명 중복)")
    void create_Fail_DuplicateUsername() {
        // Given
        UserCreateRequest userCreateRequest = UserFixture.createRequestZzo();
        given(userRepository.existsByUsername(userCreateRequest.username())).willReturn(true);

        // When
        Throwable thrown = catchThrowable(
            () -> userService.create(userCreateRequest, Optional.empty()));

        // Then
        assertThat(thrown)
            .isInstanceOf(DuplicateUserException.class);
        then(userRepository).should().existsByUsername("쪼쪼");
        then(userRepository).should(never()).save(any(User.class));
    }

    @Test
    @DisplayName("사용자 조회 - 성공")
    void find_Success() {
        // Given
        UserDto userDto = UserFixture.dtoZzo();
        UUID userId = userDto.id();
        User user = UserFixture.entityZzo();
        ReflectionTestUtils.setField(user, "id", userId);

        given(userRepository.findById(userId)).willReturn(Optional.of(user));
        given(userMapper.toDto(user)).willReturn(userDto);

        // When
        UserDto result = userService.find(userId);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(userId);
        assertThat(result.username()).isEqualTo("쪼쪼");
        then(userRepository).should().findById(userId);
        then(userMapper).should().toDto(user);
    }

    @Test
    @DisplayName("사용자 조회 - 실패(존재하지 않는 사용자)")
    void find_Fail_UserNotFound() {
        // Given
        UUID userId = UUID.randomUUID();
        given(userRepository.findById(userId)).willReturn(Optional.empty());

        // When
        Throwable thrown = catchThrowable(() -> userService.find(userId));

        // Then
        assertThat(thrown)
            .isInstanceOf(UserNotFoundException.class);
        then(userRepository).should().findById(userId);
        then(userMapper).should(never()).toDto(any(User.class));
    }

    @Test
    @DisplayName("전체 사용자 조회 - 성공")
    void findAll_Success() {
        // Given
        User user1 = UserFixture.entityZzo();
        User user2 = UserFixture.entity("투현아", "22@email.com", null);
        List<User> users = List.of(user1, user2);
        UserDto userDto1 = UserFixture.dtoZzo();
        UserDto userDto2 = UserFixture.dto("투현아", "22@email.com", null, Role.USER);
        given(userRepository.findAll()).willReturn(users);
        given(userMapper.toDto(user1)).willReturn(userDto1);
        given(userMapper.toDto(user2)).willReturn(userDto2);

        // When
        List<UserDto> result = userService.findAll();

        // Then
        assertThat(result).hasSize(2);
        assertThat(result).isNotEmpty();
        then(userRepository).should().findAll();
        then(userMapper).should(times(2)).toDto(any(User.class));
    }

    @Test
    @DisplayName("사용자 수정 - 성공")
    void update_Success() {
        // Given
        User user = UserFixture.entityZzo();
        UserDto userDto = UserFixture.dtoZzo();
        UUID userId = userDto.id();
        ReflectionTestUtils.setField(user, "id", userId);
        UserUpdateRequest testUpdateRequest = new UserUpdateRequest(
            "뉴현아",
            "zzo@email.com",
            "newPassword123!"
        );
        User updateUser = new User("뉴현아", "zzo@email.com", "newPassword123!", null);
        UserDto testUpdateUserDto = new UserDto(
            userId,
            updateUser.getUsername(),
            updateUser.getEmail(),
            null,
            true,
            Role.USER
        );
        given(userRepository.existsByEmail(testUpdateRequest.newEmail())).willReturn(false);
        given(userRepository.existsByUsername(testUpdateRequest.newUsername())).willReturn(false);
        given(userRepository.findById(userId)).willReturn(Optional.of(updateUser));
        given(userMapper.toDto(any(User.class))).willReturn(testUpdateUserDto);
        given(passwordEncoder.encode(any())).willReturn("encodedPw");
        DiscodeitUserDetails details =
            new DiscodeitUserDetails(testUpdateUserDto, "encodedPwOrWhatever");
        given(userDetailsService.loadUserByUsername("뉴현아")).willReturn(details);

        // When
        UserDto result = userService.update(userId, testUpdateRequest, Optional.empty());

        // Then
        assertThat(result.id()).isEqualTo(userId);
        assertThat(result.username()).isEqualTo("뉴현아");
        assertThat(result.email()).isEqualTo("zzo@email.com");
        then(userRepository).should().findById(userId);
        then(userRepository).should().existsByEmail(testUpdateRequest.newEmail());
        then(userRepository).should().existsByUsername(testUpdateRequest.newUsername());
        then(userMapper).should().toDto(updateUser);
    }

    @Test
    @DisplayName("사용자 수정 - 기존 프로필 삭제 후 새 프로필 교체")
    void update_Success_ProfileReplace() {
        // Given
        BinaryContent oldProfile = BinaryContentFixture.entity();
        User user = UserFixture.entity("프로필유저", "profile@email.com", oldProfile);
        UUID userId = UUID.randomUUID();
        ReflectionTestUtils.setField(user, "id", userId);
        BinaryContentCreateRequest newProfileRequest = BinaryContentFixture.createRequest();
        Optional<BinaryContentCreateRequest> optionalProfile = Optional.of(newProfileRequest);
        BinaryContent newProfileEntity = BinaryContentFixture.entity();
        UserUpdateRequest updateRequest = new UserUpdateRequest(
            "프로필유저", // 닉네임
            "profile@email.com",
            "newPassword123!"
        );
        UserDto updatedUserDto = new UserDto(
            userId,
            "프로필유저",
            "profile@email.com",
            BinaryContentFixture.dto(),
            true,
            Role.USER
        );
        given(userRepository.findById(userId)).willReturn(Optional.of(user));
        given(userRepository.existsByEmail(updateRequest.newEmail())).willReturn(false);
        given(userRepository.existsByUsername(updateRequest.newUsername())).willReturn(false);
        given(binaryContentRepository.save(any(BinaryContent.class))).willReturn(newProfileEntity);
        given(binaryContentStorage.put(any(), any())).willReturn(updatedUserDto.profile().id());
        given(userMapper.toDto(any(User.class))).willReturn(updatedUserDto);
        given(passwordEncoder.encode(any())).willReturn("encodedPw");
        DiscodeitUserDetails details =
            new DiscodeitUserDetails(updatedUserDto, "encodedPwOrWhatever");
        given(userDetailsService.loadUserByUsername("프로필유저")).willReturn(details);

        // When
        UserDto result = userService.update(userId, updateRequest, optionalProfile);

        // Then
        assertThat(result.id()).isEqualTo(userId);
        assertThat(result.username()).isEqualTo("프로필유저");
        assertThat(result.email()).isEqualTo("profile@email.com");
        assertThat(result.profile().fileName()).isEqualTo(newProfileRequest.fileName());
        then(binaryContentRepository).should().delete(oldProfile);
        then(binaryContentRepository).should().save(any(BinaryContent.class));
        then(binaryContentStorage).should().put(any(), any());
    }

    @Test
    @DisplayName("사용자 수정 - 실패(이메일 중복)")
    void update_Fail_DuplicateEmail() {
        // Given
        UUID userId = UUID.randomUUID();
        UserUpdateRequest testUpdateRequest = new UserUpdateRequest("뉴현아", "zzo@email.com",
            "newPassword123");
        Optional<BinaryContentCreateRequest> emptyProfile = Optional.empty();
        User user = new User("뉴현아", "zzo@email.com", "newPassword123!", null);
        ReflectionTestUtils.setField(user, "id", userId);
        given(userRepository.findById(userId)).willReturn(Optional.of(user));
        given(userRepository.existsByEmail(testUpdateRequest.newEmail())).willReturn(true);

        // When
        Throwable thrown = catchThrowable(
            () -> userService.update(userId, testUpdateRequest, emptyProfile));

        // Then
        assertThat(thrown)
            .isInstanceOf(DuplicateEmailException.class);
        then(userRepository).should().findById(userId);
        then(userRepository).should().existsByEmail("zzo@email.com");
    }

    @Test
    @DisplayName("사용자 수정 - 실패(존재하지 않는 id)")
    void update_Fail_UserNotFound() {
        // Given
        UUID userId = UUID.randomUUID();
        UserUpdateRequest testUpdateRequest = new UserUpdateRequest("뉴현아", "zzo@email.com",
            "newPassword123");
        Optional<BinaryContentCreateRequest> emptyProfile = Optional.empty();
        given(userRepository.findById(userId)).willReturn(Optional.empty());

        // When
        Throwable thrown = catchThrowable(
            () -> userService.update(userId, testUpdateRequest, emptyProfile));

        // Then
        assertThat(thrown)
            .isInstanceOf(UserNotFoundException.class);
        then(userRepository).should().findById(userId);
    }

    @Test
    @DisplayName("사용자 수정 - 실패(이름 중복)")
    void update_Fail_ExistsByUsername() {
        // Given
        UUID userId = UUID.randomUUID();
        UserUpdateRequest testUpdateRequest = new UserUpdateRequest("중복현아", "zzo@email.com",
            "newPassword123");
        Optional<BinaryContentCreateRequest> emptyProfile = Optional.empty();
        User user = new User("중복현아", "zzo@email.com", "newPassword123!", null);
        ReflectionTestUtils.setField(user, "id", userId);

        given(userRepository.findById(userId)).willReturn(Optional.of(user));
        given(userRepository.existsByUsername(testUpdateRequest.newUsername())).willReturn(true);

        // When
        Throwable thrown = catchThrowable(
            () -> userService.update(userId, testUpdateRequest, emptyProfile));

        // Then
        assertThat(thrown)
            .isInstanceOf(DuplicateUserException.class);
        then(userRepository).should().findById(userId);
        then(userRepository).should().existsByUsername("중복현아");
    }


    @Test
    @DisplayName("사용자 삭제 - 성공")
    void delete_Success() {
        // Given
        UUID userId = UUID.randomUUID();
        User user = new User("조현아", "zzo@gmail.com", "password123!", null);
        ReflectionTestUtils.setField(user, "id", userId);
        given(userRepository.findById(userId)).willReturn(Optional.of(user));

        // When
        userService.delete(userId);

        // Then
        then(userRepository).should().findById(userId);
        then(userRepository).should().deleteById(userId);
    }

    @Test
    @DisplayName("사용자 삭제 - 실패(존재하지 않는 사용자)")
    void delete_Fail_UserNotFound() {
        // Given
        UUID userId = UUID.randomUUID();
        given(userRepository.findById(userId)).willReturn(Optional.empty());

        // When
        Throwable thrown = catchThrowable(() -> userService.delete(userId));

        // Then
        assertThat(thrown)
            .isInstanceOf(UserNotFoundException.class);
        then(userRepository).should().findById(userId);
        then(userRepository).should(never()).deleteById(any());
    }

    @Test
    @DisplayName("사용자 삭제 - 성공(프로필 이미지 있음)")
    void delete_Success_WithProfile() {
        // Given
        BinaryContent profileEntity = BinaryContentFixture.entity();
        BinaryContentDto profileDto = BinaryContentFixture.dto();
        BinaryContentCreateRequest binaryContentCreateRequest = BinaryContentFixture.createRequest();
        Optional<BinaryContentCreateRequest> optionalProfile = Optional.of(
            binaryContentCreateRequest);
        User user_profile = UserFixture.entity("프로필유저", "profile@email.com", profileEntity);
        UserDto userDto2 = UserFixture.dto("프로필유저", "profile@email.com", profileDto, Role.USER);
        UUID userId = userDto2.id();
        ReflectionTestUtils.setField(user_profile, "id", userId);
        given(userRepository.findById(userId)).willReturn(Optional.of(user_profile));

        // When
        userService.delete(userId);

        // Then
        then(userRepository).should().findById(userId);
        then(binaryContentRepository).should().delete(profileEntity);
        then(userRepository).should().deleteById(userId);
    }

    @Test
    @DisplayName("사용자 권한 변경 - 일반 유저를 채널 매니저로 변경")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void updateUserRole_UserToChannelManager() {
        // Given
        User normalUser = UserFixture.entityZzo();
        UUID userId = UUID.randomUUID();
        ReflectionTestUtils.setField(normalUser, "id", userId);
        RoleUpdateRequest request = new RoleUpdateRequest(userId, Role.CHANNEL_MANAGER);
        normalUser.updateRole(Role.CHANNEL_MANAGER);
        ReflectionTestUtils.setField(normalUser, "id", userId);
        UserDto updatedUserDto = new UserDto(
            userId,
            "쪼쪼",
            "zzo@email.com",
            null,
            true,
            Role.CHANNEL_MANAGER
        );
        given(userRepository.findById(userId)).willReturn(Optional.of(normalUser));
        given(userRepository.save(any(User.class))).willReturn(normalUser);
        given(userMapper.toDto(any(User.class))).willReturn(updatedUserDto);

        // When
        UserDto result = userService.updateUserRole(request);

        // Then
        assertThat(result.id()).isEqualTo(userId);
        assertThat(result.username()).isEqualTo("쪼쪼");
        assertThat(result.role()).isEqualTo(Role.CHANNEL_MANAGER);
        then(userRepository).should().findById(userId);
        then(userRepository).should().save(any(User.class));
        then(userMapper).should().toDto(any(User.class));
    }
}