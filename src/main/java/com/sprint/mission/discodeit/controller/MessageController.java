package com.sprint.mission.discodeit.controller;


import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageUpdateRequest;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.service.MessageService;
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
  @PostMapping
  public ResponseEntity<Message> create(
      @RequestPart("message") MessageCreateRequest request,
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
  @PatchMapping("/{messageId}")
  public ResponseEntity<Message> update(
      @PathVariable UUID messageId,
      @RequestBody MessageUpdateRequest request
  ) {
    Message updateMessage = messageService.update(messageId, request);
    return ResponseEntity.status(HttpStatus.OK)
        .body(updateMessage);
  }


  // 메세지 삭제( DEL )
  @DeleteMapping("/{messageId}")
  public ResponseEntity<String> delete(
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
  @GetMapping("")
  public ResponseEntity<List<Message>> findAllByChannelId(
      @RequestParam("channelId") UUID channelId
  ) {
    List<Message> messages = messageService.findAllByChannelId(channelId);
    return ResponseEntity
        .status(HttpStatus.OK)
        .body(messages);
  }
}
