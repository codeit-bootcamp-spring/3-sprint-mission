package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.CreateBinaryContentRequest;
import com.sprint.mission.discodeit.dto.CreateMessageRequest;
import com.sprint.mission.discodeit.dto.CreateReadStatusRequest;
import com.sprint.mission.discodeit.dto.UpdateMessageRequest;
import com.sprint.mission.discodeit.entitiy.Message;
import com.sprint.mission.discodeit.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/api/message")
@RequiredArgsConstructor
public class MessageController {

    private final MessageService messageService;

    //MultipartFile 타입의 요청값을 CreateBinaryContentRequest 타입으로 변환하기 위한 메서드
    private List<CreateBinaryContentRequest> resolveBinaryContentRequest(List<MultipartFile> image) {
        List<CreateBinaryContentRequest> createBinaryContentRequests = new ArrayList<>();
        //컨트롤러가 요청받은 파라미터 중 MultipartFile 타입의 데이터가 존재한다면:
        try {
            for(MultipartFile image1 : image){
                createBinaryContentRequests.add( new CreateBinaryContentRequest(
                        image1.getOriginalFilename(),
                        image1.getContentType(),
                        image1.getBytes())
                );
            }
            return createBinaryContentRequests;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @RequestMapping(path = "/create", method = RequestMethod.POST, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseBody
    public ResponseEntity<Message> createMessage(@RequestPart("createMessageRequest") CreateMessageRequest request, @RequestPart(value = "image", required = false) List<MultipartFile> image) {
        Message message;
        if (image != null && !image.isEmpty()) {
            Optional<List<CreateBinaryContentRequest>> imageRequest = Optional.of(resolveBinaryContentRequest(image));
            message = messageService.create(request, imageRequest);
        } else {
            message = messageService.create(request, Optional.empty());
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(message);
    }

    @RequestMapping(path = "/search", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<List<Message>> searchByChannelId(@RequestParam UUID channelId) {
        List<Message> allByChannelId = messageService.findAllByChannelId(channelId);
        return ResponseEntity.status(HttpStatus.OK).body(allByChannelId);
    }

    @RequestMapping(path = "/update", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<String> updateMessage(@RequestPart("updateMessageRequest") UpdateMessageRequest request, @RequestPart(value = "image", required = false) List<MultipartFile> image) {
        if (image != null && !image.isEmpty()) {
            Optional<List<CreateBinaryContentRequest>> imageRequest = Optional.of(resolveBinaryContentRequest(image));
            messageService.update(request, imageRequest);
        } else {
            messageService.update(request, Optional.empty());
        }
        return ResponseEntity.status(HttpStatus.CREATED).body("변경완료!");
    }

    @RequestMapping(path = "/delete", method = RequestMethod.DELETE)
    @ResponseBody
    public ResponseEntity<String> deleteMessage(@RequestParam UUID messageId) {
        messageService.delete(messageId);
        return ResponseEntity.status(HttpStatus.OK).body("삭제 완료!");
    }


}
