package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.Dto.binaryContent.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.Dto.message.MessageAttachmentsCreateRequest;
import com.sprint.mission.discodeit.Dto.message.MessageCreateRequest;
import com.sprint.mission.discodeit.Dto.message.MessageUpdateRequest;
import com.sprint.mission.discodeit.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * packageName    : com.sprint.mission.discodeit.controller
 * fileName       : MessageController
 * author         : doungukkim
 * date           : 2025. 5. 11.
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2025. 5. 11.        doungukkim       최초 생성
 */
@RestController
@RequestMapping("api/messages")
@RequiredArgsConstructor
public class MessageController {
    private final MessageService messageService;

    // request, endpoint, (param, body, variable) : OK
    // service, repository : OK
    // response : OK
    // fail response : OK

    @GetMapping
    public ResponseEntity<?> findChannelMessages(@RequestParam UUID channelId) {
        System.out.println("MessageController.findChannelMessages");
        return messageService.findAllByChannelId(channelId);
    }


    // request, endpoint, (param, body, variable) : OK
    // service, repository : OK
    // response : OK
    // fail response :  OK
    @ResponseBody
    @RequestMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE, method = RequestMethod.POST)
    public ResponseEntity<?> creatMessage(
            @RequestPart("messageCreateRequest") MessageCreateRequest request,
            @RequestPart(value = "attachments", required = false) List<MultipartFile> attachmentFiles
    ) throws IOException {
        if (attachmentFiles.get(0).getSize() == 0) {
            return messageService.createMessage(request);
        }
        List<BinaryContentCreateRequest> attachmentRequests = new ArrayList<>();

        for (MultipartFile att : attachmentFiles) {
            attachmentRequests.add(new BinaryContentCreateRequest(
                    att.getOriginalFilename(),
                    att.getContentType(),
                    att.getBytes()));
        }
        System.out.println("MessageController.creatMessage");
        return messageService.createMessage(request, attachmentRequests);
    }

    // request, endpoint, (param, body, variable) : OK
    // service, repository : OK
    // response : OK
    // fail response : OK
    @DeleteMapping(path = "/{messageId}")
    public ResponseEntity<?> deleteMessage(@PathVariable UUID messageId) {
        System.out.println("MessageController.deleteMessage");
        return messageService.deleteMessage(messageId);
    }

    // request, endpoint, (param, body, variable) : OK
    // service, repository : OK
    // response : OK
    // fail response : OK
    @PatchMapping(path = "/{messageId}")
    public ResponseEntity<?> updateMessage(
            @PathVariable UUID messageId,
            @RequestBody MessageUpdateRequest request) {
        System.out.println("MessageController.updateMessage");
        return messageService.updateMessage(messageId, request);
    }

    //-=---------------------
    // 필요 없을 예정
    // request, endpoint, (param, body, variable) :
    // service, repository :
    // response :
    // fail response :
    @RequestMapping(path = "/create", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> createMessage(@RequestBody MessageCreateRequest request) {
        System.out.println("MessageController.createMessage");
        return messageService.createMessage(request);
    }

}
