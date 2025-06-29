package com.sprint.mission.discodeit.controller.api;

import com.sprint.mission.discodeit.dto.request.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.dto.response.ChannelResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import java.util.UUID;
import org.springframework.http.ResponseEntity;

@Tag(name = "Channel", description = "채널 API")
public interface ChannelApi {

    @Operation(summary = "퍼블릭 채널 생성", description = "새로운 퍼블릭 채널을 생성합니다.")
    @ApiResponse(responseCode = "201", description = "채널 생성 성공")
    ResponseEntity<ChannelResponse> create(
        @RequestBody(description = "퍼블릭 채널 생성 요청 DTO", required = true)
        PublicChannelCreateRequest request
    );

    @Operation(summary = "프라이빗 채널 생성", description = "새로운 프라이빗 채널을 생성합니다.")
    @ApiResponse(responseCode = "201", description = "채널 생성 성공")
    ResponseEntity<ChannelResponse> create(
        @RequestBody(description = "프라이빗 채널 생성 요청 DTO", required = true)
        PrivateChannelCreateRequest request
    );

    @Operation(summary = "채널 수정", description = "퍼블릭 채널 정보를 수정합니다.")
    @ApiResponse(responseCode = "200", description = "채널 수정 성공")
    ResponseEntity<ChannelResponse> update(
        @Parameter(description = "채널 ID") UUID channelId,
        @RequestBody(description = "채널 수정 요청 DTO", required = true)
        PublicChannelUpdateRequest request
    );

    @Operation(summary = "채널 삭제", description = "채널을 삭제합니다.")
    @ApiResponse(responseCode = "200", description = "채널 삭제 성공")
    ResponseEntity<ChannelResponse> delete(
        @Parameter(description = "채널 ID") UUID channelId
    );

    @Operation(summary = "사용자 채널 목록 조회", description = "특정 사용자에 대한 채널 목록을 조회합니다.")
    @ApiResponse(responseCode = "200", description = "조회 성공")
    ResponseEntity<List<ChannelResponse>> findAllByUserId(
        @Parameter(description = "사용자 ID") UUID userId
    );
}
