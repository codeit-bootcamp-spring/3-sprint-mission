package com.sprint.mission.discodeit.integration;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import com.sprint.mission.discodeit.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageUpdateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import java.time.Instant;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
@DisplayName("메시지 API 통합 테스트")
public class MessageIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ChannelRepository channelRepository;

    @Test
    @DisplayName("메시지 생성 시 Repository까지 반영되어야 한다")
    void createMessage_Integration_Success() throws Exception {
        // Given
        UUID userId = createUserAndGetId("testuser", "test@example.com");
        UUID channelId = createChannelAndGetId("test-channel", "Test Channel");

        MessageCreateRequest messageCreateRequest = new MessageCreateRequest(
            "Hello, World!",
            channelId,
            userId
        );

        MockMultipartFile messageCreateRequestPart
            = toJsonPart("messageCreateRequest", messageCreateRequest);

        MockMultipartFile profilePart = fakeImagePart();

        // When & Then
        mockMvc.perform(multipart("/api/messages")
                .file(messageCreateRequestPart)
                .file(profilePart)
                .contentType(MediaType.MULTIPART_FORM_DATA))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.content").value("Hello, World!"))
            .andExpect(jsonPath("$.channelId").value(channelId.toString()));

        assertThat(messageRepository.findAll().size()).isEqualTo(1);
        Message savedMessage = messageRepository.findAll().get(0);
        assertThat(savedMessage.getContent()).isEqualTo("Hello, World!");
        assertThat(savedMessage.getChannel().getId()).isEqualTo(channelId);
        assertThat(savedMessage.getAuthor().getId()).isEqualTo(userId);
    }

    @Test
    @DisplayName("존재하지 않는 채널에 메시지 생성 시 400이 반환되어야 한다")
    void createMessage_Integration_ChannelNotFound_Failure() throws Exception {
        // Given
        UUID userId = createUserAndGetId("testuser", "test@example.com");
        UUID nonExistentChannelId = UUID.randomUUID();

        MessageCreateRequest messageCreateRequest = new MessageCreateRequest(
            "Hello, World!",
            nonExistentChannelId,
            userId
        );

        MockMultipartFile messageCreateRequestPart
            = toJsonPart("messageCreateRequest", messageCreateRequest);

        MockMultipartFile profilePart = fakeImagePart();

        // When & Then
        mockMvc.perform(multipart("/api/messages")
                .file(messageCreateRequestPart)
                .file(profilePart)
                .contentType(MediaType.MULTIPART_FORM_DATA))
            .andExpect(status().isNotFound());

        assertThat(messageRepository.findAll().size()).isEqualTo(0);
    }

    @Test
    @DisplayName("메시지 수정 시 Repository까지 반영되어야 한다")
    void updateMessage_Integration_Success() throws Exception {
        // Given
        UUID userId = createUserAndGetId("testuser", "test@example.com");
        UUID channelId = createChannelAndGetId("test-channel", "Test Channel");
        UUID messageId = createMessageAndGetId("Original message", channelId, userId);

        MessageUpdateRequest messageUpdateRequest = new MessageUpdateRequest("Updated message");

        // When & Then
        mockMvc.perform(patch("/api/messages/{messageId}", messageId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(messageUpdateRequest)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content").value("Updated message"));

        Message updatedMessage = messageRepository.findById(messageId).orElseThrow();
        assertThat(updatedMessage.getContent()).isEqualTo("Updated message");
    }

    @Test
    @DisplayName("존재하지 않는 메시지 수정 시 404가 반환되어야 한다")
    void updateMessage_Integration_NotFound_Failure() throws Exception {
        // Given
        MessageUpdateRequest messageUpdateRequest = new MessageUpdateRequest("Updated message");

        // When & Then
        mockMvc.perform(patch("/api/messages/{messageId}", UUID.randomUUID())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(messageUpdateRequest)))
            .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("메시지를 삭제하면 Repository에서 제거되어야 한다")
    void deleteMessage_Integration_Success() throws Exception {
        // Given
        UUID userId = createUserAndGetId("testuser", "test@example.com");
        UUID channelId = createChannelAndGetId("test-channel", "Test Channel");
        UUID messageId = createMessageAndGetId("Delete this message", channelId, userId);

        // When & Then
        mockMvc.perform(delete("/api/messages/{messageId}", messageId))
            .andExpect(status().isNoContent());

        boolean exists = messageRepository.existsById(messageId);
        assertThat(exists).isFalse();
    }

    @Test
    @DisplayName("존재하지 않는 메시지를 삭제하면 404 Not Found가 발생해야 한다")
    void deleteMessage_Integration_NotFound_Failure() throws Exception {
        // Given
        UUID messageId = UUID.randomUUID();

        // When & Then
        mockMvc.perform(delete("/api/messages/{messageId}", messageId))
            .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("채널의 메시지 목록 조회 시 해당 채널의 메시지들이 반환되어야 한다")
    void getMessagesByChannel_Integration_Success() throws Exception {
        // Given
        UUID userId = createUserAndGetId("testuser", "test@example.com");
        UUID channelId = createChannelAndGetId("test-channel", "Test Channel");

        createMessageAndGetId("First message", channelId, userId);
        createMessageAndGetId("Second message", channelId, userId);

        // When & Then
        mockMvc.perform(get("/api/messages")
                .param("channelId", channelId.toString()))
            .andExpect(status().isOk());

        assertThat(messageRepository.findAll().size()).isEqualTo(2);
    }

    @Test
    @DisplayName("메시지가 없는 채널 조회 시 빈 배열이 반환되어야 한다")
    void getMessagesByChannel_Integration_EmptyList() throws Exception {
        // Given
        UUID channelId = createChannelAndGetId("empty-channel", "Empty Channel");

        // When & Then
        mockMvc.perform(get("/api/messages")
                .param("channelId", channelId.toString()))
            .andExpect(status().isOk());

        assertThat(messageRepository.findAll().size()).isEqualTo(0);
    }


    private UUID createMessageAndGetId(String content, UUID channelId, UUID authorId)
        throws Exception {
        MessageCreateRequest request = new MessageCreateRequest(content, channelId, authorId);
        MockMultipartFile requestPart = toJsonPart("messageCreateRequest", request);

        String body = mockMvc.perform(multipart("/api/messages")
                .file(requestPart))
            .andReturn()
            .getResponse()
            .getContentAsString();

        return UUID.fromString(JsonPath.read(body, "$.id"));
    }

    private UUID createUserAndGetId(String username, String email) {
        User user = new User(username, email, "!password123", null);

        initializeUserStatus(user);

        User savedUser = userRepository.save(user);
        return savedUser.getId();
    }

    private void initializeUserStatus(User user) {
        UserStatus userStatus = new UserStatus(user, Instant.now());
    }

    private UUID createChannelAndGetId(String name, String description) {
        Channel channel = new Channel(ChannelType.PUBLIC, name, description);
        Channel savedChannel = channelRepository.save(channel);
        return savedChannel.getId();
    }

    private MockMultipartFile toJsonPart(String partName, Object value) throws Exception {
        return new MockMultipartFile(
            partName,
            partName + ".json",
            "application/json",
            objectMapper.writeValueAsBytes(value)
        );
    }

    private MockMultipartFile fakeImagePart() {
        return new MockMultipartFile(
            "profile",
            "image.png",
            MediaType.IMAGE_PNG_VALUE,
            "fake-image-content".getBytes()
        );
    }


}
