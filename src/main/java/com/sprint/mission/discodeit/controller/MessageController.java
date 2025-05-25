package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageUpdateRequest;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.service.MessageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.io.IOException;
import java.util.ArrayList;
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

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/messages")
@Tag(
    name = "Message",
    description = "Message API"
)
public class MessageController {

  private final MessageService messageService;

  @Operation(
      summary = "Message 생성",
      operationId = "create_2"
  )
  @ApiResponses(
      value = {
          @ApiResponse(
              responseCode = "201",
              description = "Message가 성공적으로 생성됨",
              content = @Content(mediaType = "*/*", schema = @Schema(implementation = Message.class))
          ),
          @ApiResponse(
              responseCode = "404",
              description = "Channel 또는 User를 찾을 수 없음",
              content = @Content(mediaType = "*/*",
                  examples = @ExampleObject(value = "Channel | Author with id {channelId | authorId} not found")
              )
          )
      }
  )
  @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<Message> createMessage(
      @RequestPart("messageCreateRequest") MessageCreateRequest messageCreateRequest,
      @RequestPart(value = "attachments", required = false) List<MultipartFile> attachments
  ) {
    List<BinaryContentCreateRequest> attachmentRequests = Optional.ofNullable(attachments)
        .map(files -> files.stream()
            .map(file -> {
              try {
                return new BinaryContentCreateRequest(
                    file.getOriginalFilename(),
                    file.getBytes(),
                    file.getContentType()
                );
              } catch (IOException e) {
                throw new RuntimeException(e);
              }
            })
            .toList())
        .orElse(new ArrayList<>());
    Message newMessage = messageService.createMessage(messageCreateRequest, attachmentRequests);
    return ResponseEntity.status(HttpStatus.CREATED).body(newMessage);
  }

  @Operation(
      summary = "Message 내용 수정",
      operationId = "update_2",
      tags = {"Message"}
  )
  @ApiResponses(
      value = {
          @ApiResponse(
              responseCode = "200",
              description = "Message가 성공적으로 수정됨",
              content = @Content(mediaType = "*/*",
                  schema = @Schema(implementation = Message.class))
          ),
          @ApiResponse(
              responseCode = "404",
              description = "Message를 찾을 수 없음",
              content = @Content(mediaType = "*/*",
                  examples = @ExampleObject(value = "Message with id {messageId} not found")
              )
          )
      }
  )
  @Parameter(
      name = "messageId",
      in = ParameterIn.PATH,
      description = "수정할 Message ID",
      required = true,
      schema = @Schema(type = "String", format = "uuid")
  )
  @io.swagger.v3.oas.annotations.parameters.RequestBody(
      content = @Content(
          mediaType = MediaType.APPLICATION_JSON_VALUE,
          schema = @Schema(implementation = MessageUpdateRequest.class)
      )
  )
  @PatchMapping("/{messageId}")
  public ResponseEntity<Message> updateMessage(@PathVariable("messageId") UUID messageId,
      @RequestBody MessageUpdateRequest request) {
    Message updatedMessage = messageService.updateMessage(messageId, request);
    return ResponseEntity.status(HttpStatus.OK).body(updatedMessage);
  }

  @Operation(
      summary = "Message 삭제",
      operationId = "delete_1",
      tags = {"Message"}
  )
  @ApiResponses(
      value = {
          @ApiResponse(
              responseCode = "204",
              description = "Message가 성공적으로 삭제됨",
              content = @Content(mediaType = "*/*",
                  examples = @ExampleObject(value = "Success")
              )
          ),
          @ApiResponse(
              responseCode = "404",
              description = "Message를 찾을 수 없음",
              content = @Content(mediaType = "*/*",
                  examples = @ExampleObject(value = "Message with id {messageId} not found")
              )
          )
      }
  )
  @Parameter(
      name = "messageId",
      in = ParameterIn.PATH,
      description = "삭제할 Message ID",
      required = true,
      schema = @Schema(type = "string", format = "uuid")
  )
  @DeleteMapping("/{messageId}")
  public ResponseEntity<Void> deleteMessage(@PathVariable("messageId") UUID messageId,
      @RequestParam("senderId") UUID senderId) {
    messageService.deleteMessage(messageId, senderId);
    return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
  }

  //  특정 채널의 메시지 목록을 조회
  @Operation(
      summary = "Channel의 Message 목록 조회",
      operationId = "findAllByChannelId"
  )
  @ApiResponse(
      responseCode = "200",
      description = "Message 목록 조회 성공",
      content = @Content(mediaType = "*/*", array = @ArraySchema(schema = @Schema(implementation = Message.class)))
  )
  @Parameter(
      name = "channelId",
      in = ParameterIn.QUERY,
      description = "조회할 Channel ID",
      required = true,
      schema = @Schema(type = "string", format = "uuid")
  )
  @GetMapping
  public ResponseEntity<List<Message>> findAllMessageInChannel(
      @RequestParam("channelId") UUID channelId) {
    List<Message> messages = messageService.findAllByChannelId(channelId);
    return ResponseEntity.status(HttpStatus.OK).body(messages);
  }

}