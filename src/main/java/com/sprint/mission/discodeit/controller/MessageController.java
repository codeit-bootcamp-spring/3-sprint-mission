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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
@RequestMapping("/api/message")
@Controller
public class MessageController {

    private final MessageService messageService;

    // 메시지 생성
    @RequestMapping(
            path = "/create",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    @ResponseBody
    public ResponseEntity<Message> createMessage(
            @RequestPart("messageCreateRequest") MessageCreateRequest request,
            @RequestPart(value = "attachedFiles", required = false) List<MultipartFile> attachedFiles
    ){
        List<BinaryContentCreateRequest> attachedFileRequest = resolveAttachmentsRequest(attachedFiles);

        Message createMessage = messageService.createMessage(request, attachedFileRequest);
        System.out.println(createMessage);

        return ResponseEntity.status(HttpStatus.CREATED).body(createMessage);
    }


    // 채널의 메시지 조회
    @RequestMapping(
            path = "/find"
    )
    @ResponseBody
    public ResponseEntity<List<Message>> getMessageByChannelId(
            @RequestParam UUID channelId,
            @RequestParam UUID userId,
            @RequestParam String password
    ){
        List<Message> getMessage =  messageService.findallByChannelId(channelId, userId, password);

        return ResponseEntity.ok(getMessage);
    }

    // 메시지 수정
    @RequestMapping(
            path = "/update"
    )
    @ResponseBody
    public ResponseEntity<String> updateMessage(
            @RequestPart("messageUpdateRequest") MessageUpdateRequest request
    ){
        boolean updated = messageService.updateMessage(request);

        return updated ? ResponseEntity.ok("메시지가 수정되었습니다.") : ResponseEntity.badRequest().body("메시지 내용 수정 실패");
    }

    //메시지 삭제
    @RequestMapping(
            path = "/delete"
    )
    @ResponseBody
    public ResponseEntity<String> deleteMessage(
            @RequestParam UUID messageId,
            @RequestParam UUID senderId,
            @RequestParam String password

    ){
        messageService.deletedMessage(messageId, senderId, password);
        return ResponseEntity.ok("메시지가 삭제되었습니다.");
    }

    

    // 다중 파일 처리
    // MultipartFile 타입의 요청값을 BinaryContentCreateRequest 타입으로 변환하기 위한 메서드
    private Optional<BinaryContentCreateRequest> resolveProfileRequest(MultipartFile attachedFile) {

        if(attachedFile.isEmpty()) {
            // 컨트롤러가 요청받은 파라미터 중 MultipartFile 타입의 데이터가 비어있다면:
            return Optional.empty();
        } else {
            // 컨트롤러가 요청받은 파라미터 중 MultipartFile 타입의 데이터가 존재한다면:
            try {
                BinaryContentCreateRequest binaryContentCreateRequest = new BinaryContentCreateRequest(
                        attachedFile.getOriginalFilename(),
                        attachedFile.getContentType(),
                        attachedFile.getBytes()
                );
                return Optional.of(binaryContentCreateRequest);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    // 다중 파일 처리
    private List<BinaryContentCreateRequest> resolveAttachmentsRequest(List<MultipartFile> attachedFiles) {
        if (attachedFiles == null || attachedFiles.isEmpty()) return List.of();

        return attachedFiles.stream()
                .filter(file -> !file.isEmpty())
                .map(file -> {
                    try {
                        return new BinaryContentCreateRequest(
                                file.getOriginalFilename(),
                                file.getContentType(),
                                file.getBytes()
                        );
                    } catch (IOException e) {
                        throw new RuntimeException("첨부파일 처리 중 오류 발생", e);
                    }
                })
                .toList();
    }
}
