package com.sprint.mission.discodeit.controller.api;

import com.sprint.mission.discodeit.dto.request.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.request.ReadStatusUpdateRequest;
import com.sprint.mission.discodeit.dto.response.ReadStatusResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import java.util.UUID;
import org.springframework.http.ResponseEntity;

@Tag(name = "ReadStatus", description = "읽음 상태 API")
public interface ReadStatusApi {

    @Operation(summary = "읽음 상태 생성", description = "사용자의 채널 읽음 상태를 생성합니다.")
    @ApiResponse(responseCode = "201", description = "생성 성공")
    ResponseEntity<ReadStatusResponse> createReadStatus(
        @RequestBody(description = "읽음 상태 생성 요청 DTO", required = true)
        ReadStatusCreateRequest request
    );

    @Operation(summary = "읽음 상태 수정", description = "읽음 상태 정보를 수정합니다.")
    @ApiResponse(responseCode = "200", description = "수정 성공")
    ResponseEntity<ReadStatusResponse> updateReadStatus(
        @Parameter(description = "읽음 상태 ID") UUID readStatusId,
        @RequestBody(description = "읽음 상태 수정 요청 DTO", required = true)
        ReadStatusUpdateRequest request
    );

    @Operation(summary = "사용자별 읽음 상태 조회", description = "특정 사용자에 대한 모든 읽음 상태를 조회합니다.")
    @ApiResponse(responseCode = "200", description = "조회 성공")
    ResponseEntity<List<ReadStatusResponse>> findAllByUserId(
        @Parameter(description = "사용자 ID") UUID userId
    );
}
