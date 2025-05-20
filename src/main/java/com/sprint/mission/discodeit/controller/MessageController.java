package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageUpdateRequest;
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
import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "Message", description = "Message API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/messages")
public class MessageController {

  private final MessageService messageService;

  @Operation(summary = "Message 생성")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "201", description = "Message 생성 성공"),
      @ApiResponse(responseCode = "404", description = "유저 또는 채널을 찾을 수 없음",
          content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
  })
  @PostMapping(
      consumes = MediaType.MULTIPART_FORM_DATA_VALUE
  )
  public ResponseEntity<Message> create(
      @Parameter(
          description = "Message 생성 정보",
          required = true,
          content = @Content(
              mediaType = MediaType.APPLICATION_JSON_VALUE,
              schema = @Schema(implementation = MessageCreateRequest.class)
          )
      )
      @RequestPart("messageCreateRequest") MessageCreateRequest messageCreateRequest,

      @Parameter(
          description = "Message 첨부 파일들",
          content = @Content(
              mediaType = MediaType.APPLICATION_OCTET_STREAM_VALUE,
              schema = @Schema(type = "array", format = "binary", implementation = MultipartFile.class)
          )
      )
      @RequestPart(value = "attachments", required = false) List<MultipartFile> attachments) {

    List<BinaryContentCreateRequest> binaryContentCreateRequests =
        Optional.ofNullable(attachments)
            .orElse(List.of()).stream()
            .map(this::resolveAttachmentRequest)
            .flatMap(Optional::stream)
            .toList();

    Message message = messageService.create(messageCreateRequest,
        binaryContentCreateRequests);
    return ResponseEntity.created(URI.create("/api/messages/" + message.getId())).body(message);
  }

  @Operation(summary = "Channel의 Message 목록 조회")
  @ApiResponse(responseCode = "200", description = "Message를 성공적으로 조회함",
      content = @Content(
          array = @ArraySchema(schema = @Schema(implementation = Message.class))
      )
  )
  @GetMapping
  public ResponseEntity<List<Message>> findAllByChannelId(@RequestParam UUID channelId) {
    return ResponseEntity.ok(messageService.findAllByChannelId(channelId));
  }

  @Operation(summary = "Message 내용 수정")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Message가 성공적으로 수정됨"),
      @ApiResponse(responseCode = "404", description = "Message를 찾을 수 없음", content = @Content())
  })
  @PatchMapping("/{messageId}")
  public ResponseEntity<Message> update(@PathVariable UUID messageId,
      @RequestBody MessageUpdateRequest request) {
    return messageService.updateContent(messageId, request)
        .map(ResponseEntity::ok)
        .orElseGet(() -> ResponseEntity.notFound().build());
  }

  @Operation(summary = "Message 삭제")
  @ApiResponses(
      value = {
          @ApiResponse(responseCode = "204", description = "Message가 성공적으로 삭제됨"),
          @ApiResponse(responseCode = "404", description = "Message를 찾을 수 없음")
      }
  )
  @DeleteMapping("/{messageId}")
  public ResponseEntity<Object> delete(@PathVariable UUID messageId) {
    return messageService.delete(messageId)
        .map(m -> ResponseEntity.noContent().build())
        .orElseGet(() -> ResponseEntity.notFound().build());
  }

  private Optional<BinaryContentCreateRequest> resolveAttachmentRequest(MultipartFile attachments) {

    if (attachments.isEmpty()) {
      return Optional.empty();
    } else {
      try {
        BinaryContentCreateRequest binaryContentCreateRequest = new BinaryContentCreateRequest(
            attachments.getOriginalFilename(),
            attachments.getContentType(),
            attachments.getBytes()
        );
        return Optional.of(binaryContentCreateRequest);
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    }
  }
}
