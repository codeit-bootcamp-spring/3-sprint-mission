package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.BinaryContent.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.Message.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.Message.MessageUpdateRequest;
import com.sprint.mission.discodeit.dto.MessageDto;
import com.sprint.mission.discodeit.dto.response.PageResponse;
import com.sprint.mission.discodeit.service.MessageService;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
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
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/messages")
public class MessageController {
    private final MessageService messageService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<MessageDto> create(
            @RequestPart("messageCreateRequest") MessageCreateRequest req,
            @RequestPart(value = "attachments", required = false) List<MultipartFile> files
    ) throws IOException {
        List<BinaryContentCreateRequest> atts = new ArrayList<>();
        if (files != null) {
            for (MultipartFile f : files) {
                atts.add(new BinaryContentCreateRequest(
                        f.getOriginalFilename(),
                        f.getContentType(),
                        f.getBytes()
                ));
            }
        }
        return ResponseEntity.ok(messageService.create(req, atts));
    }

    @GetMapping
    public ResponseEntity<PageResponse<MessageDto>> findAllByChannelId(
            @RequestParam("channelId") UUID channelId,
            @RequestParam(value = "cursor", required = false) String cursor,
            @RequestParam(value = "size", defaultValue = "50") int size
    ) {
        PageResponse<MessageDto> resp = messageService.findAllByChannelId(channelId, cursor, size);
        return ResponseEntity.ok(resp);
    }

    @PatchMapping("/{messageId}")
    public ResponseEntity<MessageDto> update(
            @PathVariable UUID messageId,
            @RequestBody MessageUpdateRequest req
    ) {
        return ResponseEntity.ok(messageService.update(messageId, req));
    }

    @DeleteMapping("/{messageId}")
    public ResponseEntity<Void> delete(@PathVariable UUID messageId) {
        messageService.delete(messageId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{messageId}")
    public ResponseEntity<MessageDto> find(@PathVariable UUID messageId) {
        return ResponseEntity.ok(messageService.find(messageId));
    }
}