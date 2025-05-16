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
@Controller
@RequestMapping("api/messages")
@RequiredArgsConstructor
public class MessageController {
    private final MessageService messageService;

    // request, endpoint, (param, body, variable) : OK
    // service, repository : OK
    // response : OK
    // fail response : OK
    @ResponseBody
    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<?> findChannelMessages(@RequestParam UUID channelId) {
        return messageService.findAllByChannelId(channelId);
    }

    // request, endpoint, (param, body, variable) :
    // service, repository :
    // response :
    // fail response :
    @ResponseBody
    @RequestMapping(path = "/create", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> createMessage(@RequestBody MessageCreateRequest request) {
        return messageService.createMessage(request);
    }

    // request, endpoint, (param, body, variable) :
    // service, repository :
    // response :
    // fail response :
    @ResponseBody
    @RequestMapping(path = "/create-attachment", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> createAttachment(
            @RequestPart("messageAttachmentsCreateRequest") MessageAttachmentsCreateRequest request,
            @RequestPart(value = "attachment") List<MultipartFile> attachmentFiles
    ) throws IOException {
        List<BinaryContentCreateRequest> attachmentRequests = new ArrayList<>();
        for (MultipartFile att : attachmentFiles) {
            attachmentRequests.add(new BinaryContentCreateRequest(
                    att.getOriginalFilename(),
                    att.getContentType(),
                    att.getBytes()));
        }
        return messageService.createMessage(request, attachmentRequests);
    }

    @ResponseBody
    @RequestMapping(path = "/update-message", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> updateMessage(@RequestBody MessageUpdateRequest request) {
        return messageService.updateMessage(request);
    }

    @ResponseBody
    @RequestMapping(path = "/delete")
    public ResponseEntity<?> updateMessage(@RequestParam UUID messageId) {
        return messageService.deleteMessage(messageId);
    }
}
