package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.data.BinaryContentDto;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.user.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.user.UserUpdateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.exception.user.DuplicatedUserException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.BinaryContentMapper;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.basic.BasicUserService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
/**
 * UserService의 주요 기능(생성, 수정, 삭제, 예외 등)에 대한 단위 테스트 클래스입니다.
 * <p>
 * - 사용자 생성, 수정, 삭제, 예외 상황 등을 검증합니다.
 * - Mockito를 활용한 Mock 객체 기반 테스트입니다.
 */
public class UserServiceTest {

    @InjectMocks
    private BasicUserService basicUserService;

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

    @Mock
    private BinaryContentMapper binaryContentMapper;

    private UUID userId;
    private UserCreateRequest validCreateRequest;
    private UserUpdateRequest validUpdateRequest;
    private BinaryContentCreateRequest validProfileRequest;
    private User existingUser;

    @BeforeEach
    @DisplayName("테스트 환경 설정 확인")
    void setUp() {
        userId = UUID.randomUUID();
        validCreateRequest = new UserCreateRequest("testUser", "test@example.com", "securePass");
        validUpdateRequest = new UserUpdateRequest("newUser", "new@example.com", "newPass");
        validProfileRequest = new BinaryContentCreateRequest("profile.png", "image/png", new byte[]{1, 2, 3});

        existingUser = new User("oldUser", "old@example.com", "oldPass", null);
        existingUser.setId(userId);

        // 의존성 주입이 올바르게 되었는지 확인
        assertNotNull(basicUserService, "basicUserService가 정상적으로 주입되어야 한다");
    }

    /**
     * [성공] 프로필 없이 사용자를 생성할 수 있는지 검증합니다.
     */
    @Test
    @DisplayName("[성공] 프로필 없이 사용자 생성")
    void create_ShouldCreateUserWithoutProfile_WhenNoProfileProvided() {

        // Given
        given(userRepository.existsByEmail("test@example.com")).willReturn(false);
        given(userRepository.existsByUsername("testUser")).willReturn(false);

        User user = new User("testUser", "test@example.com", "securePass", null);
        given(userRepository.saveAndFlush(any(User.class))).willReturn(user);
        UserDto mockUser = new UserDto(userId, "testUser", "test@example.com", null, true);
        given(userMapper.toDto(any(User.class))).willReturn(mockUser);

        // When
        UserDto result = basicUserService.create(validCreateRequest, Optional.empty());

        // Then
        assertNotNull(result);
        then(userRepository).should().existsByEmail("test@example.com");
        then(userRepository).should().existsByUsername("testUser");
        then(userRepository).should().saveAndFlush(any(User.class));
        then(userStatusRepository).should().save(any(UserStatus.class));
        then(binaryContentRepository).shouldHaveNoInteractions();
        then(binaryContentStorage).shouldHaveNoInteractions();
    }

    /**
     * [성공] 프로필이 있을 때 사용자를 생성할 수 있는지 검증합니다.
     */
    @Test
    @DisplayName("[성공] 프로필과 함께 사용자 생성")
    void create_ShouldCreateUserWithProfile_WhenProfileProvided() {

        // Given
        given(userRepository.existsByEmail("test@example.com")).willReturn(false);
        given(userRepository.existsByUsername("testUser")).willReturn(false);

        BinaryContent mockContent = new BinaryContent("profile.png", 3L, "image/png");
        BinaryContentDto mockContentDto = binaryContentMapper.toDto(mockContent);
        given(binaryContentRepository.save(any(BinaryContent.class))).willReturn(mockContent);

        User mockUser = new User("testUser", "test@example.com", "securePass", mockContent);
        given(userRepository.saveAndFlush(any(User.class))).willReturn(mockUser);
        given(userMapper.toDto(any(User.class))).willReturn(new UserDto(userId, "testUser", "test@example.com", mockContentDto, true));

        // When
        UserDto result = basicUserService.create(validCreateRequest, Optional.of(validProfileRequest));

        // Then
        assertNotNull(result);
        then(userRepository).should().existsByEmail("test@example.com");
        then(userRepository).should().existsByUsername("testUser");
        then(binaryContentRepository).should().save(any(BinaryContent.class));
        then(binaryContentStorage).should().put(any(), eq(new byte[]{1, 2, 3}));
        then(userRepository).should().saveAndFlush(any(User.class));
        then(userStatusRepository).should().save(any(UserStatus.class));
    }

    /**
     * [실패] 이메일이 중복될 때 예외가 발생하는지 검증합니다.
     */
    @Test
    @DisplayName("[실패] 중복 이메일로 사용자 생성 시 예외 발생")
    void create_ShouldThrowDuplicatedUserException_WhenEmailExists() {

        // Given
        given(userRepository.existsByEmail("test@example.com")).willReturn(true);

        // When & Then
        DuplicatedUserException ex = assertThrows(DuplicatedUserException.class, () ->
                basicUserService.create(validCreateRequest, Optional.empty())
        );

        assertEquals("이미 가입된 이메일입니다.", ex.getMessage());
        then(userRepository).should().existsByEmail("test@example.com");
        then(userRepository).should(never()).saveAndFlush(any());
    }

    /**
     * [실패] 유저명이 중복될 때 예외가 발생하는지 검증합니다.
     */
    @Test
    @DisplayName("[실패] 중복 유저명으로 사용자 생성 시 예외 발생")
    void create_ShouldThrowDuplicatedUserException_WhenUsernameExists() {

        // Given
        given(userRepository.existsByEmail("test@example.com")).willReturn(false);
        given(userRepository.existsByUsername("testUser")).willReturn(true);

        // When & Then
        DuplicatedUserException ex = assertThrows(DuplicatedUserException.class, () ->
                basicUserService.create(validCreateRequest, Optional.empty())
        );

        assertEquals("이미 가입된 사용자명입니다.", ex.getMessage());
        then(userRepository).should().existsByEmail("test@example.com");
        then(userRepository).should().existsByUsername("testUser");
        then(userRepository).should(never()).saveAndFlush(any());
    }

