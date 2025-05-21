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

@RequiredArgsConstructor
@RequestMapping("/api/message")
@Controller
public class MessageController {

    private final MessageService messageService;

    // 메세지 전송( POST )
    @RequestMapping(
        path = "/send"
            , method = RequestMethod.POST
            , consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    @ResponseBody
    public ResponseEntity<Message> sendMessage(
            @RequestPart("message") MessageCreateRequest request,
            @RequestPart(value = "attachment", required = false) MultipartFile attachment
    ) {
        // 선택적 첨부파일 처리
        List<BinaryContentCreateRequest> attachments = resolveAttachmentRequest(attachment)
                // Optional 값을 List로 반환
                .map(Collections::singletonList)
                // 첨부파일이 없으면 빈 리스트로
                .orElse(Collections.emptyList());


        Message sendMessage = messageService.create(request, attachments);
        // HTTP 응답 커스터마이징
        // 상태코드 : 생성됨( 201 )
        // 응답 상태( 상태 코드 : 201 ), 내부 정보( 유저 생성 DTO, 선택적 프로필 이미지 ) 반환
        return ResponseEntity.status(HttpStatus.CREATED).body(sendMessage);
    }

    private Optional<BinaryContentCreateRequest> resolveAttachmentRequest(MultipartFile attachment) {

        if (attachment.isEmpty()) {
            // 컨트롤러가 요청받은 파라미터 중 MultipartFile 타입의 데이터가 비어있다면:
            return Optional.empty();
        } else {
            // 컨트롤러가 요청받은 파라미터 중 MultipartFile 타입의 데이터가 존재한다면:
            try {
                BinaryContentCreateRequest binaryContentCreateRequest = new BinaryContentCreateRequest(
                        attachment.getOriginalFilename(),
                        attachment.getBytes(),
                        attachment.getContentType()
                );
                return Optional.of(binaryContentCreateRequest);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }


    // 메세지 수정( PUT )
    @RequestMapping(
            path = "/{messageId}"
            , method = RequestMethod.PUT
            , consumes = MediaType.APPLICATION_JSON_VALUE
    )
    @ResponseBody
    public ResponseEntity<Message> update(
            @PathVariable UUID messageId,
            @RequestBody MessageUpdateRequest request
    ) {
        Message updateMessage = messageService.update(messageId, request);
        return ResponseEntity.status(HttpStatus.OK).body(updateMessage);
    }


    // 메세지 삭제( DEL )
    @RequestMapping(
            path = "/{messageId}"
            , method = RequestMethod.DELETE
    )
    @ResponseBody
    public ResponseEntity<String> delete(@PathVariable UUID messageId) {
        try {
            messageService.delete(messageId);
            return ResponseEntity.status(HttpStatus.OK).body("메세지 삭제에 성공했습니다");
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("찾고자하는 메세지는 존재하지 않습니다");
        }
    }


    // 특정 채널의 소속된 메세지 목록 조회( GET )
    @RequestMapping(
            path = "/channel/{channelId}"
            , method = RequestMethod.GET
            , produces = MediaType.APPLICATION_JSON_VALUE
    )
    @ResponseBody
    public ResponseEntity<List<Message>> findAllByChannelId(
            @PathVariable UUID channelId
    ) {
        List<Message> messages = messageService.findAllByChannelId(channelId);

        // 리스트가 비었을 경우 응답 정보가 없다고 판단하여 204 발생
        if (messages.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }

        return ResponseEntity.status(HttpStatus.OK).body(messages);
    }
}
