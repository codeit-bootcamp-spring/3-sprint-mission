package com.sprint.mission.discodeit.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.dto.data.MessageDto;
import com.sprint.mission.discodeit.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageUpdateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class MessageIntegrationTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserStatusRepository userStatusRepository;
    @Autowired
    private ChannelRepository channelRepository;
    @Autowired
    private MessageRepository messageRepository;

    private User author;
    private Channel channel;
    private UserStatus status;

    @BeforeEach
    void setup() {
        author = userRepository.save(new User("author", "author@example.com", "password", null));
        status = new UserStatus(author, Instant.now());
        status = userStatusRepository.save(status);
        channel = channelRepository.save(new Channel(ChannelType.PUBLIC, "channel", "desc"));
    }

    @Test
    @DisplayName("메시지 생성 성공 - 첨부파일 포함")
    void createMessage_withAttachment_success() throws Exception {
        // Given
        MessageCreateRequest messageCreateRequest = new MessageCreateRequest(
            "Hello, this is a message.",
            channel.getId(),
            author.getId()
        );

        MockMultipartFile requestPart = new MockMultipartFile(
            "messageCreateRequest",
            "",
            MediaType.APPLICATION_JSON_VALUE,
            objectMapper.writeValueAsBytes(messageCreateRequest)
        );

        MockMultipartFile file = new MockMultipartFile(
            "attachments",
            "hello.txt",
            MediaType.TEXT_PLAIN_VALUE,
            "Hello File!".getBytes(StandardCharsets.UTF_8)
        );

        // When
        ResultActions result = mockMvc.perform(
            multipart("/api/messages")
                .file(requestPart)
                .file(file)
                .contentType(MediaType.MULTIPART_FORM_DATA)
        );

        // Then
        result.andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").exists())
            .andExpect(jsonPath("$.content").value("Hello, this is a message."))
            .andExpect(jsonPath("$.author.id").value(author.getId().toString()))
            .andExpect(jsonPath("$.channelId").value(channel.getId().toString()));
    }

    @Test
    @DisplayName("메시지 생성 성공 - 첨부파일 미포함")
    void createMessage_success() throws Exception {
        // Given
        MessageCreateRequest request = new MessageCreateRequest("Hello world", channel.getId(),
            author.getId());
        String json = objectMapper.writeValueAsString(request);

        MockMultipartFile requestPart = new MockMultipartFile(
            "messageCreateRequest", "", "application/json",
            json.getBytes(StandardCharsets.UTF_8)
        );

        // When
        ResultActions result = mockMvc.perform(multipart("/api/messages")
            .file(requestPart)
            .contentType(MediaType.MULTIPART_FORM_DATA));

        // Then
        result.andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").exists())
            .andExpect(jsonPath("$.content").value("Hello world"))
            .andExpect(jsonPath("$.channelId").value(channel.getId().toString()))
            .andExpect(jsonPath("$.author.id").value(author.getId().toString()));
    }

    @Test
    @DisplayName("메시지 수정 성공")
    void updateMessage_success() throws Exception {
        // Given
        MessageCreateRequest createRequest = new MessageCreateRequest("original", channel.getId(),
            author.getId());
        MessageDto created = createTestMessage(createRequest);

        MessageUpdateRequest updateRequest = new MessageUpdateRequest("updated content");

        // When
        ResultActions result = mockMvc.perform(patch("/api/messages/" + created.id())
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(updateRequest)));

        // Then
        result.andExpect(status().isOk())
            .andExpect(jsonPath("$.content").value("updated content"));
    }

    @Test
    @DisplayName("메시지 삭제 성공")
    void deleteMessage_success() throws Exception {
        // Given
        MessageDto created = createTestMessage(
            new MessageCreateRequest("to delete", channel.getId(), author.getId()));

        // When
        ResultActions result = mockMvc.perform(delete("/api/messages/" + created.id()));

        // Then
        result.andExpect(status().isNoContent());
        assertThat(messageRepository.existsById(created.id())).isFalse();
    }

    @Test
    @DisplayName("채널의 메시지 목록 조회")
    void findAllByChannelId_success() throws Exception {
        // Given
        for (int i = 0; i < 3; i++) {
            createTestMessage(new MessageCreateRequest("msg" + i, channel.getId(), author.getId()));
        }

        // When
        ResultActions result = mockMvc.perform(get("/api/messages")
            .param("channelId", channel.getId().toString()));

        // Then
        result.andExpect(status().isOk())
            .andExpect(jsonPath("$.content.length()").value(3));
    }

    private MessageDto createTestMessage(MessageCreateRequest request) throws Exception {
        String json = objectMapper.writeValueAsString(request);

        MockMultipartFile req = new MockMultipartFile(
            "messageCreateRequest", "messageCreateRequest.json", "application/json",
            json.getBytes(StandardCharsets.UTF_8)
        );

        String response = mockMvc.perform(multipart("/api/messages")
                .file(req)
                .contentType(MediaType.MULTIPART_FORM_DATA))
            .andExpect(status().isCreated())
            .andReturn().getResponse().getContentAsString();

        return objectMapper.readValue(response, MessageDto.class);
    }
}