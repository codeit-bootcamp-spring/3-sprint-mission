package com.sprint.mission.discodeit;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@ActiveProfiles("test")
@AutoConfigureMockMvc
@SpringBootTest
@DisplayName("유저 서비스 통합 테스트")
public class UserServiceIntegrationTest {

    @Autowired private UserService userService;
    @Autowired private UserRepository userRepository;
    @Autowired private BinaryContentRepository binaryContentRepository;
    @Autowired private UserStatusRepository userStatusRepository;
    @Autowired private MockMvc mockMvc;

    /*
    * 유저 생성 프로세스가 모든 계층에서 올바르게 동작하는지 검증한다.
    * 유서 생성 시 BinaryContent(프로필 이미지), UserStatus 생성 까지의
    * 전체 비즈니스 플로우를 데이터베이스와 함께 테스트한다.
    * */
    @Test
    @DisplayName("유저 생성 프로세스가 모든 계층에서 올바르게 동작해야 한다")
    @Transactional
    void completeUserCreateProcessIntegration() throws Exception {
        // given
        UserCreateRequest request = new UserCreateRequest("test", "test@gmail.com", "test1234");

        // Jackson으로 JSON 직렬화
        String json = new ObjectMapper().writeValueAsString(request);

        MockMultipartFile jsonPart = new MockMultipartFile(
                "userCreateRequest",
                "",
                "application/json",
                json.getBytes(StandardCharsets.UTF_8)
        );

        MockMultipartFile filePart = new MockMultipartFile(
                "profile",
                "test.txt",
                "text/plain",
                "test".getBytes(StandardCharsets.UTF_8)
        );

        // when & then
        MvcResult result = mockMvc.perform(multipart("/api/users")
                        .file(jsonPart)
                        .file(filePart)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username").value("test"))
                .andExpect(jsonPath("$.profile.fileName").value("test.txt"))
                .andReturn();

        // then - 응답에서 userId 추출해서 DB 검증
        String responseBody = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode responseJson = objectMapper.readTree(responseBody);
        UUID userId = UUID.fromString(responseJson.get("id").asText());
        UUID profileId = UUID.fromString(responseJson.get("profile").get("id").asText());

        User user = userRepository.findById(userId).orElseThrow();
        assertThat(user.getUsername()).isEqualTo("test");

        BinaryContent profile = binaryContentRepository.findById(profileId).orElseThrow();
        assertThat(profile.getFileName()).isEqualTo("test.txt");

        UserStatus userStatus = userStatusRepository.findByUserId(userId).orElseThrow();
        assertThat(userStatus.getUser().getId()).isEqualTo(userId);
    }
}
