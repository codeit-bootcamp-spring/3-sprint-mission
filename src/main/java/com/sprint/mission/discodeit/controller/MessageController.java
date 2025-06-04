package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.CodeMessageResponseDto;
import com.sprint.mission.discodeit.dto.ResponseCode;
import com.sprint.mission.discodeit.dto.ResponseMessage;
import com.sprint.mission.discodeit.dto.data.MessageDto;
import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageUpdateRequest;
import com.sprint.mission.discodeit.dto.response.PageResponse;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.service.MessageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.Instant;
import java.util.*;

@Tag(
        name = "Message",
        description = "Message API"
)
@RestController
@RequestMapping("/api/messages")
@RequiredArgsConstructor
public class MessageController {

  private final MessageService messageService;

  @Operation(
          summary = "Message 생성",
          operationId = "create_2",
          tags = {"Message"}
  )
  @ApiResponses(value = {
          @ApiResponse(
                  responseCode = "201",
                  description = "Message가 성공적으로 생성됨",
                  content = @Content(
                          mediaType = "*/*",
                          schema = @Schema(implementation = Message.class)
                  )
          ),
          @ApiResponse(
                  responseCode = "404",
                  description = "Channel 또는 User를 찾을 수 없음",
                  content = @Content(
                          mediaType = "*/*",
                          examples = @ExampleObject(value = "Channel | Author with id {channelId | authorId} not found")
                  )
          )
  })
  @Parameter(
          name = "messageCreateRequest",
          description = "새로운 메시지 생성 정보를 포함한 객체",
          required = true,
          schema = @Schema(implementation = MessageCreateRequest.class)
  )
  @Parameter(
          name = "attachments",
          description = "Message 첨부 파일들",
          content = @Content(
                  mediaType = MediaType.MULTIPART_FORM_DATA_VALUE,
                  array = @ArraySchema(
                          schema = @Schema(type = "string", format = "binary")
                  )
          )
  )
  @PostMapping(value = "", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<MessageDto> create(
      @RequestPart("messageCreateRequest") MessageCreateRequest messageCreateRequest,
      @RequestPart(value = "attachments", required = false) List<MultipartFile> attachments
  ) {
      List<BinaryContentCreateRequest> attachmentRequests = Optional.ofNullable(attachments)
              .map(files -> files.stream()
                      .map(file -> {
                          try {
                              return new BinaryContentCreateRequest(
                                      file.getOriginalFilename(),
                                      file.getContentType(),
                                      file.getBytes()
                              );
                          } catch (IOException e) {
                              throw new RuntimeException(e);
                          }
                      })
                      .toList())
              .orElse(new ArrayList<>());
      MessageDto createdMessage = messageService.create(messageCreateRequest, attachmentRequests);
      return ResponseEntity
              .status(HttpStatus.CREATED)
              .body(createdMessage);
  }

  @Operation(
          summary = "Message 내용 수정",
          operationId = "update_2",
          tags = {"Message"}
  )
  @ApiResponses(value = {
          @ApiResponse(
                  responseCode = "200",
                  description = "Message가 성공적으로 수정됨",
                  content = @Content(
                          mediaType = "*/*",
                          schema = @Schema(implementation = Message.class)
                  )
          ),
          @ApiResponse(
                  responseCode = "404",
                  description = "Message를 찾을 수 없음",
                  content = @Content(
                          mediaType = "*/*",
                          examples = @ExampleObject(value = "Message with id {messageId} not found")
                  )
          )
  })
  @Parameter(
          name = "messageId",
          in = ParameterIn.PATH,
          description = "수정할 Message ID",
          required = true,
          schema = @Schema(type = "string", format = "uuid")
  )
  @Parameter(
          name = "messageUpdateRequestDto",
          description = "수정할 메시지 정보",
          required = true,
          schema = @Schema(implementation = Message.class)
  )
  @PatchMapping("/{messageId}")
  public ResponseEntity<MessageDto> update(@PathVariable("messageId") UUID messageId,
      @RequestBody MessageUpdateRequest request) {

      MessageDto updatedMessage = messageService.update(messageId, request);
      return ResponseEntity
              .status(HttpStatus.OK)
              .body(updatedMessage);
  }

  @Operation(
          summary = "Message 삭제",
          operationId = "delete_1",
          tags = {"Message"}
  )
  @ApiResponses(value = {
          @ApiResponse(
                  responseCode = "204",
                  description = "Message가 성공적으로 삭제됨",
                  content = @Content(
                          mediaType = "*/*",
                          examples = @ExampleObject(value = "Success")
                  )
          ),
          @ApiResponse(
                  responseCode = "404",
                  description = "Message를 찾을 수 없음",
                  content = @Content(
                          mediaType = "*/*",
                          examples = @ExampleObject(value = "Message with id {messageId} not found")
                  )
          )
  })
  @Parameters({
          @Parameter(
                  name = "messageId",
                  description = "삭제할 Message ID",
                  required = true,
                  in = ParameterIn.PATH,
                  schema = @Schema(type = "string", format = "uuid")
          )
  })
  @DeleteMapping("/{messageId}")
  public ResponseEntity<?> delete(@PathVariable("messageId") UUID messageId) {
      messageService.delete(messageId);
      return ResponseEntity
              .status(HttpStatus.NO_CONTENT)
              .body("Message가 성공적으로 삭제됨");
  }

  @Operation(
          summary = "Channel의 Message 목록 조회",
          operationId = "findAllByChannelId",
          tags = {"Message"}
  )
  @ApiResponse(
          responseCode = "200",
          description = "Message 목록 조회 성공",
          content = @Content(
                  mediaType = "*/*",
                  array = @ArraySchema(
                          schema = @Schema(implementation = Message.class)
                  )
          )
  )
  @Parameter(
          name = "channelId",
          description = "조회할 Channel ID",
          required = true,
          in = ParameterIn.QUERY,
          schema = @Schema(type = "string", format = "uuid")
  )
  @GetMapping("")
  public ResponseEntity<PageResponse<?>> findAllByChannelId(
          @RequestParam("channelId") UUID channelId,
          @RequestParam(name = "cursor", required = false) Instant cursor,
          @PageableDefault Pageable pageable) {

      PageResponse<?> messages = messageService.findAllByChannelId(channelId, cursor, pageable);
      return ResponseEntity
              .status(HttpStatus.OK)
              .body(messages);
  }
}
