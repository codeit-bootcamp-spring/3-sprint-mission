package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.message.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.message.MessageUpdateRequest;
import com.sprint.mission.discodeit.entity.Message;

import java.util.List;
import java.util.UUID;

public interface MessageService {

    /**
     * 새 메시지 생성 (첨부파일 포함 가능)
     * @param request 메시지 생성 요청 DTO
     * @return 생성된 메시지 (또는 MessageDto)
     */
    Message createMessage(MessageCreateRequest request); // DTO 활용, 첨부파일 고려

    /**
     * 특정 채널의 모든 메시지 조회
     * @param channelId 채널 ID
     * @return 해당 채널의 메시지 목록 (또는 List<MessageDto>)
     */
    List<Message> findAllByChannelId(UUID channelId); // 메소드명 변경 및 파라미터 추가

    /**
     * 특정 메시지 조회 
     * @param messageId 메시지 ID
     * @return 조회된 메시지 (또는 MessageDto)
     */
    Message findMessageById(UUID messageId); // 기존 findOne 등과 유사

    /**
     * 메시지 내용 수정
     * @param messageId 수정할 메시지 ID
     * @param request 메시지 수정 요청 DTO
     * @return 수정된 메시지 (또는 MessageDto)
     */
    Message updateMessage(UUID messageId, MessageUpdateRequest request); // DTO 활용

    /**
     * 메시지 삭제 (관련 첨부파일 함께 삭제)
     * @param messageId 삭제할 메시지 ID
     */
    void deleteMessage(UUID messageId);
}
