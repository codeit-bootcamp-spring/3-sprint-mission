package com.sprint.mission.discodeit.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.dto.data.ChannelDto;
import com.sprint.mission.discodeit.dto.request.channel.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.channel.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.channel.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.exception.GlobalExceptionHandler;
import com.sprint.mission.discodeit.service.ChannelService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * ChannelController에 대한 슬라이스 테스트 클래스입니다.
 * <p>
 * 컨트롤러 레이어만을 독립적으로 테스트하며, 서비스 레이어는 Mock으로 대체하여 HTTP 요청/응답 처리 로직을 검증합니다.
 * <ul>
 *   <li>공개/비공개 채널 생성, 수정, 삭제, 사용자별 채널 조회 등 다양한 시나리오를 검증합니다.</li>
 * </ul>
 */
@WebMvcTest(ChannelController.class)
@Import(GlobalExceptionHandler.class)
class ChannelControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ChannelService channelService;

    private UUID testChannelId;
    private UUID testUserId;
    private ChannelDto testPublicChannelDto;
    private ChannelDto testPrivateChannelDto;

    /**
     * 각 테스트 메소드 실행 전에 공통으로 사용할 테스트 데이터를 초기화합니다.
     */
    @BeforeEach
    void setUp() {
        testChannelId = UUID.randomUUID();
        testUserId = UUID.randomUUID();
        
        testPublicChannelDto = new ChannelDto(
            testChannelId,
            ChannelType.PUBLIC,
            "테스트 공개 채널",
            "테스트 공개 채널 설명",
            null,
            null
        );
        
        testPrivateChannelDto = new ChannelDto(
            UUID.randomUUID(),
            ChannelType.PRIVATE,
            "테스트 비공개 채널",
            "테스트 비공개 채널 설명",
            null,
            null
        );
    }

    @Nested
    @DisplayName("공개 채널 생성 테스트")
    class CreatePublicChannelTest {

        /**
         * [성공] 유효한 공개 채널 정보로 채널을 생성할 수 있는지 검증합니다.
         */
        @Test
        @DisplayName("[성공] 유효한 공개 채널 정보로 채널 생성")
        void shouldCreatePublicChannelSuccessfully_whenValidChannelDataProvided() throws Exception {
            // given: 유효한 공개 채널 생성 요청 데이터 준비
            PublicChannelCreateRequest createRequest = new PublicChannelCreateRequest(
                "테스트 공개 채널",
                "테스트 공개 채널 설명"
            );
            
            given(channelService.create(any(PublicChannelCreateRequest.class)))
                .willReturn(testPublicChannelDto);

            // when & then: POST 요청을 보내고 응답 검증
            mockMvc.perform(post("/api/channels/public")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(createRequest)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(testChannelId.toString()))
                .andExpect(jsonPath("$.name").value("테스트 공개 채널"))
                .andExpect(jsonPath("$.description").value("테스트 공개 채널 설명"))
                .andExpect(jsonPath("$.type").value("PUBLIC"));
        }

        /**
         * [실패] 잘못된 공개 채널 정보로 채널 생성 시 400 Bad Request가 반환되는지 검증합니다.
         */
        @Test
        @DisplayName("[실패] 잘못된 공개 채널 정보로 채널 생성 시 400 반환")
        void shouldReturnBadRequest_whenInvalidPublicChannelDataProvided() throws Exception {
            // given: 유효하지 않은 공개 채널 생성 요청 데이터 준비 (채널명 누락)
            PublicChannelCreateRequest invalidRequest = new PublicChannelCreateRequest(
                "",  // 빈 채널명
                "테스트 공개 채널 설명"
            );

            // when & then: 유효하지 않은 데이터로 POST 요청을 보내고 400 응답 검증
            mockMvc.perform(post("/api/channels/public")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(invalidRequest)))
                .andDo(print())
                .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("비공개 채널 생성 테스트")
    class CreatePrivateChannelTest {

        @Test
        @DisplayName("유효한 비공개 채널 정보로 채널을 생성하면 201 Created와 함께 생성된 채널 정보를 반환한다")
        void shouldCreatePrivateChannelSuccessfully_whenValidChannelDataProvided() throws Exception {
            // given: 유효한 비공개 채널 생성 요청 데이터 준비
            PrivateChannelCreateRequest createRequest = new PrivateChannelCreateRequest(
                Arrays.asList(testUserId, UUID.randomUUID())
            );
            
            given(channelService.create(any(PrivateChannelCreateRequest.class)))
                .willReturn(testPrivateChannelDto);

            // when & then: POST 요청을 보내고 응답 검증
            mockMvc.perform(post("/api/channels/private")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(createRequest)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(testPrivateChannelDto.id().toString()))
                .andExpect(jsonPath("$.name").value("테스트 비공개 채널"))
                .andExpect(jsonPath("$.description").value("테스트 비공개 채널 설명"))
                .andExpect(jsonPath("$.type").value("PRIVATE"));
        }

        @Test
        @DisplayName("유효하지 않은 비공개 채널 정보로 채널 생성을 요청하면 400 Bad Request를 반환한다")
        void shouldReturnBadRequest_whenInvalidPrivateChannelDataProvided() throws Exception {
            // given: 유효하지 않은 비공개 채널 생성 요청 데이터 준비 (참가자 목록 누락)
            PrivateChannelCreateRequest invalidRequest = new PrivateChannelCreateRequest(
                null  // null 참가자 목록
            );

            // when & then: 유효하지 않은 데이터로 POST 요청을 보내고 400 응답 검증
            mockMvc.perform(post("/api/channels/private")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(invalidRequest)))
                .andDo(print())
                .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("채널 수정 테스트")
    class UpdateChannelTest {

        @Test
        @DisplayName("유효한 채널 정보로 채널을 수정하면 200 OK와 함께 수정된 채널 정보를 반환한다")
        void shouldUpdateChannelSuccessfully_whenValidChannelDataProvided() throws Exception {
            // given: 유효한 채널 수정 요청 데이터 준비
            PublicChannelUpdateRequest updateRequest = new PublicChannelUpdateRequest(
                "수정된 채널명",
                "수정된 채널 설명"
            );
            
            ChannelDto updatedChannelDto = new ChannelDto(
                testChannelId,
                ChannelType.PUBLIC,
                "수정된 채널명",
                "수정된 채널 설명",
                null,
                null
            );
            
            given(channelService.update(eq(testChannelId), any(PublicChannelUpdateRequest.class)))
                .willReturn(updatedChannelDto);

            // when & then: PATCH 요청을 보내고 응답 검증
            mockMvc.perform(patch("/api/channels/" + testChannelId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(updateRequest)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(testChannelId.toString()))
                .andExpect(jsonPath("$.name").value("수정된 채널명"))
                .andExpect(jsonPath("$.description").value("수정된 채널 설명"));
        }

        @Test
        @DisplayName("존재하지 않는 채널 ID로 수정을 요청하면 CHANNEL_NOT_FOUND 상태코드로 응답한다")
        void shouldThrowException_whenChannelNotFound() throws Exception {
            // given: 존재하지 않는 채널 ID와 유효한 수정 요청 데이터 준비
            UUID nonExistentChannelId = UUID.randomUUID();
            PublicChannelUpdateRequest updateRequest = new PublicChannelUpdateRequest(
                "수정된 채널명",
                "수정된 채널 설명"
            );
            
            given(channelService.update(eq(nonExistentChannelId), any(PublicChannelUpdateRequest.class)))
                .willThrow(new com.sprint.mission.discodeit.exception.channel.ChannelNotFoundException("채널을 찾을 수 없습니다."));

            // when & then: 존재하지 않는 채널 ID로 PATCH 요청을 보내고 예외 응답 검증
            mockMvc.perform(patch("/api/channels/" + nonExistentChannelId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(updateRequest)))
                .andDo(print())
                .andExpect(status().is(com.sprint.mission.discodeit.exception.ErrorCode.CHANNEL_NOT_FOUND.getStatus().value()))
                .andExpect(jsonPath("$.code").value(com.sprint.mission.discodeit.exception.ErrorCode.CHANNEL_NOT_FOUND.getCode()))
                .andExpect(jsonPath("$.message").value(com.sprint.mission.discodeit.exception.ErrorCode.CHANNEL_NOT_FOUND.getMsg()));
        }
    }

    @Nested
    @DisplayName("채널 삭제 테스트")
    class DeleteChannelTest {

        @Test
        @DisplayName("존재하는 채널 ID로 삭제를 요청하면 204 No Content를 반환한다")
        void shouldDeleteChannelSuccessfully_whenValidChannelIdProvided() throws Exception {
            // given: 채널 삭제 서비스가 정상적으로 동작하도록 설정
            willDoNothing().given(channelService).delete(testChannelId);

            // when & then: DELETE 요청을 보내고 204 응답 검증
            mockMvc.perform(delete("/api/channels/" + testChannelId))
                .andDo(print())
                .andExpect(status().isNoContent());
        }

        @Test
        @DisplayName("존재하지 않는 채널 ID로 삭제를 요청하면 CHANNEL_NOT_FOUND 상태코드로 응답한다")
        void shouldThrowException_whenChannelNotFoundForDeletion() throws Exception {
            // given: 존재하지 않는 채널 ID 준비
            UUID nonExistentChannelId = UUID.randomUUID();
            willThrow(new com.sprint.mission.discodeit.exception.channel.ChannelNotFoundException("채널을 찾을 수 없습니다.")).given(channelService).delete(nonExistentChannelId);

            // when & then: 존재하지 않는 채널 ID로 DELETE 요청을 보내고 예외 응답 검증
            mockMvc.perform(delete("/api/channels/" + nonExistentChannelId))
                .andDo(print())
                .andExpect(status().is(com.sprint.mission.discodeit.exception.ErrorCode.CHANNEL_NOT_FOUND.getStatus().value()))
                .andExpect(jsonPath("$.code").value(com.sprint.mission.discodeit.exception.ErrorCode.CHANNEL_NOT_FOUND.getCode()))
                .andExpect(jsonPath("$.message").value(com.sprint.mission.discodeit.exception.ErrorCode.CHANNEL_NOT_FOUND.getMsg()));
        }
    }

    @Nested
    @DisplayName("사용자별 채널 목록 조회 테스트")
    class FindChannelsByUserIdTest {

        @Test
        @DisplayName("유효한 사용자 ID로 채널 목록 조회 요청 시 200 OK와 함께 채널 목록을 반환한다")
        void shouldReturnChannelsByUserId_whenValidUserIdProvided() throws Exception {
            // given: 사용자별 채널 목록 데이터 준비
            given(channelService.findAllByUserId(testUserId))
                .willReturn(Arrays.asList(testPublicChannelDto, testPrivateChannelDto));

            // when & then: GET 요청을 보내고 응답 검증
            mockMvc.perform(get("/api/channels")
                    .param("userId", testUserId.toString()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].name").value("테스트 공개 채널"))
                .andExpect(jsonPath("$[1].name").value("테스트 비공개 채널"));
        }

        @Test
        @DisplayName("채널이 없는 사용자 ID로 조회 요청 시 200 OK와 함께 빈 목록을 반환한다")
        void shouldReturnEmptyList_whenNoChannelsExistForUser() throws Exception {
            // given: 빈 채널 목록 반환하도록 설정
            given(channelService.findAllByUserId(testUserId))
                .willReturn(Arrays.asList());

            // when & then: GET 요청을 보내고 빈 목록 응답 검증
            mockMvc.perform(get("/api/channels")
                    .param("userId", testUserId.toString()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));
        }
    }
} 