package com.sprint.mission.discodeit.Contoller;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.controller.ChannelController;
import com.sprint.mission.discodeit.dto.data.ChannelDto;
import com.sprint.mission.discodeit.dto.request.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.service.ChannelService;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

@WebMvcTest(ChannelController.class)
public class ChannelControllerTest {

  @Autowired
  MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @MockitoBean
  ChannelService channelService;

  @Nested
  @DisplayName("비공개 채널 생성 테스트")
  class CreatePrivateChannel {

    @Test
    @DisplayName("비공개 채널 생성 성공")
    void whenCreatePrivateChannel_thenReturnCreatedChannel() throws Exception {
      // given
      List<UUID> memberIds = List.of(UUID.randomUUID());
      PrivateChannelCreateRequest request = new PrivateChannelCreateRequest(memberIds);
      ChannelDto createdDto = new ChannelDto(
          UUID.randomUUID(),
          ChannelType.PRIVATE,
          null,
          null,
          List.of(), // 참여자 정보 생략
          Instant.now()
      );
      given(channelService.create(request)).willReturn(createdDto);

      // When
      ResultActions result = mockMvc.perform(post("/api/channels/private")
          .contentType(MediaType.APPLICATION_JSON)
          .content(objectMapper.writeValueAsBytes(request)));

      // Then
      result.andExpect(status().isCreated())
          .andExpect(jsonPath("$.type").value("PRIVATE"));
    }

    @Test
    @DisplayName("비공개 채널 생성 실패 시 500 반환")
    void whenCreatePrivateChannelFails_thenReturnError() throws Exception {
      // given
      PrivateChannelCreateRequest request = new PrivateChannelCreateRequest(List.of());
      given(channelService.create(request)).willThrow(new RuntimeException("creation error"));

      // When
      ResultActions result = mockMvc.perform(post("/api/channels/private")
          .contentType(MediaType.APPLICATION_JSON)
          .content(objectMapper.writeValueAsBytes(request)));

      // Then
      result.andExpect(status().isInternalServerError());
    }
  }

  @Nested
  @DisplayName("공개 채널 수정 테스트")
  class UpdatePublicChannel {

    @Test
    @DisplayName("공개 채널 수정 성공")
    void whenUpdatePublicChannel_thenReturnUpdatedChannel() throws Exception {
      // given
      UUID channelId = UUID.fromString("00000000-0000-0000-0000-000000000030");
      PublicChannelUpdateRequest request = new PublicChannelUpdateRequest("updated name",
          "updated desc");
      ChannelDto updatedDto = new ChannelDto(
          channelId,
          ChannelType.PUBLIC,
          "updated name",
          "updated desc",
          List.of(),
          Instant.now()
      );
      given(channelService.update(channelId, request)).willReturn(updatedDto);

      // When
      ResultActions result = mockMvc.perform(patch("/api/channels/{channelId}", channelId)
          .contentType(MediaType.APPLICATION_JSON)
          .content(objectMapper.writeValueAsBytes(request)));

      // Then
      result
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.name").value("updated name"))
          .andExpect(jsonPath("$.description").value("updated desc"));
    }

    @Test
    @DisplayName("공개 채널 수정 실패 시 500 반환")
    void whenUpdatePublicChannelFails_thenReturnError() throws Exception {
      // given
      UUID channelId = UUID.randomUUID();
      PublicChannelUpdateRequest request = new PublicChannelUpdateRequest("fail", "desc");
      given(channelService.update(channelId, request))
          .willThrow(new RuntimeException("update error"));

      // When
      ResultActions result = mockMvc.perform(patch("/api/channels/{channelId}", channelId)
          .contentType(MediaType.APPLICATION_JSON)
          .content(objectMapper.writeValueAsBytes(request)));

      // Then
      result
          .andExpect(status().isInternalServerError());
    }
  }

}
