package com.sprint.mission.discodeit.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;

import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserUpdateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.storage.local.LocalBinaryContentStorage;
import java.io.IOException;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.method.AuthorizationManagerBeforeMethodInterceptor;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@ActiveProfiles("test")
@SpringBootTest(properties = "spring.main.allow-bean-definition-overriding=true")
@Transactional
@DisplayName("UserService 통합 테스트")
public class UserServiceIntegrationTest {

    @Autowired private UserService userService;
    @Autowired private UserRepository userRepository;
    @Autowired private BinaryContentRepository binaryContentRepository;

    @TestConfiguration
    static class AllowAllMethodSecurity {
        @Bean("preAuthorizeAuthorizationMethodInterceptor")
        @Primary
        AuthorizationManagerBeforeMethodInterceptor preAuthorizeAllowAll() {
            return AuthorizationManagerBeforeMethodInterceptor.preAuthorize(
                (auth, mi) -> new AuthorizationDecision(true)
            );
        }
    }

    @Test
    @DisplayName("모든 계층의 사용자 생성 프로세스")
    void completeUserCreateProcessIntegration() throws IOException {
        // given
        UserCreateRequest userCreateRequest = new UserCreateRequest("tom", "tom@test.com", "pw123456");
        MultipartFile multipartFile = new MockMultipartFile("profile", "profile.png", "image/png", "test-bytes".getBytes());

        BinaryContentCreateRequest binaryContentCreateRequest = new BinaryContentCreateRequest(
            multipartFile.getOriginalFilename(), multipartFile.getContentType(), multipartFile.getBytes()
        );

        // when
        UserDto userDto = userService.create(userCreateRequest, Optional.of(binaryContentCreateRequest));

        assertThat(userDto.profile()).isNotNull();
        assertThat(userDto.profile().fileName()).isEqualTo("profile.png");
        assertThat(userDto.profile().contentType()).isEqualTo("image/png");

        // then
        BinaryContent saved = binaryContentRepository
            .findById(userDto.profile().id())
            .orElseThrow();
        assertThat(saved.getFileName()).isEqualTo("profile.png");
    }

    @Test
    @DisplayName("모든 계층의 사용자 정보 업데이트 프로세스")
    void completeUserUpdateProcessIntegration() throws IOException {
        UserCreateRequest createReq =
            new UserCreateRequest("tom", "tom@test.com", "pw123456");
        MultipartFile initFile =
            new MockMultipartFile("profile", "init.png", "image/png", "initContent".getBytes());
        BinaryContentCreateRequest initBinaryReq =
            new BinaryContentCreateRequest(
                initFile.getOriginalFilename(),
                initFile.getContentType(),
                initFile.getBytes()
            );
        UserDto created = userService.create(createReq, Optional.of(initBinaryReq));

        UserUpdateRequest updateReq =
            new UserUpdateRequest("tommy", "tommy@test.com", "pw223456");
        MultipartFile newFile =
            new MockMultipartFile("profile", "updated.png", "image/png", "updateContent".getBytes());
        BinaryContentCreateRequest newBinaryReq =
            new BinaryContentCreateRequest(
                newFile.getOriginalFilename(),
                newFile.getContentType(),
                newFile.getBytes()
            );

        // when
        UserDto updated = userService.update(
            created.id(),
            updateReq,
            Optional.of(newBinaryReq)
        );

        // then
        assertThat(updated.username()).isEqualTo("tommy");
        assertThat(updated.email()).isEqualTo("tommy@test.com");
        assertThat(updated.profile()).isNotNull();
        assertThat(updated.profile().fileName()).isEqualTo("updated.png");

        BinaryContent savedNew = binaryContentRepository
            .findById(updated.profile().id())
            .orElseThrow();
        assertThat(savedNew.getFileName()).isEqualTo("updated.png");
    }

    @Test
    @DisplayName("모든 계층의 사용자 삭제 프로세스")
    void completeUserDeleteProcessIntegration() throws IOException {
        UserCreateRequest createReq =
            new UserCreateRequest("tom", "tom@test.com", "pw123456");
        UserDto created = userService.create(createReq, Optional.empty());
        UUID userId = created.id();

        // when
        userService.delete(userId);

        // then
        assertFalse(userRepository.findById(userId).isPresent());
    }
}
