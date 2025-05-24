package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentDTO;
import com.sprint.mission.discodeit.dto.message.MessageRequestDTO;
import com.sprint.mission.discodeit.dto.message.MessageResponseDTO;
import com.sprint.mission.discodeit.dto.message.MessageUpdateDTO;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.util.FileConverter;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
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

@Tag(name = "Message", description = "Message API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/messages")
public class MessageController {

  private final MessageService messageService;

  @Operation(summary = "Message 생성")
  @ApiResponses(
      value = {
          @ApiResponse(responseCode = "201", description = "Message가 성공적으로 생성됨"),
          @ApiResponse(responseCode = "404", description = "Channel 또는 User를 찾을 수 없음"
              , content = @Content(examples = {
              @ExampleObject(value = "Channel | Sender with id {channelId | authorId} not found")}))
      }
  )
  @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<Message> create(
      @RequestPart("messageCreateRequest") MessageRequestDTO messageRequestDTO,
      @Parameter(description = "Message 첨부 파일들")
      @RequestPart(value = "attachments", required = false) List<MultipartFile> attachedFiles) {
    List<BinaryContentDTO> binaryContentDTOS = FileConverter.resolveFileRequest(attachedFiles);

    Message createdMessage = messageService.create(messageRequestDTO, binaryContentDTOS);

    return ResponseEntity.status(HttpStatus.CREATED).body(createdMessage);
  }


  @Operation(summary = "Channel의 Message 목록 조회")
  @ApiResponse(responseCode = "200", description = "Message 읽음 상태 목록 조회 성공")
  @GetMapping
  public ResponseEntity<List<MessageResponseDTO>> findAllByChannelId(@RequestParam UUID channelId) {
    List<MessageResponseDTO> foundMessages = messageService.findAllByChannelId(channelId);

    return ResponseEntity.status(HttpStatus.OK).body(foundMessages);
  }

  @Operation(summary = "Message 내용 수정")
  @ApiResponses(
      value = {
          @ApiResponse(responseCode = "200", description = "Message가 성공적으로 수정됨"),
          @ApiResponse(responseCode = "404", description = "Message를 찾을 수 없음"
              , content = @Content(examples = {
              @ExampleObject(value = "Message with id {messageId} not found")}))
      }
  )
  @PatchMapping(path = "/{messageId}")
  public ResponseEntity<MessageResponseDTO> updateContent(
      @Parameter(description = "수정할 Message ID") @PathVariable UUID messageId,
      @RequestBody MessageUpdateDTO messageUpdateDTO) {
    MessageResponseDTO updatedMessage = messageService.updateContent(messageId,
        messageUpdateDTO.newContent());

    return ResponseEntity.status(HttpStatus.OK).body(updatedMessage);
  }

  @Operation(summary = "Message 삭제")
  @ApiResponses(
      value = {
          @ApiResponse(responseCode = "204", description = "Message가 성공적으로 삭제됨"),
          @ApiResponse(responseCode = "404", description = "Message를 찾을 수 없음"
              , content = @Content(examples = {
              @ExampleObject(value = "Message with id {messageId} not found")}))
      }
  )
  @DeleteMapping(path = "/{messageId}")
  public ResponseEntity<String> deleteById(
      @Parameter(description = "삭제할 Message ID") @PathVariable UUID messageId) {
    messageService.deleteById(messageId);

    return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
  }
}
