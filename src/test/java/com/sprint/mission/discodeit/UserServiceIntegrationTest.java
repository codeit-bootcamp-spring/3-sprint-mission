package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@DisplayName("유저 서비스 통합 테스트")
public class UserServiceIntegrationTest {

    @Autowired private UserService userService;
    @Autowired private UserRepository userRepository;
    @Autowired private BinaryContentRepository binaryContentRepository;
    @Autowired private UserStatusRepository userStatusRepository;

    /*
    * 유저 생성 프로세스가 모든 계층에서 올바르게 동작하는지 검증한다.
    * 유서 생성 시 BinaryContent(프로필 이미지), UserStatus 생성 까지의
    * 전체 비즈니스 플로우를 데이터베이스와 함께 테스트한다.
    * */
    @Test
    @DisplayName("유저 생성 프로세스가 모든 계층에서 올바르게 동작해야 한다")
    @Transactional
    void completeUserCreateProcessIntegration() {
        // given
        UserCreateRequest userCreateRequest = new UserCreateRequest("test", "test@gmail.com", "test1234");
        MultipartFile multipartFile = new MockMultipartFile("file", "test.txt", "text/plain", "test".getBytes());
        BinaryContentCreateRequest binaryContentCreateRequest;

        try {
            binaryContentCreateRequest = new BinaryContentCreateRequest(multipartFile.getName(), multipartFile.getContentType(), multipartFile.getBytes());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // when
        UserDto userDto = userService.create(userCreateRequest, Optional.of(binaryContentCreateRequest));

        // then
        User findUser = userRepository.findById(userDto.id()).orElseThrow();

        assertThat(userDto.username()).isEqualTo(findUser.getUsername());

        BinaryContent findUserProfile = binaryContentRepository.findById(userDto.profile().id()).orElseThrow();
        assertThat(userDto.profile().fileName()).isEqualTo(findUserProfile.getFileName());

        UserStatus findUserStatus = userStatusRepository.findByUserId(userDto.id()).orElseThrow();
        assertThat(findUserStatus.getUser().getId()).isEqualTo(userDto.id());
    }
}
