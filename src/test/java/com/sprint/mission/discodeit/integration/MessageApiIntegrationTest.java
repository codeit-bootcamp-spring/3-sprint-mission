package com.sprint.mission.discodeit.integration;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.dto.ChannelDto;
import com.sprint.mission.discodeit.dto.MessageDto;
import com.sprint.mission.discodeit.dto.UserDto;
import com.sprint.mission.discodeit.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageUpdateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;
import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
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
class MessageApiIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MessageService messageService;

    @Autowired
    private ChannelService channelService;

    @Autowired
    private UserService userService;

    private ChannelDto channel;
    private UserDto user;
    private PublicChannelCreateRequest publicChannelCreateRequest;
    private UserCreateRequest userCreateRequest;
    private MessageCreateRequest messageCreateRequest;
    private MessageUpdateRequest messageUpdateRequest;
    private MockMultipartFile attachmentFile;

    @BeforeEach
    void setUp() {
        publicChannelCreateRequest = new PublicChannelCreateRequest("공개 채널", "공개 채널 입니다.");
        userCreateRequest = new UserCreateRequest("testuser", "test@abc.com", "1234");

        channel = channelService.create(publicChannelCreateRequest);
        user = userService.createUser(userCreateRequest, Optional.empty());

        messageCreateRequest = new MessageCreateRequest("hello", channel.id(), user.id());
        messageUpdateRequest = new MessageUpdateRequest("newContent");

        attachmentFile = new MockMultipartFile(
            "attachments", "image.jpg", MediaType.IMAGE_JPEG_VALUE, "이미지".getBytes()
        );
    }

    @Test
    @DisplayName("메시지 생성 성공")
    void createMessage_Success() throws Exception {
        MockMultipartFile messageCreateRequestPart = new MockMultipartFile(
            "messageCreateRequest",
            "",
            MediaType.APPLICATION_JSON_VALUE,
            objectMapper.writeValueAsBytes(messageCreateRequest)
        );

        mockMvc.perform(multipart("/api/messages")
                .file(messageCreateRequestPart)
                .file(attachmentFile))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id", notNullValue()))
            .andExpect(jsonPath("$.content").value("hello"))
            .andExpect(jsonPath("$.channelId").value(channel.id().toString()))
            .andExpect(jsonPath("$.author.id").value(user.id().toString()))
            .andExpect(jsonPath("$.attachments", hasSize(1)))
            .andExpect(jsonPath("$.attachments[0].fileName").value("image.jpg"));
    }

    @Test
    @DisplayName("메시지 생성 실패")
    void createMessage_Failure() throws Exception {
        MessageCreateRequest invalidRequest = new MessageCreateRequest("", channel.id(), user.id());

        MockMultipartFile messageCreateRequestPart = new MockMultipartFile(
            "messageCreateRequest",
            "",
            MediaType.APPLICATION_JSON_VALUE,
            objectMapper.writeValueAsBytes(invalidRequest)
        );

        mockMvc.perform(multipart("/api/messages")
                .file(messageCreateRequestPart))
            .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("채널별 메시지 조회 성공")
    void findAllMessagesByChannelId_Success() throws Exception {
        MessageCreateRequest messageCreateRequest1 = new MessageCreateRequest("hi", channel.id(),
            user.id());

        messageService.createMessage(messageCreateRequest, new ArrayList<>());
        messageService.createMessage(messageCreateRequest1, new ArrayList<>());

        mockMvc.perform(get("/api/messages")
                .param("channelId", channel.id().toString())
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content", hasSize(2)))
            .andExpect(jsonPath("$.content[0].content").value("hi"))
            .andExpect(jsonPath("$.content[1].content").value("hello"))
            .andExpect(jsonPath("$.size").exists())
            .andExpect(jsonPath("$.hasNext").exists())
            .andExpect(jsonPath("$.totalElements").isEmpty());
    }

    @Test
    @DisplayName("메시지 수정 성공")
    void updateMessage_Success() throws Exception {
        MessageDto createdMessage = messageService.createMessage(messageCreateRequest,
            new ArrayList<>());
        UUID messageId = createdMessage.id();

        mockMvc.perform(patch("/api/messages/{messageId}", messageId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(messageUpdateRequest)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(messageId.toString()))
            .andExpect(jsonPath("$.content").value("newContent"))
            .andExpect(jsonPath("$.updatedAt").exists());
    }

    @Test
    @DisplayName("메시지 수정 실패 ")
    void updateMessage_Failure() throws Exception {
        UUID nonExistentMessageId = UUID.randomUUID();

        mockMvc.perform(patch("/api/messages/{messageId}", nonExistentMessageId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(messageUpdateRequest)))
            .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("메시지 삭제 성공")
    void deleteMessage_Success() throws Exception {
        MessageDto createdMessage = messageService.createMessage(messageCreateRequest,
            new ArrayList<>());
        UUID messageId = createdMessage.id();

        mockMvc.perform(delete("/api/messages/{messageId}", messageId)
                .param("senderId", user.id().toString()))
            .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("메시지 삭제 실패")
    void deleteMessage_Failure() throws Exception {
        UUID nonExistentMessageId = UUID.randomUUID();

        mockMvc.perform(delete("/api/messages/{messageId}", nonExistentMessageId)
                .param("senderId", user.id().toString()))
            .andExpect(status().isNotFound());
    }

}