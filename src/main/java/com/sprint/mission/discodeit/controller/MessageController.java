package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageUpdateRequest;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/messages")
public class MessageController {

  private final MessageService messageService;

  @RequestMapping(
      path ="/create",
      consumes = MediaType.MULTIPART_FORM_DATA_VALUE
  )
  public ResponseEntity<Message> createMessage(@RequestPart("messageCreateRequest") MessageCreateRequest messageCreateRequest,
                                               @RequestPart(value = "attachments", required = false) List<MultipartFile> attachments
  ) {
    List<BinaryContentCreateRequest> attachmentRequests = Optional.ofNullable(attachments)
        .map(files -> files.stream()
            .map(file -> {
              try{
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
    return ResponseEntity.status(HttpStatus.OK).body(newMessage);
  }

  @RequestMapping("/update")
  public ResponseEntity<Message> updateMessage(@RequestParam("messageId")UUID messageId,
                                               @RequestBody MessageUpdateRequest request) {
    Message updatedMessage = messageService.updateMessage(messageId, request);
    return ResponseEntity.status(HttpStatus.OK).body(updatedMessage);
  }

  @RequestMapping("/delete")
  public ResponseEntity<Void> deleteMessage(@RequestParam("messageId")UUID messageId,
                                               @RequestParam("senderId") UUID senderId) {
    messageService.deleteMessage(messageId, senderId);
    return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
  }

  //  특정 채널의 메시지 목록을 조회
  @RequestMapping("/findAll")
  public ResponseEntity<List<Message>> findAllMessageInChannel(@RequestParam("channelId")UUID channelId){
    List<Message> messages = messageService.findAllByChannelId(channelId);
    return ResponseEntity.status(HttpStatus.OK).body(messages);
  }

}