    /**
     * 사용자 수정 관련 테스트 모음입니다.
     */
    @Nested
    @DisplayName("[유저] 수정 기능 테스트")
    class UpdateUserTests {

        @Test
        void shouldUpdateUser_WithoutProfile() {

            // Given
            given(userRepository.findById(userId)).willReturn(Optional.of(existingUser));
            given(userRepository.existsByUsername("newUser")).willReturn(false);
            given(userRepository.existsByEmail("new@example.com")).willReturn(false);
            given(userMapper.toDto(any(User.class))).willReturn(new UserDto(userId, "newUser", "new@example.com", null, true));

            // When
            UserDto result = basicUserService.update(userId, validUpdateRequest, Optional.empty());

            // Then
            assertNotNull(result);
            then(userRepository).should().findById(userId);
            then(userRepository).should().existsByUsername("newUser");
            then(userRepository).should().existsByEmail("new@example.com");
            then(binaryContentRepository).shouldHaveNoInteractions();
            then(binaryContentStorage).shouldHaveNoInteractions();
        }

        @Test
        void shouldUpdateUser_WithNewProfile() {

            // Given
            given(userRepository.findById(userId)).willReturn(Optional.of(existingUser));
            given(userRepository.existsByUsername("newUser")).willReturn(false);
            given(userRepository.existsByEmail("new@example.com")).willReturn(false);
            given(binaryContentRepository.save(any(BinaryContent.class)))
                    .willAnswer(invocation -> invocation.getArgument(0));
            given(userMapper.toDto(any(User.class))).willReturn(new UserDto(userId, "newUser", "new@example.com", null, true));

            // When
            UserDto result = basicUserService.update(userId, validUpdateRequest, Optional.of(validProfileRequest));

            // Then
            assertNotNull(result);
            then(binaryContentRepository).should().save(any(BinaryContent.class));
            then(binaryContentStorage).should().put(any(), eq(new byte[]{1, 2, 3}));
        }

        @Test
        void shouldDeleteOldProfile_WhenNewProvided() {

            // Given
            BinaryContent oldProfile = new BinaryContent("old.png", 10L, "image/png");
            oldProfile.setId(UUID.randomUUID());
            existingUser.update(existingUser.getUsername(), existingUser.getEmail(), existingUser.getPassword(), oldProfile);

            given(userRepository.findById(userId)).willReturn(Optional.of(existingUser));
            given(userRepository.existsByUsername("newUser")).willReturn(false);
            given(userRepository.existsByEmail("new@example.com")).willReturn(false);
            given(binaryContentRepository.save(any(BinaryContent.class)))
                    .willAnswer(invocation -> invocation.getArgument(0));
            given(userMapper.toDto(any(User.class))).willReturn(new UserDto(userId, "newUser", "new@example.com", null, true));

            // When
            UserDto result = basicUserService.update(userId, validUpdateRequest, Optional.of(validProfileRequest));

            // Then
            assertNotNull(result);
            then(binaryContentRepository).should().deleteById(oldProfile.getId());
            then(binaryContentRepository).should().save(any(BinaryContent.class));
            then(binaryContentStorage).should().put(any(), eq(new byte[]{1, 2, 3}));
        }

        @Test
        void shouldThrow_WhenUsernameDuplicated() {

            // Given
            given(userRepository.findById(userId)).willReturn(Optional.of(existingUser));
            given(userRepository.existsByUsername("newUser")).willReturn(true);

            // When & Then
            assertThrows(DuplicatedUserException.class, () ->
                    basicUserService.update(userId, validUpdateRequest, Optional.empty())
            );

            then(userRepository).should().existsByUsername("newUser");
            then(userRepository).should(never()).existsByEmail(any());
        }

        @Test
        void shouldThrow_WhenEmailDuplicated() {

            // Given
            given(userRepository.findById(userId)).willReturn(Optional.of(existingUser));
            given(userRepository.existsByUsername("newUser")).willReturn(false);
            given(userRepository.existsByEmail("new@example.com")).willReturn(true);

            // When & Then
            assertThrows(DuplicatedUserException.class, () ->
                    basicUserService.update(userId, validUpdateRequest, Optional.empty())
            );

            then(userRepository).should().existsByEmail("new@example.com");
        }

        @Test
        void shouldThrow_WhenUserNotFound() {

            // Given
            given(userRepository.findById(userId)).willReturn(Optional.empty());

            // When & Then
            assertThrows(UserNotFoundException.class, () ->
                    basicUserService.update(userId, validUpdateRequest, Optional.empty())
            );

            then(userRepository).should().findById(userId);
        }
    }

    @Nested
    @DisplayName("유저 삭제 테스트")
    class DeleteUserTests {

        @Test
        void shouldDelete_UserWhenExists() {

            // Given
            given(userRepository.existsById(userId)).willReturn(true);

            // When
            basicUserService.delete(userId);

            // Then
            then(userRepository).should().existsById(userId);
            then(userRepository).should().deleteById(userId);
        }

        @Test
        void shouldThrow_WhenUserNotFound() {

            // Given
            given(userRepository.existsById(userId)).willReturn(false);

            // When & Then
            assertThrows(UserNotFoundException.class, () ->
                    basicUserService.delete(userId)
            );

            then(userRepository).should().existsById(userId);
            then(userRepository).should(never()).deleteById(any());
        }
    }
}
