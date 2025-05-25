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

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/messages")
public class MessageController {

  private final MessageService messageService;

  @GetMapping
  public ResponseEntity<List<Message>> findAllByChannelId(@RequestParam UUID channelId) {
    return ResponseEntity.ok(messageService.findAllByChannelId(channelId));
  }

  @ResponseStatus(HttpStatus.CREATED)
  @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<Message> create(
          @RequestPart("messageCreateRequest") MessageCreateRequest request,
          @RequestPart(value = "attachments", required = false) List<MultipartFile> attachments) {
    List<BinaryContentCreateRequest> files = toBinaryRequests(attachments);
    Message created = messageService.create(request, files);
    return ResponseEntity.status(HttpStatus.CREATED).body(created);   // 201
  }

  @PatchMapping("/{messageId}")
  public ResponseEntity<Message> update(@PathVariable UUID messageId,
                                        @RequestBody MessageUpdateRequest request) {
    return ResponseEntity.ok(messageService.update(messageId, request));
  }

  @ResponseStatus(HttpStatus.NO_CONTENT)
  @DeleteMapping("/{messageId}")
  public ResponseEntity<Void> delete(@PathVariable UUID messageId) {
    messageService.delete(messageId);
    return ResponseEntity.noContent().build();                        // 204
  }

  /* ---------- util ---------- */
  private List<BinaryContentCreateRequest> toBinaryRequests(List<MultipartFile> files) {
    if (files == null) return List.of();
    return files.stream()
            .map(f -> new BinaryContentCreateRequest(
                    f.getOriginalFilename(),
                    f.getContentType(),
                    f.getSize(),
                    extractBytes(f)))
            .collect(Collectors.toList());
  }
  private byte[] extractBytes(MultipartFile f) {
    try { return f.getBytes(); }
    catch (Exception e) { throw new RuntimeException(e); }
  }
}
