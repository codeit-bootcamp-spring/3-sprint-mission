package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.Dto.binaryContent.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.Dto.message.MessageAttachmentsCreateRequest;
import com.sprint.mission.discodeit.Dto.message.MessageCreateRequest;
import com.sprint.mission.discodeit.Dto.message.MessageCreateResponse;
import com.sprint.mission.discodeit.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;
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
@RequestMapping("api/message/*")
@RequiredArgsConstructor
public class MessageController {
    private final MessageService messageService;

//    [ ] 메시지를 보낼 수 있다.
//    [ ] 메시지를 수정할 수 있다.
//    [ ] 메시지를 삭제할 수 있다.
//    [ ] 특정 채널의 메시지 목록을 조회할 수 있다.

    @ResponseBody
    @RequestMapping(path = "/create", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> createMessage(@RequestBody MessageCreateRequest request) {
        return messageService.createMessage(request);
    }

    @ResponseBody
    @RequestMapping(path = "/createAttachment", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> createAttachment(
            @RequestPart("messageAttachmentsCreateRequest")MessageAttachmentsCreateRequest request,
            @RequestPart(value = "attachment") MultipartFile attachmentFile
            ) throws IOException {

        BinaryContentCreateRequest attachmentRequest = new BinaryContentCreateRequest(
                attachmentFile.getOriginalFilename(),
                attachmentFile.getContentType(),
                attachmentFile.getBytes());

        return messageService.createMessage(request, attachmentRequest);
    }

}
