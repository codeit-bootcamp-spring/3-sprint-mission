package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.BinaryContent.BinaryContentCreateRequestDTO;
import com.sprint.mission.discodeit.dto.Message.MessageCreateRequestDTO;
import com.sprint.mission.discodeit.dto.Message.MessageUpdateRequestDTO;
import com.sprint.mission.discodeit.dto.UserStatus.UserStatusUpdateRequestDTO;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
@RequestMapping("/api/message")
@Controller
public class MessageController {

    private final MessageService messageService;
    private final ChannelService channelService;
    private final UserStatusService userStatusService;

    /**
     * 메시지 전송
     *
     * @param messageCreateRequestDTO 메시지 생성 요청 DTO
     * @param attachments 첨부파일 목록
     * @return 생성된 Message (HTTP 201 CREATED)
     */
    @RequestMapping(
            path = "/create",
            method = RequestMethod.POST,
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    @ResponseBody
    public ResponseEntity<Message> create(
            @RequestPart("messageCreateRequestDTO") MessageCreateRequestDTO messageCreateRequestDTO,
            @RequestPart(value = "attachments", required = false) List<MultipartFile> attachments
    ) {
        // 첨부파일 생성 요청 DTO 목록 설정
        List<BinaryContentCreateRequestDTO> attachmentsRequestDTO =
                Optional.ofNullable(attachments)
                        .orElse(List.of()).stream()
                        .map(this::resolveAttachmentRequest)
                        .flatMap(Optional::stream)
                        .toList();

        // 메시지 생성
        Message createdMessage = messageService.create(messageCreateRequestDTO,attachmentsRequestDTO);

        // UserStatus 업데이트
        UUID authorId = createdMessage.getAuthorId();
        UserStatusUpdateRequestDTO updateRequestDTO = new UserStatusUpdateRequestDTO(Instant.now());

        userStatusService.updateByUserId(authorId, updateRequestDTO);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(createdMessage);
    }

    /**
     * MultipartFile 타입의 요청값을 BinaryContentCreateReqeust 타입으로 변환
     *
     * @param attachment 첨부파일 (MultipartFile)
     * @return 생성된 바이너리 파일 생성 요청 DTO
     */
    private Optional<BinaryContentCreateRequestDTO> resolveAttachmentRequest(MultipartFile attachment){
        if (attachment.isEmpty()) {
            // 컨트롤러가 요청받은 파라미터 중 MultipartFile 타입의 데이터가 비어있다면:
            return Optional.empty();
        } else {
            // 컨트롤러가 요청받은 파라미터 중 MultipartFile 타입의 데이터가 존재한다면:
            try {
                BinaryContentCreateRequestDTO binaryContentCreateRequestDTO = new BinaryContentCreateRequestDTO(
                        attachment.getOriginalFilename(),
                        attachment.getContentType(),
                        attachment.getBytes()
                );
                return Optional.of(binaryContentCreateRequestDTO);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * 메시지 수정
     *
     * @param messageId 수정할 메시지 ID
     * @param messageUpdateRequestDTO 메시지 수정 요청 DTO
     * @return 수정된 Message (HTTP 200 OK)
     */
    @RequestMapping(
            path = "/update",
            method = RequestMethod.PUT,
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Message> update(
            @RequestParam UUID messageId,
            @RequestBody MessageUpdateRequestDTO messageUpdateRequestDTO
    ) {
        // 메시지 수정
        Message updatedMessage = messageService.update(messageId,messageUpdateRequestDTO);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(updatedMessage);
    }

    /**
     * 메시지 삭제
     *
     * @param messageId 삭제할 메시지 ID
     * @return 삭제 완료 메시지 (HTTP 200 OK)
     */
    @RequestMapping(
            path = "/delete",
            method = RequestMethod.DELETE
    )
    public ResponseEntity<String> delete(
            @RequestParam UUID messageId
    ) {
        // 메시지 삭제
        messageService.delete(messageId);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body("메시지가 삭제되었습니다.");
    }

    /**
     * 특정 채널의 메시지 목록 조회
     *
     * @param channelId 채널 ID
     * @return 조회된 메시지 목록 (HTTP 200 OK)
     */
    @RequestMapping(
            path = "/findAll",
            method = RequestMethod.GET
    )
    public ResponseEntity<List<Message>> findAllByChannelId(
            @RequestParam UUID channelId
    ) {
        // 채널 유효성 검사
        channelService.find(channelId);

        // 채널의 메시지 목록 조회
        List<Message> messages = messageService.findAllByChannelId(channelId);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(messages);
    }
}
