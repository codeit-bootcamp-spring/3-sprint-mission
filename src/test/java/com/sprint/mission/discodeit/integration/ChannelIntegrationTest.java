package com.sprint.mission.discodeit.integration;

import static org.hamcrest.Matchers.is;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelUpdateRequest;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
@WithMockUser(username = "manager", roles = {"CHANNEL_MANAGER"})
public class ChannelIntegrationTest {

    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper objectMapper;

    UUID channelId;

    @BeforeEach
    void setUp() throws Exception {
        PublicChannelCreateRequest request = new PublicChannelCreateRequest("test-channel",
            "설명입니다.");

        MvcResult result = mockMvc.perform(
                post("/api/channels/public").with(csrf()).contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsBytes(request))).andExpect(status().isCreated())
            .andReturn();

        channelId = UUID.fromString(
            objectMapper.readTree(result.getResponse().getContentAsString()).get("id").asText());
    }

    @Test
    @DisplayName("공개 채널 생성 성공")
    void shouldCreatePublicChannelSuccessfully() throws Exception {
        PublicChannelCreateRequest request = new PublicChannelCreateRequest("new-channel", "새 설명");

        mockMvc.perform(
                post("/api/channels/public").with(csrf()).contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsBytes(request))).andExpect(status().isCreated())
            .andExpect(jsonPath("$.name", is("new-channel")))
            .andExpect(jsonPath("$.description", is("새 설명")))
            .andExpect(jsonPath("$.type", is("PUBLIC"))).andExpect(jsonPath("$.id").exists());
    }

    @Test
    @DisplayName("공개 채널 생성 실패 - 유효하지 않은 요청")
    void shouldFailToCreatePublicChannelWithInvalidRequest() throws Exception {
        PublicChannelCreateRequest request = new PublicChannelCreateRequest("a", "desc");

        mockMvc.perform(
                post("/api/channels/public").with(csrf()).contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsBytes(request)))
            .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("공개 채널 수정 성공")
    void shouldUpdatePublicChannelSuccessfully() throws Exception {
        PublicChannelUpdateRequest updateRequest = new PublicChannelUpdateRequest("updated",
            "업데이트 설명");
        String json = objectMapper.writeValueAsString(updateRequest);

        mockMvc.perform(patch("/api/channels/{id}", channelId).with(csrf())
                .contentType(MediaType.APPLICATION_JSON).content(json)).andExpect(status().isOk())
            .andExpect(jsonPath("$.id", is(channelId.toString())))
            .andExpect(jsonPath("$.name", is("updated")))
            .andExpect(jsonPath("$.description", is("업데이트 설명")));
    }

    @Test
    @DisplayName("공개 채널 수정 실패 - 존재하지 않는 채널")
    void shouldFailToUpdatePublicChannelWhenNotFound() throws Exception {
        UUID fakeId = UUID.randomUUID();
        PublicChannelUpdateRequest updateRequest = new PublicChannelUpdateRequest("updated",
            "업데이트 설명");
        String json = objectMapper.writeValueAsString(updateRequest);

        mockMvc.perform(
            patch("/api/channels/{id}", fakeId).with(csrf()).contentType(MediaType.APPLICATION_JSON)
                .content(json)).andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("공개 채널 삭제 성공")
    void shouldDeletePublicChannelSuccessfully() throws Exception {
        mockMvc.perform(delete("/api/channels/{id}", channelId).with(csrf()))
            .andExpect(status().isOk()).andExpect(jsonPath("$.id", is(channelId.toString())));
    }

    @Test
    @DisplayName("공개 채널 삭제 실패 - 존재하지 않는 채널")
    void shouldFailToDeletePublicChannelWhenNotFound() throws Exception {
        UUID fakeId = UUID.randomUUID();

        mockMvc.perform(delete("/api/channels/{id}", fakeId).with(csrf()))
            .andExpect(status().isNotFound());
    }
}
