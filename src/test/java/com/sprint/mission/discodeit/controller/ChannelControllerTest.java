package com.sprint.mission.discodeit.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.config.JpaConfig;
import com.sprint.mission.discodeit.dto.channel.ChannelResponseDto;
import com.sprint.mission.discodeit.dto.channel.PrivateChannelDto;
import com.sprint.mission.discodeit.dto.channel.PublicChannelDto;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.service.basic.BasicChannelService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ChannelController.class,
        excludeAutoConfiguration = {JpaConfig.class})
@DisplayName("ChannelController 슬라이스 테스트")
class ChannelControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private BasicChannelService channelService;

    @Test
    @DisplayName("Public Channel 요청이 올바르게 처리되어야 한다.")
    void givenValidPublicChannelDto_whenCreatePublicChannel_thenReturnCreatedChannelResponse()
            throws Exception {

        // given
        UUID channelId = UUID.randomUUID();
        PublicChannelDto request = new PublicChannelDto("public", "test channel");
        ChannelResponseDto expectedResponse = new ChannelResponseDto(channelId, ChannelType.PUBLIC, "public",
                "test channel", List.of(), null);

        given(channelService.createPublicChannel(any(PublicChannelDto.class)))
                .willReturn(expectedResponse);

        // when
        ResultActions result = mockMvc.perform(post("/api/channels/public")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(request)));

        // then
        result.andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(channelId.toString()))
                .andExpect(jsonPath("$.name").value("public"))
                .andExpect(jsonPath("$.description").value("test channel"));
        verify(channelService).createPublicChannel(argThat(req ->
                req.name().equals("public") &&
                        req.description().equals("test channel")
        ));
    }

    @Test
    @DisplayName("Private Channel 요청이 올바르게 처리되어야 한다.")
    void givenValidPrivateChannelDto_whenCreatePrivateChannel_thenReturnCreatedChannelResponse()
            throws Exception {

        // given
        UUID channelId = UUID.randomUUID();

        PrivateChannelDto request = new PrivateChannelDto(List.of(UUID.randomUUID(), UUID.randomUUID()));

        ChannelResponseDto expectedResponse = new ChannelResponseDto(channelId, ChannelType.PRIVATE, null,
                null, List.of(), null);

        given(channelService.createPrivateChannel(any(PrivateChannelDto.class)))
                .willReturn(expectedResponse);

        // when
        ResultActions result = mockMvc.perform(post("/api/channels/private")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(request)));

        // then
        result.andExpect(status().isCreated())
                .andExpect(jsonPath("$.type").value(ChannelType.PRIVATE.toString()));
        verify(channelService).createPrivateChannel(request);
    }

    @Test
    @DisplayName("Public Channel 생성 시 채널 이름이 누락되면 400 Bad Request가 발생해야 한다.")
    void givenMissingRequiredField_whenCreatePublicChannel_thenReturnBadRequestWithValidationError()
            throws Exception {

        // given
        PublicChannelDto request = new PublicChannelDto(null, "test channel");

        // when
        ResultActions result = mockMvc.perform(post("/api/channels/public")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(request)));


        // then
        result.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_FAILED"))
                .andExpect(jsonPath("$.message").value("유효성 검사 실패"));
        verify(channelService, never()).createPublicChannel(request);
    }
}