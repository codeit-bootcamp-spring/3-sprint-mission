package com.sprint.mission.discodeit.controller;


import com.sprint.mission.discodeit.dto.binarycontent.CreateBinaryContentRequest;
import com.sprint.mission.discodeit.dto.message.CreateMessageRequest;
import com.sprint.mission.discodeit.dto.message.MessageDTO;
import com.sprint.mission.discodeit.dto.message.UpdateMessageRequest;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.service.MessageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "메시지", description = "메시지 API")
@RestController
@RequestMapping("/api/messages")
@RequiredArgsConstructor
public class MessageController {

  private final MessageService messageService;

  @Operation(
      summary = "Message 생성"
  )
  @ApiResponses(
      value = {
          @ApiResponse(responseCode = "201", description = "Message가 성공적으로 생성됨", content = @Content(schema = @Schema(implementation = Message.class))),
          @ApiResponse(responseCode = "404", description = "Channel 또는 User를 찾을 수 없음", content = @Content(schema = @Schema(hidden = true)))
      }
  )
  @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<Message> createMessage(
      @RequestPart("messageCreateRequest") CreateMessageRequest createMessageRequest,
      @Parameter(description = "메시지 첨부 파일들", required = false)
      @RequestPart(value = "attachments", required = false) List<MultipartFile> attachments
  ) {
    List<CreateBinaryContentRequest> attachmentRequests = Optional.ofNullable(attachments)
        .map(files -> files.stream()
            .map(file -> {
              try {
                return new CreateBinaryContentRequest(
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

    Message message = messageService.create(createMessageRequest, attachmentRequests);
    return ResponseEntity
        .status(HttpStatus.CREATED)
        .body(message);
  }

  @Operation(
      summary = "메시지 조회"
  )
  @ApiResponses(
      value = {
          @ApiResponse(responseCode = "200", description = "메시지 조회 성공", content = @Content(schema = @Schema(implementation = MessageDTO.class))),
          @ApiResponse(responseCode = "404", description = "존재하지 않는 메시지 ID", content = @Content(schema = @Schema(hidden = true)))
      }
  )
  @GetMapping(value = "/{messageId}")
  public ResponseEntity<MessageDTO> find(
      @Parameter(description = "조회할 메시지 ID", required = true)
      @PathVariable UUID messageId
  ) {
    Message message = messageService.find(messageId);
    return ResponseEntity.ok(MessageDTO.fromDomain(message));
  }

  @Operation(
      summary = "채널의 메시지 목록 조회"
  )
  @ApiResponses(
      value = {
          @ApiResponse(responseCode = "200", description = "메시지 목록 조회 성공", content = @Content(array = @ArraySchema(schema = @Schema(implementation = Message.class)))),
          @ApiResponse(responseCode = "404", description = "존재하지 않는 채널 ID", content = @Content(schema = @Schema(hidden = true)))
      }
  )
  @GetMapping
  public ResponseEntity<List<Message>> findAllMessageByChannelId(
      @Parameter(description = "조회할 채널 ID", required = true)
      @RequestParam("channelId") UUID channelId
  ) {
    List<Message> messageList = messageService.findAllByChannelId(channelId);
    return ResponseEntity
        .status(HttpStatus.OK)
        .body(messageList);
  }

  @Operation(
      summary = "메시지 내용 수정"
  )
  @ApiResponses(
      value = {
          @ApiResponse(responseCode = "200", description = "메시지 수정 성공", content = @Content(schema = @Schema(implementation = Message.class))),
          @ApiResponse(responseCode = "404", description = "Message를 찾을 수 없습니다", content = @Content(schema = @Schema(hidden = true)))
      }
  )
  @PatchMapping("/{messageId}")
  public ResponseEntity<Message> updateMessage(
      @Parameter(description = "수정할 메시지 ID", required = true)
      @PathVariable UUID messageId,
      @RequestBody UpdateMessageRequest updateMessageRequest
  ) {
    Message message = messageService.update(messageId, updateMessageRequest);
    return ResponseEntity
        .status(HttpStatus.OK)
        .body(message);
  }

  @Operation(
      summary = "메시지 삭제"
  )
  @ApiResponses(
      value = {
          @ApiResponse(responseCode = "204", description = "메시지가 삭제 성공"),
          @ApiResponse(responseCode = "404", description = "메시지 ID를 찾을 수 없습니다.", content = @Content(schema = @Schema(hidden = true)))
      }
  )
  @DeleteMapping("/{messageId}")
  public ResponseEntity<String> deleteMessage(
      @Parameter(description = "삭제할 메시지 ID", required = true)
      @PathVariable UUID messageId
  ) {
    messageService.delete(messageId);
    return ResponseEntity
        .status(HttpStatus.OK)
        .body("메시지 ID : " + messageId + " 삭제 성공");
  }


}
