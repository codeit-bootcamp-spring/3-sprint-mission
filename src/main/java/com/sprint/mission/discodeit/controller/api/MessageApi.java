package com.sprint.mission.discodeit.controller.api;

import com.sprint.mission.discodeit.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageUpdateRequest;
import com.sprint.mission.discodeit.dto.response.MessageResponse;
import com.sprint.mission.discodeit.dto.response.PageResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "Message", description = "메시지 API")
public interface MessageApi {

    @Operation(summary = "메시지 생성", description = "새 메시지를 생성합니다. 첨부파일이 있으면 함께 업로드됩니다.")
    @ApiResponse(responseCode = "201", description = "메시지 생성 성공")
    ResponseEntity<MessageResponse> create(
        @Parameter(description = "메시지 생성 요청 DTO", required = true)
        @RequestPart("messageCreateRequest") MessageCreateRequest messageCreateRequest,

        @Parameter(description = "첨부파일 (선택)", required = false)
        @RequestPart(value = "attachments", required = false) List<MultipartFile> attachments
    );

    @Operation(summary = "메시지 수정", description = "메시지 내용을 수정합니다.")
    @ApiResponse(responseCode = "200", description = "메시지 수정 성공")
    ResponseEntity<MessageResponse> update(
        @Parameter(description = "메시지 ID") UUID messageId,
        @RequestBody(description = "메시지 수정 요청 DTO", required = true) MessageUpdateRequest request
    );

    @Operation(summary = "메시지 삭제", description = "메시지를 삭제합니다.")
    @ApiResponse(responseCode = "204", description = "삭제 성공")
    ResponseEntity<Void> delete(
        @Parameter(description = "메시지 ID") UUID messageId
    );

    @Operation(summary = "채널 메시지 목록 조회", description = "특정 채널의 메시지를 페이징으로 조회합니다.")
    @ApiResponse(responseCode = "200", description = "조회 성공")
    ResponseEntity<PageResponse<MessageResponse>> findAllByChannelId(
        @Parameter(description = "채널 ID") UUID channelId,
        @Parameter(description = "커서") Instant cursor,
        Pageable pageable
    );
}
