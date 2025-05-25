package com.sprint.mission.discodeit.controller;


import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageUpdateRequest;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.service.MessageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

@RequiredArgsConstructor
@RequestMapping("/api/messages")
@RestController
public class MessageController {

  private final MessageService messageService;

  // 메세지 전송( POST )
  @Operation(summary = "메세지 전송", description = "메세지를 전송하며, 선택적으로 첨부파일을 포함할 수 있습니다")
  @ApiResponse(responseCode = "201", description = "메세지 전송 성공")
  @PostMapping
  public ResponseEntity<Message> sendMessage(
      @Parameter(description = "메세지 전송 요청 정보", required = true)
      @RequestPart("message") MessageCreateRequest request,

      @Parameter(description = "첨부파일", required = false)
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
    Message createdMessage = messageService.create(request, attachmentRequests);
    return ResponseEntity
        .status(HttpStatus.CREATED)
        .body(createdMessage);
  }


  // 메세지 수정( PATCH )
  @Operation(summary = "메세지 수정", description = "지정한 ID에 해당하는 메세지를 수정합니다")
  @ApiResponse(responseCode = "200", description = "메세지 수정 성공")
  @PatchMapping("/{messageId}")
  public ResponseEntity<Message> update(
      @Parameter(description = "메세지 ID", required = true)
      @PathVariable UUID messageId,
      @RequestBody MessageUpdateRequest request
  ) {
    Message updateMessage = messageService.update(messageId, request);
    return ResponseEntity.status(HttpStatus.OK)
        .body(updateMessage);
  }


  // 메세지 삭제( DEL )
  @Operation(summary = "메세지 삭제", description = "지정한 ID에 해당하는 메세지를 삭제합니다")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "메세지 삭제 성공"),
      @ApiResponse(responseCode = "404", description = "해당 메세지를 찾을 수 없습니다")
  })
  @DeleteMapping("/{messageId}")
  public ResponseEntity<String> delete(
      @Parameter(description = "메세지 ID", required = true)
      @PathVariable UUID messageId
  ) {
    try {
      messageService.delete(messageId);
      return ResponseEntity.status(HttpStatus.OK).body("메세지 삭제에 성공했습니다");
    } catch (NoSuchElementException e) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND)
          .body("찾고자하는 메세지는 존재하지 않습니다");
    }
  }


  // 특정 채널의 소속된 메세지 목록 조회( GET )
  @Operation(summary = "채널별 메세지 목록 조회", description = "특정 채널에 속한 모든 메세지를 조회합니다")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "메세지 목록 조회 성공"),
      @ApiResponse(responseCode = "204", description = "조회 가능한 메세지가 없습니다", content = @Content)
  })
  @GetMapping("")
  public ResponseEntity<List<Message>> findAllByChannelId(
      @Parameter(description = "채널 ID", required = true)
      @RequestParam("channelId") UUID channelId
  ) {
    List<Message> messages = messageService.findAllByChannelId(channelId);
    return ResponseEntity
        .status(HttpStatus.OK)
        .body(messages);
  }
}
