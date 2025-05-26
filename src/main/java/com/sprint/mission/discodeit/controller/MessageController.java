package com.sprint.mission.discodeit.controller;

import static com.sprint.mission.discodeit.controller.Converter.resolveBinaryContentRequest;

import com.sprint.mission.discodeit.dto.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.MessageUpdateRequest;
import com.sprint.mission.discodeit.entitiy.Message;
import com.sprint.mission.discodeit.service.MessageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Tag(name = "Message", description = "MessageAPI")
public class MessageController {

  private final MessageService messageService;


  @Operation(summary = "Message 생성")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "404", description = "Channel 또는 User를 찾을 수 없음", content = @Content(schema = @Schema(example = "Channel | Author with id {channelId | authorId} not found"))),
      @ApiResponse(responseCode = "201", description = "Message가 성공적으로 생성됨", content = @Content(schema = @Schema(implementation = Message.class)))
  })
  @PostMapping(value = "/messages", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<Message> create_2(
      @RequestPart("messageCreateRequest") MessageCreateRequest request,
      @RequestPart(value = "attachments", required = false) List<MultipartFile> image) {
    Message message;
    if (image != null && !image.isEmpty()) {
      Optional<List<BinaryContentCreateRequest>> imageRequest = Optional.of(
          resolveBinaryContentRequest(image));
      message = messageService.create(request, imageRequest);
    } else {
      message = messageService.create(request, Optional.empty());
    }
    return ResponseEntity.status(HttpStatus.CREATED).body(message);
  }

  @Operation(summary = "Channel의 Message 목록 조회")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Message 목록 조회 성공", content = @Content(array = @ArraySchema(schema = @Schema(implementation = Message.class))))
  })
  @Parameter(name = "channelId", description = "조회할 Channel ID", required = true, schema = @Schema(type = "string", format = "uuid"))
  @GetMapping("/messages")
  public ResponseEntity<List<Message>> findAllByChannelId(@RequestParam UUID channelId) {
    List<Message> allByChannelId = messageService.findAllByChannelId(channelId);
    return ResponseEntity.status(HttpStatus.OK).body(allByChannelId);
  }

  @Operation(summary = "Message 내용 수정")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Message가 성공적으로 수정됨", content = @Content(schema = @Schema(implementation = Message.class))),
      @ApiResponse(responseCode = "404", description = "Message를 찾을 수 없음", content = @Content(schema = @Schema(example = "Message with id {messageId} not found"))),
  })
  @Parameter(name = "messageId", description = "수정할 messageID", required = true, schema = @Schema(type = "string", format = "uuid"))
  @PatchMapping(value = "/messages/{messageId}", consumes = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<String> updateMessage(
      @PathVariable UUID messageId,
      @RequestBody MessageUpdateRequest request) {

    messageService.update(messageId, request, Optional.empty());
    return ResponseEntity.status(HttpStatus.CREATED).body("변경완료!");
  }


  @Operation(summary = "Message 삭제")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "204", description = "Message가 성공적으로 삭제됨"),
      @ApiResponse(responseCode = "404", description = "Message를 찾을 수 없음", content = @Content(schema = @Schema(example = "Message with id {messageId} not found"))),
  })
  @Parameter(name = "messageId", description = "삭제할 Message ID", required = true, schema = @Schema(type = "string", format = "uuid"))
  @DeleteMapping("/messages/{messageId}")
  public ResponseEntity<String> deleteMessage(@PathVariable UUID messageId) {
    messageService.delete(messageId);
    return ResponseEntity.status(HttpStatus.OK).body("삭제 완료!");
  }


}
