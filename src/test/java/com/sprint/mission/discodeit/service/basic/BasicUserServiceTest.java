package com.sprint.mission.discodeit.service.basic;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.never;

import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserUpdateRequest;
import com.sprint.mission.discodeit.dto.response.BinaryContentDto;
import com.sprint.mission.discodeit.dto.response.UserDto;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.exception.user.UserEmailDuplicationException;
import com.sprint.mission.discodeit.exception.user.UserNameDuplicationException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.exception.userstatus.UserStatusNotFoundByUserException;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;


@ExtendWith(MockitoExtension.class)
@DisplayName("BasicUserService 단위 테스트")
public class BasicUserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserStatusRepository userStatusRepository;

    @Mock
    private BinaryContentRepository binaryContentRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private BinaryContentStorage binaryContentStorage;

    @InjectMocks
    private BasicUserService userService;

    // 테스트용 공통 데이터
    private UserCreateRequest userCreateRequest;
    private User user;
    private UserDto userDto;
    private UserUpdateRequest userUpdateRequest;
    private UUID userId;
    private UUID profileId;
    private BinaryContent profile;
    private UserStatus userStatus;

    @BeforeEach
    void setUp() {
        // Mock 객체 주입 확인
        assertNotNull(userService);
        assertNotNull(userRepository);
        assertNotNull(userStatusRepository);
        assertNotNull(binaryContentRepository);
        assertNotNull(userMapper);
        assertNotNull(binaryContentStorage);

        userId = UUID.randomUUID();
        profileId = UUID.randomUUID();

        userCreateRequest = new UserCreateRequest(
                "홍길동",
                "hong@test.com",
                "1234"
        );

        user = new User("홍길동", "hong@test.com", "1234");
        ReflectionTestUtils.setField(user, "id", userId);

        profile = new BinaryContent("profile.jpg", 1000L, "image/jpeg");
        ReflectionTestUtils.setField(profile, "id", profileId);
        user.updateProfile(profile);

        userStatus = new UserStatus(user, Instant.now());
        user.setStatus(userStatus);

        userUpdateRequest = new UserUpdateRequest(
                "김철수",
                "kim@test.com", 
                "5678"
        );

        BinaryContentDto profileDto = new BinaryContentDto(
                profileId,
                "profile.jpg",
                1000L,
                "image/jpeg"
        );

        userDto = new UserDto(
                userId,
                "홍길동",
                "hong@test.com",
                profileDto,
                false
        );
    }

    @Nested
    @DisplayName("사용자 생성 테스트")
    class CreateUserTests {

        @Test
        @DisplayName("프로필 이미지 없이 사용자 생성 성공")
        void create_WithoutProfileImage_Success() {
            // Given
            UUID defaultProfileId = UUID.randomUUID();
            
            given(userRepository.existsByEmail(userCreateRequest.email())).willReturn(false);
            given(userRepository.existsByUsername(userCreateRequest.username())).willReturn(false);
            given(userRepository.save(any(User.class))).willReturn(user);
            given(binaryContentRepository.save(any(BinaryContent.class))).willAnswer(invocation -> {
                BinaryContent defaultProfile = invocation.getArgument(0);
                ReflectionTestUtils.setField(defaultProfile, "id", defaultProfileId);
                return defaultProfile;
            });
            given(userMapper.toDto(user)).willReturn(userDto);

            // When
            UserDto result = userService.create(userCreateRequest, Optional.empty());

            // Then
            assertThat(result).isNotNull();
            assertThat(result.username()).isEqualTo(userCreateRequest.username());
            assertThat(result.email()).isEqualTo(userCreateRequest.email());
            
            then(userRepository).should().existsByEmail(userCreateRequest.email());
            then(userRepository).should().existsByUsername(userCreateRequest.username());
            then(userRepository).should().save(any(User.class));
            then(binaryContentRepository).should().save(any(BinaryContent.class));
            then(binaryContentStorage).should().put(eq(defaultProfileId), any(byte[].class));
        }

        @Test
        @DisplayName("프로필 이미지와 함께 사용자 생성 성공")
        void create_WithProfileImage_Success() {
            // Given
            byte[] imageBytes = "test image".getBytes();
            BinaryContentCreateRequest profileRequest = new BinaryContentCreateRequest(
                    "custom-profile.jpg","image/jpeg", imageBytes
            );

            given(userRepository.existsByEmail(userCreateRequest.email())).willReturn(false);
            given(userRepository.existsByUsername(userCreateRequest.username())).willReturn(false);
            given(userRepository.save(any(User.class))).willReturn(user);
            given(binaryContentRepository.save(any(BinaryContent.class))).willAnswer(invocation -> {
                BinaryContent saved = invocation.getArgument(0);
                ReflectionTestUtils.setField(saved, "id", profileId);
                return saved;
            });
            given(userMapper.toDto(user)).willReturn(userDto);

            // When
            UserDto result = userService.create(userCreateRequest, Optional.of(profileRequest));

            // Then
            assertThat(result).isNotNull();
            assertThat(result.username()).isEqualTo(userCreateRequest.username());
            
            then(binaryContentStorage).should().put(eq(profileId), eq(imageBytes));
        }

        @Test
        @DisplayName("이메일 중복으로 사용자 생성 실패")
        void create_DuplicateEmail_ThrowsException() {
            // Given
            given(userRepository.existsByEmail(userCreateRequest.email())).willReturn(true);

            // When & Then
            assertThatThrownBy(() -> userService.create(userCreateRequest, Optional.empty()))
                    .isInstanceOf(UserEmailDuplicationException.class);
            
            then(userRepository).should().existsByEmail(userCreateRequest.email());
            then(userRepository).should(never()).save(any(User.class));
        }

        @Test
        @DisplayName("사용자명 중복으로 사용자 생성 실패")
        void create_DuplicateUsername_ThrowsException() {
            // Given
            given(userRepository.existsByEmail(userCreateRequest.email())).willReturn(false);
            given(userRepository.existsByUsername(userCreateRequest.username())).willReturn(true);

            // When & Then
            assertThatThrownBy(() -> userService.create(userCreateRequest, Optional.empty()))
                    .isInstanceOf(UserNameDuplicationException.class);
            
            then(userRepository).should().existsByUsername(userCreateRequest.username());
            then(userRepository).should(never()).save(any(User.class));
        }
    }

    @Nested
    @DisplayName("사용자 조회 테스트")
    class FindUserTests {

        @Test
        @DisplayName("ID로 사용자 조회 성공")
        void find_ExistingUser_Success() {
            // Given
            given(userRepository.findById(userId)).willReturn(Optional.of(user));
            given(userStatusRepository.findUserStatusByUserId(userId)).willReturn(Optional.of(userStatus));
            given(userMapper.toDto(user)).willReturn(userDto);

            // When
            UserDto result = userService.find(userId);

            // Then
            assertThat(result).isNotNull();
            assertThat(result.id()).isEqualTo(userId);
            assertThat(result.username()).isEqualTo(user.getUsername());
            
            then(userRepository).should().findById(userId);
            then(userStatusRepository).should().findUserStatusByUserId(userId);
            then(userMapper).should().toDto(user);
        }

        @Test
        @DisplayName("존재하지 않는 사용자 조회 실패")
        void find_NonExistingUser_ThrowsException() {
            // Given
            given(userRepository.findById(userId)).willReturn(Optional.empty());

            // When & Then
            assertThatThrownBy(() -> userService.find(userId))
                    .isInstanceOf(UserNotFoundException.class);
            
            then(userRepository).should().findById(userId);
            then(userStatusRepository).should(never()).findUserStatusByUserId(any());
        }

        @Test
        @DisplayName("사용자는 존재하지만 상태가 없는 경우 실패")
        void find_UserWithoutStatus_ThrowsException() {
            // Given
            given(userRepository.findById(userId)).willReturn(Optional.of(user));
            given(userStatusRepository.findUserStatusByUserId(userId)).willReturn(Optional.empty());

            // When & Then
            assertThatThrownBy(() -> userService.find(userId))
                    .isInstanceOf(UserStatusNotFoundByUserException.class);
            
            then(userRepository).should().findById(userId);
            then(userStatusRepository).should().findUserStatusByUserId(userId);
        }
    }

    @Nested
    @DisplayName("전체 사용자 조회 테스트")
    class FindAllUsersTests {

        @Test
        @DisplayName("전체 사용자 조회 성공")
        void findAll_WithUsers_Success() {
            // Given
            User user2 = new User("김철수", "kim@test.com", "5678");
            UUID user2Id = UUID.randomUUID();
            ReflectionTestUtils.setField(user2, "id", user2Id);
            
            List<User> users = Arrays.asList(user, user2);
            
            UserDto userDto2 = new UserDto(user2Id, "김철수", "kim@test.com", null, false);
            
            given(userRepository.findAllWithProfileAndStatus()).willReturn(users);
            given(userMapper.toDto(user)).willReturn(userDto);
            given(userMapper.toDto(user2)).willReturn(userDto2);

            // When
            List<UserDto> result = userService.findAll();

            // Then
            assertThat(result).hasSize(2);
            assertThat(result.get(0).username()).isEqualTo("홍길동");
            assertThat(result.get(1).username()).isEqualTo("김철수");
            
            then(userRepository).should().findAllWithProfileAndStatus();
            then(userMapper).should().toDto(user);
            then(userMapper).should().toDto(user2);
        }

        @Test
        @DisplayName("사용자가 없을 때 빈 리스트 반환")
        void findAll_NoUsers_ReturnsEmptyList() {
            // Given
            given(userRepository.findAllWithProfileAndStatus()).willReturn(Arrays.asList());

            // When
            List<UserDto> result = userService.findAll();

            // Then
            assertThat(result).isEmpty();
            
            then(userRepository).should().findAllWithProfileAndStatus();
            then(userMapper).should(never()).toDto(any(User.class));
        }
    }

    @Nested
    @DisplayName("사용자 수정 테스트")
    class UpdateUserTests {

        @Test
        @DisplayName("사용자 정보 수정 성공")
        void update_ExistingUser_Success() {
            // Given
            given(userRepository.findById(userId)).willReturn(Optional.of(user));
            given(userRepository.existsByEmail(userUpdateRequest.newEmail())).willReturn(false);
            given(userRepository.existsByUsername(userUpdateRequest.newUsername())).willReturn(false);
            given(userMapper.toDto(user)).willReturn(userDto);

            // When
            UserDto result = userService.update(userId, userUpdateRequest, Optional.empty());

            // Then
            assertThat(result).isNotNull();
            
            then(userRepository).should().findById(userId);
            then(userRepository).should().existsByEmail(userUpdateRequest.newEmail());
            then(userRepository).should().existsByUsername(userUpdateRequest.newUsername());
            then(userMapper).should().toDto(user);
        }

        @Test
        @DisplayName("프로필 이미지와 함께 사용자 정보 수정 성공")
        void update_WithNewProfileImage_Success() {
            // Given
            byte[] newImageBytes = "new image".getBytes();
            BinaryContentCreateRequest newProfileRequest = new BinaryContentCreateRequest(
                    "new-profile.jpg", "image/jpeg", newImageBytes
            );
            UUID newProfileId = UUID.randomUUID();
            
            given(userRepository.findById(userId)).willReturn(Optional.of(user));
            given(userRepository.existsByEmail(userUpdateRequest.newEmail())).willReturn(false);
            given(userRepository.existsByUsername(userUpdateRequest.newUsername())).willReturn(false);

            given(binaryContentRepository.save(any(BinaryContent.class))).willAnswer(invocation -> {
                BinaryContent saved = invocation.getArgument(0);
                ReflectionTestUtils.setField(saved, "id", newProfileId);
                return saved;
            });

            willDoNothing().given(binaryContentStorage).delete(profileId);
            willDoNothing().given(binaryContentRepository).deleteById(profileId);
            given(userMapper.toDto(user)).willReturn(userDto);

            // When
            UserDto result = userService.update(userId, userUpdateRequest, Optional.of(newProfileRequest));

            // Then
            assertThat(result).isNotNull();
            
            then(binaryContentStorage).should().delete(profileId);
            then(binaryContentRepository).should().deleteById(profileId);
            then(binaryContentStorage).should().put(eq(newProfileId), eq(newImageBytes));
        }

        @Test
        @DisplayName("존재하지 않는 사용자 수정 시도시 실패")
        void update_NonExistingUser_ThrowsException() {
            // Given
            given(userRepository.findById(userId)).willReturn(Optional.empty());

            // When & Then
            assertThatThrownBy(() -> userService.update(userId, userUpdateRequest, Optional.empty()))
                    .isInstanceOf(UserNotFoundException.class);
            
            then(userRepository).should().findById(userId);
            then(userRepository).should(never()).existsByEmail(any());
        }

        @Test
        @DisplayName("이메일 중복으로 수정 실패")
        void update_DuplicateEmail_ThrowsException() {
            // Given
            given(userRepository.findById(userId)).willReturn(Optional.of(user));
            given(userRepository.existsByEmail(userUpdateRequest.newEmail())).willReturn(true);

            // When & Then
            assertThatThrownBy(() -> userService.update(userId, userUpdateRequest, Optional.empty()))
                    .isInstanceOf(UserEmailDuplicationException.class);
            
            then(userRepository).should().existsByEmail(userUpdateRequest.newEmail());
        }

        @Test
        @DisplayName("사용자명 중복으로 수정 실패")
        void update_DuplicateUsername_ThrowsException() {
            // Given
            given(userRepository.findById(userId)).willReturn(Optional.of(user));
            given(userRepository.existsByEmail(userUpdateRequest.newEmail())).willReturn(false);
            given(userRepository.existsByUsername(userUpdateRequest.newUsername())).willReturn(true);

            // When & Then
            assertThatThrownBy(() -> userService.update(userId, userUpdateRequest, Optional.empty()))
                    .isInstanceOf(UserNameDuplicationException.class);
            
            then(userRepository).should().existsByUsername(userUpdateRequest.newUsername());
        }
    }

    @Nested
    @DisplayName("사용자 삭제 테스트")
    class DeleteUserTests {

        @Test
        @DisplayName("사용자 삭제 성공")
        void delete_ExistingUser_Success() {
            // Given
            given(userRepository.findById(userId)).willReturn(Optional.of(user));
            willDoNothing().given(binaryContentStorage).delete(profileId);
            willDoNothing().given(userRepository).delete(user);

            // When
            userService.delete(userId);

            // Then
            then(userRepository).should().findById(userId);
            then(binaryContentStorage).should().delete(profileId);
            then(userRepository).should().delete(user);
        }

        @Test
        @DisplayName("프로필 이미지가 없는 사용자 삭제 성공")
        void delete_UserWithoutProfile_Success() {
            // Given
            User userWithoutProfile = new User("테스트", "test@test.com", "1234");
            ReflectionTestUtils.setField(userWithoutProfile, "id", userId);
            // 프로필 이미지 설정하지 않음 (null)
            
            given(userRepository.findById(userId)).willReturn(Optional.of(userWithoutProfile));
            willDoNothing().given(userRepository).delete(userWithoutProfile);

            // When
            userService.delete(userId);

            // Then
            then(userRepository).should().findById(userId);
            then(binaryContentStorage).should(never()).delete(any());
            then(userRepository).should().delete(userWithoutProfile);
        }

        @Test
        @DisplayName("존재하지 않는 사용자 삭제 시도시 실패")
        void delete_NonExistingUser_ThrowsException() {
            // Given
            given(userRepository.findById(userId)).willReturn(Optional.empty());

            // When & Then
            assertThatThrownBy(() -> userService.delete(userId))
                    .isInstanceOf(UserNotFoundException.class);
            
            then(userRepository).should().findById(userId);
            then(userRepository).should(never()).delete(any(User.class));
            then(binaryContentStorage).should(never()).delete(any());
        }
    }
}
