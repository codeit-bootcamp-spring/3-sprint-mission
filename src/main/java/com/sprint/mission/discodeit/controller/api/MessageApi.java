package com.sprint.mission.discodeit.controller.api;

import com.sprint.mission.discodeit.dto.message.MessageRequestDTO;
import com.sprint.mission.discodeit.dto.message.MessageResponseDTO;
import com.sprint.mission.discodeit.dto.message.MessageUpdateDTO;
import com.sprint.mission.discodeit.entity.Message;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "Message", description = "Message API")
public interface MessageApi {

  @Operation(summary = "Message 생성")
  @ApiResponses(
      value = {
          @ApiResponse(responseCode = "201", description = "Message가 성공적으로 생성됨"),
          @ApiResponse(responseCode = "404", description = "Channel 또는 User를 찾을 수 없음"
              , content = @Content(examples = {
              @ExampleObject(value = "Channel | Sender with id {channelId | authorId} not found")}))
      }
  )
  ResponseEntity<Message> create(
      @RequestPart("messageCreateRequest") MessageRequestDTO messageRequestDTO,
      @Parameter(description = "Message 첨부 파일들")
      @RequestPart(value = "attachments", required = false) List<MultipartFile> attachedFiles);


  @Operation(summary = "Channel의 Message 목록 조회")
  @ApiResponse(responseCode = "200", description = "Message 읽음 상태 목록 조회 성공")
  ResponseEntity<List<MessageResponseDTO>> findAllByChannelId(@RequestParam UUID channelId);

  @Operation(summary = "Message 내용 수정")
  @ApiResponses(
      value = {
          @ApiResponse(responseCode = "200", description = "Message가 성공적으로 수정됨"),
          @ApiResponse(responseCode = "404", description = "Message를 찾을 수 없음"
              , content = @Content(examples = {
              @ExampleObject(value = "Message with id {messageId} not found")}))
      }
  )
  ResponseEntity<MessageResponseDTO> updateContent(
      @Parameter(description = "수정할 Message ID") @PathVariable UUID messageId,
      @RequestBody MessageUpdateDTO messageUpdateDTO);

  @Operation(summary = "Message 삭제")
  @ApiResponses(
      value = {
          @ApiResponse(responseCode = "204", description = "Message가 성공적으로 삭제됨"),
          @ApiResponse(responseCode = "404", description = "Message를 찾을 수 없음"
              , content = @Content(examples = {
              @ExampleObject(value = "Message with id {messageId} not found")}))
      }
  )
  ResponseEntity<String> deleteById(
      @Parameter(description = "삭제할 Message ID") @PathVariable UUID messageId);
}
