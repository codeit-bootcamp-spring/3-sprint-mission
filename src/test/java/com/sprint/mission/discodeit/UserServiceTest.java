package com.sprint.mission.discodeit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;

import com.sprint.mission.discodeit.dto.BinaryContentDto;
import com.sprint.mission.discodeit.dto.UserDto;
import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.basic.BasicUserService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserService 단위 테스트")
public class UserServiceTest {

    @InjectMocks
    private BasicUserService userService;
    @Mock
    private UserRepository userRepository;
    @Mock
    private BinaryContentRepository binaryContentRepository;
    @Mock
    private UserStatusRepository userStatusRepository;
    @Mock
    private BinaryContentStorage binaryContentStorage;
    @Mock
    private UserMapper userMapper;

    @Test
    @DisplayName("프로필이미지가 있는 유저를 생성할 수 있다.")
    void 유저_생성_프로필_성공() {

        // given
        String name = "testuser";
        String email = "testuser@abc.com";
        String password = "1234";
        UserCreateRequest userCreateRequest = new UserCreateRequest(name, email, password);
        byte[] imageBytes = "이미지".getBytes(StandardCharsets.UTF_8);

        BinaryContentCreateRequest profileRequest = new BinaryContentCreateRequest("img.png",
            (long) imageBytes.length, "image/png", imageBytes);
        UUID profileId = UUID.randomUUID();
        BinaryContent profileEntity = new BinaryContent("img.png", (long) imageBytes.length,
            "image/png");
        ReflectionTestUtils.setField(profileEntity, "id", profileId);

        User userEntity = new User(name, email, password, profileEntity);
        UUID userId = UUID.randomUUID();
        ReflectionTestUtils.setField(userEntity, "id", userId);

        BinaryContentDto profileDto = new BinaryContentDto(profileId, "img.png",
            (long) imageBytes.length, "image/png");
        UserDto expectedDto = new UserDto(userId, name, email, profileDto, true);

        given(userRepository.existsByEmail(email)).willReturn(false);
        given(userRepository.existsByUsername(name)).willReturn(false);
        given(binaryContentRepository.save(any(BinaryContent.class))).willReturn(profileEntity);
        given(binaryContentStorage.put(eq(profileId), eq(imageBytes))).willReturn(profileId);
        given(userRepository.save(any(User.class))).willReturn(userEntity);
        given(userStatusRepository.save(any(UserStatus.class))).willReturn(any());
        given(userMapper.toDto(userEntity)).willReturn(expectedDto);

        // when
        UserDto result = userService.createUser(userCreateRequest, Optional.of(profileRequest));

        // then
        assertThat(result).isNotNull();
        assertThat(result.username()).isEqualTo(name);
        assertThat(result.email()).isEqualTo(email);
        assertThat(result.profile()).isNotNull();
        assertThat(result.profile().id()).isEqualTo(profileId);
    }

}

