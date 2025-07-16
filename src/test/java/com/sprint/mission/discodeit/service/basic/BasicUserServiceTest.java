package com.sprint.mission.discodeit.service.basic;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.mock;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import com.sprint.mission.discodeit.dto.data.BinaryContentDto;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserUpdateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.user.UserAlreadyExistException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
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
    private UserMapper userMapper;
    @Mock
    private BinaryContentRepository binaryContentRepository;
    @Mock
    private BinaryContentStorage binaryContentStorage;
    @InjectMocks
    private BasicUserService userService;

    private UUID userId;
    private User user;
    private UserDto userDto;
    private BinaryContentCreateRequest binaryContentCreateRequest;
    private BinaryContent binaryContent;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        byte[] profileBytes = "testprofile.jpg".getBytes();
        binaryContent = new BinaryContent("testprofile.jpg", 10L, "image/jpeg");
        binaryContentCreateRequest = new BinaryContentCreateRequest("testprofile.jpg", "image/jpeg",
            profileBytes);

        BinaryContentDto profileDto = new BinaryContentDto(
            UUID.randomUUID(),
            "testprofile.jpg",
            10L,
            "image/jpeg"
        );

        userDto = new UserDto(
            UUID.randomUUID(), "username", "test@example.com", profileDto, null);

        //update 테스트를 위한 User
        userId = UUID.randomUUID();
        user = new User("oldUsername", "old@example.com", "oldPass", null);

    }

    @Test
    @DisplayName("사용자 생성 성공")
    void createUserSuccess() {
        // given
        UserCreateRequest request = new UserCreateRequest("username", "test@example.com",
            "password");
        Optional<BinaryContentCreateRequest> optionalProfile = Optional.of(
            binaryContentCreateRequest);

        //userRepository에는 현재 어떠한 데이터도 존재하지 않음
        given(userRepository.existsByEmail(anyString())).willReturn(false);
        given(userRepository.existsByUsername(anyString())).willReturn(false);

        given(binaryContentRepository.save(any(BinaryContent.class))).willReturn(binaryContent);
        given(userMapper.toDto(any(User.class))).willReturn(userDto);

        // when
        UserDto result = userService.create(request, optionalProfile);

        // then
        assertThat(result).isNotNull();
        assertThat(result.username()).isEqualTo(request.username());
        assertThat(result.email()).isEqualTo(request.email());

        assertThat(result.profile()).isNotNull();
        assertThat(result.profile().fileName()).isEqualTo("testprofile.jpg");

        verify(userRepository).save(any(User.class));
        verify(binaryContentRepository).save(any(BinaryContent.class));
    }

    @Test
    @DisplayName("중복 이메일로 사용자 생성 실패")
    void createUserEmailDuplicate() {
        // given
        UserCreateRequest request = new UserCreateRequest("username", "duplicate@example.com",
            "password");

        //request.email 이 repo에 있다고 true값 반환
        given(userRepository.existsByEmail(request.email())).willReturn(true);
        given(userRepository.existsByUsername(request.username())).willReturn(false);

        // when / then
        assertThatThrownBy(() -> userService.create(request, Optional.empty()))
            .isInstanceOf(UserAlreadyExistException.class)
            .hasMessageContaining("사용자 이메일이 중복됩니다. 다른 이메일을 사용해주세요.");
    }

    @Test
    @DisplayName("중복 사용자명으로 사용자 생성 실패")
    void createUserUsernameDuplicate() {
        // given
        UserCreateRequest request = new UserCreateRequest("username", "test@example.com",
            "password");
        given(userRepository.existsByEmail(request.email())).willReturn(false);
        given(userRepository.existsByUsername(request.username())).willReturn(true);

        // when / then
        assertThatThrownBy(() -> userService.create(request, Optional.empty()))
            .isInstanceOf(UserAlreadyExistException.class)
            .hasMessageContaining("사용자명이 중복됩니다. 다른 사용자명을 사용해주세요.");
    }

    @Test
    @DisplayName("존재하지 않는 사용자 조회 실패")
    void findUserNotFound() {
        // given
        UUID userId = UUID.randomUUID();
        given(userRepository.findById(userId)).willReturn(Optional.empty());

        // when / then
        assertThatThrownBy(() -> userService.find(userId))
            .isInstanceOf(UserNotFoundException.class);
    }

    @Test
    @DisplayName("사용자 수정 성공")
    void update() {
        // given
        UserUpdateRequest request = new UserUpdateRequest("newUsername", "new@example.com",
            "newPass");

        given(userRepository.findById(userId)).willReturn(Optional.of(user));

        //수정한 이메일, 이름 없다고 가정
        given(userRepository.existsByEmail("new@example.com")).willReturn(false);
        given(userRepository.existsByUsername("newUsername")).willReturn(false);

        given(userMapper.toDto(any(User.class))).willReturn(mock(UserDto.class));

        // when
        UserDto result = userService.update(userId, request, Optional.empty());

        // then
        assertThat(result).isNotNull();
        then(userRepository).should().findById(userId);
        then(userRepository).should(never()).save(any());

    }

    @Test
    @DisplayName("사용자 수정 실패 - 존재하지 않는 User")
    void update_notFound() {
        // given
        given(userRepository.findById(userId)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> userService.update(userId, new UserUpdateRequest("a", "b", "c"),
            Optional.empty()))
            .isInstanceOf(UserNotFoundException.class);
    }

    @Test
    @DisplayName("모든 사용자 조회 성공")
    void findAll() {
        // given
        given(userRepository.findAllWithProfileAndStatus()).willReturn(List.of(user));
        given(userMapper.toDto(user)).willReturn(mock(UserDto.class));

        // when
        List<UserDto> result = userService.findAll();

        // then
        assertThat(result).isNotEmpty();
        then(userRepository).should().findAllWithProfileAndStatus();
    }

    @Test
    @DisplayName("사용자 삭제 성공")
    void delete() {
        // given
        given(userRepository.existsById(userId)).willReturn(true);

        // when
        userService.delete(userId);

        // then
        then(userRepository).should().deleteById(userId);
    }

    @Test
    @DisplayName("사용자 삭제 실패 - 존재하지 않음")
    void delete_notFound() {
        // given
        given(userRepository.existsById(userId)).willReturn(false);

        // when & then
        assertThatThrownBy(() -> userService.delete(userId))
            .isInstanceOf(UserNotFoundException.class);
    }
}
