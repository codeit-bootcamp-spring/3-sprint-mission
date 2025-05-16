package com.sprint.mission.discodeit.controller;


import com.sprint.mission.discodeit.dto.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.MessageUpdateRequest;
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
import java.io.IOException;
import java.net.URI;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
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
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RequiredArgsConstructor
@RestController
@ResponseBody
@RequestMapping("/api/messages")
@Tag(name = "Message", description = "Message API")
public class MessageController {

  private final MessageService messageService;

  /* 메세지 생성 */
  //FIXME : api-docs랑 다름
  @Operation(summary = "Message 생성")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "404", description = "Channel 또는 User를 찾을 수 없음", content = @Content(examples = {
          @ExampleObject(value = "Channel | Author with id {channelId | authorId} not found")
      })),
      @ApiResponse(responseCode = "201", description = "Message가 성공적으로 생성됨", content = @Content(schema = @Schema(implementation = Message.class)))
  })
  @io.swagger.v3.oas.annotations.parameters.RequestBody(
      required = true,
      content = {
          @Content(mediaType = "multipart/form-data", schema = @Schema(implementation = MessageCreateRequest.class)),
          @Content(mediaType = "multipart/form-data", array = @ArraySchema(schema = @Schema(type = "string", format = "binary", description = "Message 첨부 파일들"))),
      }
  )
  @PostMapping(consumes = {MediaType.APPLICATION_JSON_VALUE,
      MediaType.MULTIPART_FORM_DATA_VALUE})
  public ResponseEntity<Message> createMessage(
      @RequestPart("messageCreateRequest") MessageCreateRequest messageCreateRequest,
      @RequestPart(value = "attachments", required = false) List<MultipartFile> attachments
  ) {
    List<BinaryContentCreateRequest> binaryContentCreateRequests =
        Optional.ofNullable(attachments).map(
            p -> p.stream().map(this::resolveProfileRequest)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .toList()
            // XXX : 왜 null를 return하지 않고 빈 리스트를 반환했지?
        ).orElse(Collections.emptyList());
    try {
      Message createdMessage = messageService.create(messageCreateRequest,
          binaryContentCreateRequests);
      return ResponseEntity.created(URI.create(createdMessage.getId().toString()))
          .body(createdMessage);
    } catch (NoSuchElementException | IllegalAccessException E) {
      return ResponseEntity.unprocessableEntity().build();
    }
  }


  /* 메세지 수정 */
  @Operation(summary = "Message 내용 수정")
  @Parameters({
      @Parameter(
          name = "messageId",
          description = "수정할 Message ID",
          required = true,
          in = ParameterIn.PATH,
          schema = @Schema(type = "string", format = "uuid")
      )
  })
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Message가 성공적으로 수정됨", content = @Content(schema = @Schema(implementation = Message.class))),
      @ApiResponse(responseCode = "404", description = "Message를 찾을 수 없음", content = @Content(examples = {
          @ExampleObject(value = "Message with id {messageId} not found")
      }))
  })
  @io.swagger.v3.oas.annotations.parameters.RequestBody(
      required = true,
      content = @Content(schema = @Schema(implementation = MessageUpdateRequest.class))
  )
  @PatchMapping(path = "{messageId}")
  public ResponseEntity<Message> update(
      @PathVariable String messageId,
      @RequestBody MessageUpdateRequest messageUpdateRequest
  ) {
    Message updatedMessage = messageService.update(parseStringToUuid(messageId),
        messageUpdateRequest);
    return ResponseEntity.ok().body(updatedMessage);
  }

  /* 메세지 삭제 */
  @Operation(summary = "Message 삭제")
  @Parameters({
      @Parameter(
          name = "messageId",
          description = "삭제할 Message ID",
          required = true,
          in = ParameterIn.PATH,
          schema = @Schema(type = "string", format = "uuid")
      )
  })
  @ApiResponses(value = {
      @ApiResponse(responseCode = "204", description = "Message가 성공적으로 삭제됨"),
      @ApiResponse(responseCode = "404", description = "Message를 찾을 수 없음", content = @Content(examples = {
          @ExampleObject(value = "Message with id {messageId} not found")
      }))
  })

  @DeleteMapping(path = "{messageId}")
  public ResponseEntity<Void> delete(
      @PathVariable String messageId
  ) {
    messageService.delete(parseStringToUuid(messageId));
    //TODO : 204번으로 리턴
    return ResponseEntity.ok().build();
  }

  /* 특정 채널의 메시지 목록을 조회 */
  @Operation(summary = "Channel의 Message 목록 조회")
  @Parameters({
      @Parameter(
          name = "channelId",
          description = "조회할 Channel ID",
          required = true,
          in = ParameterIn.QUERY,
          schema = @Schema(type = "string", format = "uuid")
      )
  })
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Message 목록 조회 성공", content = @Content(array = @ArraySchema(schema = @Schema(implementation = Message.class))))
  })
  @GetMapping()
  public ResponseEntity<List<Message>> findAllByChannelId(
      @RequestParam String channelId
  ) {
    List<Message> MessageList = messageService.findAllByChannelId(parseStringToUuid(channelId));
    return ResponseEntity.ok().body(MessageList);
  }

  //FIXME : util로 빼기 (모든 컨트롤러에서 중복됨)
  /* MultipartFile 타입 -> BinaryContentCreateRequest 타입으로 변경 */
  private Optional<BinaryContentCreateRequest> resolveProfileRequest(MultipartFile profile) {
    if (profile.isEmpty()) {
      return Optional.empty();
    } else {
      try {
        BinaryContentCreateRequest binaryContentCreateRequest = new BinaryContentCreateRequest(
            profile.getOriginalFilename(), profile.getContentType(), profile.getBytes());
        return Optional.of(binaryContentCreateRequest);
      } catch (IOException e) {
        throw new RuntimeException();
      }
    }
  }

  //FIXME : util로 빼기 (모든 컨트롤러에서 중복됨)
  /* String 타입 -> UUID 타입으로 변경 */
  private UUID parseStringToUuid(String id) {
    try {
      return UUID.fromString(id);
    } catch (IllegalArgumentException e) {
      throw new RuntimeException("올바른 파라미터 형식이 아닙니다.");
    }
  }

}
