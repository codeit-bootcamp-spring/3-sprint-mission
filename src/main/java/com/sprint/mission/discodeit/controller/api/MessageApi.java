package com.sprint.mission.discodeit.controller.api;

import com.sprint.mission.discodeit.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageUpdateRequest;
import com.sprint.mission.discodeit.dto.response.MessageResponse;
import com.sprint.mission.discodeit.dto.response.PageResponse;
import com.sprint.mission.discodeit.entity.Message;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.ErrorResponse;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "Message", description = "Message API")
public interface MessageApi {

  @Operation(summary = "Message 생성")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "201", description = "Message 생성 성공"),
      @ApiResponse(responseCode = "404", description = "유저 또는 채널을 찾을 수 없음",
          content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
  })
  ResponseEntity<MessageResponse> create(
      @Parameter(
          description = "Message 생성 정보",
          required = true,
          content = @Content(
              mediaType = MediaType.APPLICATION_JSON_VALUE,
              schema = @Schema(implementation = MessageCreateRequest.class)
          )
      )
      MessageCreateRequest messageCreateRequest,

      @Parameter(
          description = "Message 첨부 파일들",
          content = @Content(
              mediaType = MediaType.APPLICATION_OCTET_STREAM_VALUE,
              schema = @Schema(type = "array", format = "binary", implementation = MultipartFile.class)
          )
      )
      List<MultipartFile> attachments);

  @Operation(summary = "Channel의 Message 목록 조회")
  @ApiResponse(responseCode = "200", description = "Message를 성공적으로 조회함",
      content = @Content(
          array = @ArraySchema(schema = @Schema(implementation = Message.class))
      )
  )
  ResponseEntity<PageResponse<MessageResponse>> findAllByChannelId(UUID channelId, Instant cursor,
      Pageable pageable);

  @Operation(summary = "Message 내용 수정")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Message가 성공적으로 수정됨"),
      @ApiResponse(responseCode = "404", description = "Message를 찾을 수 없음", content = @Content())
  })
  ResponseEntity<MessageResponse> update(UUID messageId, MessageUpdateRequest request);

  @Operation(summary = "Message 삭제")
  @ApiResponses(
      value = {
          @ApiResponse(responseCode = "204", description = "Message가 성공적으로 삭제됨"),
          @ApiResponse(responseCode = "404", description = "Message를 찾을 수 없음")
      }
  )
  ResponseEntity<Void> delete(UUID messageId);
}
