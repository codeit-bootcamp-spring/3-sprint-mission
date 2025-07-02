package com.sprint.mission.discodeit.integration;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageUpdateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.UserService;
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
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class MessageIntegrationTest {

    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    UserService userService;
    @Autowired
    ChannelService channelService;

    UUID channelId;
    UUID userId;

    @BeforeEach
    void setUp() {
        PublicChannelCreateRequest channelRequest = new PublicChannelCreateRequest("channel",
            "desc");
        channelId = channelService.createPublicChannel(channelRequest).id();

        UserCreateRequest userRequest = new UserCreateRequest("user", "user@email.com", "password");
        userId = userService.create(userRequest, Optional.empty()).id();
    }

    @Test
    @DisplayName("메시지 생성 성공")
    void shouldCreateMessageSuccessfully() throws Exception {
        MessageCreateRequest request = new MessageCreateRequest("헬로 메시지", channelId, userId);
        MockMultipartFile messagePart = new MockMultipartFile(
            "messageCreateRequest", "", MediaType.APPLICATION_JSON_VALUE,
            objectMapper.writeValueAsBytes(request)
        );

        mockMvc.perform(multipart("/api/messages").file(messagePart))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.content", is("헬로 메시지")))
            .andExpect(jsonPath("$.channelId", is(channelId.toString())))
            .andExpect(jsonPath("$.author.id", is(userId.toString())));
    }

    @Test
    @DisplayName("메시지 수정 성공")
    void shouldUpdateMessageSuccessfully() throws Exception {
        MessageCreateRequest create = new MessageCreateRequest("수정 전", channelId, userId);
        MockMultipartFile messagePart = new MockMultipartFile(
            "messageCreateRequest", "", MediaType.APPLICATION_JSON_VALUE,
            objectMapper.writeValueAsBytes(create)
        );
        MvcResult result = mockMvc.perform(multipart("/api/messages").file(messagePart))
            .andExpect(status().isCreated())
            .andReturn();
        UUID messageId = UUID.fromString(
            objectMapper.readTree(result.getResponse().getContentAsString()).get("id").asText()
        );

        MessageUpdateRequest update = new MessageUpdateRequest("수정 후");
        String json = objectMapper.writeValueAsString(update);

        mockMvc.perform(patch("/api/messages/{id}", messageId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content", is("수정 후")))
            .andExpect(jsonPath("$.id", is(messageId.toString())));
    }

    @Test
    @DisplayName("메시지 삭제 성공")
    void shouldDeleteMessageSuccessfully() throws Exception {
        MessageCreateRequest request = new MessageCreateRequest("삭제할 메시지", channelId, userId);
        MockMultipartFile messagePart = new MockMultipartFile(
            "messageCreateRequest", "", MediaType.APPLICATION_JSON_VALUE,
            objectMapper.writeValueAsBytes(request)
        );
        MvcResult result = mockMvc.perform(multipart("/api/messages").file(messagePart))
            .andExpect(status().isCreated())
            .andReturn();
        UUID messageId = UUID.fromString(
            objectMapper.readTree(result.getResponse().getContentAsString()).get("id").asText()
        );

        mockMvc.perform(delete("/api/messages/{id}", messageId))
            .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/messages")
                .param("channelId", channelId.toString())
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content", hasSize(0)));
    }

    @Test
    @DisplayName("메시지 목록 조회 성공")
    void shouldGetMessageListSuccessfully() throws Exception {
        for (int i = 1; i <= 2; i++) {
            MessageCreateRequest req = new MessageCreateRequest("메시지" + i, channelId, userId);
            MockMultipartFile part = new MockMultipartFile(
                "messageCreateRequest", "", MediaType.APPLICATION_JSON_VALUE,
                objectMapper.writeValueAsBytes(req)
            );
            mockMvc.perform(multipart("/api/messages").file(part))
                .andExpect(status().isCreated());
        }

        mockMvc.perform(get("/api/messages")
                .param("channelId", channelId.toString())
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content", hasSize(2)))
            .andExpect(jsonPath("$.content[0].content", is("메시지2")))
            .andExpect(jsonPath("$.content[1].content", is("메시지1")));
    }
}
