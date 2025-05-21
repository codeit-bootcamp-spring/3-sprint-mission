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
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/api/message")
public class MessageController {

  private final MessageService messageService;

  @RequestMapping(
      path = "/send"
      , method = RequestMethod.POST
      , consumes = MediaType.MULTIPART_FORM_DATA_VALUE
  )
  @ResponseBody
  public ResponseEntity<Message> send(
      @RequestPart("messageCreateRequest") MessageCreateRequest messageCreateRequest,
      @RequestPart(value = "attachments", required = false) List<MultipartFile> attachments
  ) {
    List<BinaryContentCreateRequest> attachmentRequests = new ArrayList<>();

    if (attachments != null) {
      for (MultipartFile file : attachments) {
        resolveAttachmentRequest(file).ifPresent(attachmentRequests::add);
      }
    }

    Message created = messageService.create(messageCreateRequest, attachmentRequests);
    return ResponseEntity.status(HttpStatus.CREATED).body(created);
  }

  @RequestMapping(
      path = "/update"
      , method = RequestMethod.PUT
  )
  @ResponseBody
  public ResponseEntity<Message> update(
      @RequestParam("messageId") UUID messageId,
      @RequestBody MessageUpdateRequest request
  ) {

    Message updatedmessage = messageService.update(messageId, request);
    return ResponseEntity.status(HttpStatus.OK).body(updatedmessage);

  }

  @RequestMapping(
      path = "/delete"
      , method = RequestMethod.DELETE
  )
  @ResponseBody
  public ResponseEntity<String> delete(
      @RequestParam("messageId") UUID messageId
  ) {

    messageService.delete(messageId);
    return ResponseEntity.status(HttpStatus.NO_CONTENT).build();

  }

  @RequestMapping(
      path = "/search"
      , method = RequestMethod.GET
  )
  @ResponseBody
  public ResponseEntity<List<Message>> search(
      @RequestParam("channelId") UUID channelId
  ) {
    List<Message> messages = messageService.findAllByChannelId(channelId);

    return ResponseEntity.status(HttpStatus.OK).body(messages);
  }


  private Optional<BinaryContentCreateRequest> resolveAttachmentRequest(MultipartFile file) {
    if (file == null || file.isEmpty()) {
      return Optional.empty();
    }
    try {
      return Optional.of(new BinaryContentCreateRequest(
          file.getOriginalFilename(),
          file.getContentType(),
          file.getBytes()
      ));
    } catch (IOException e) {
      throw new RuntimeException("첨부파일 처리 중 오류 발생: " + file.getOriginalFilename(), e);
    }
  }

}
