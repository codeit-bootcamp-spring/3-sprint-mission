package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Message;

import java.util.List;
import java.util.UUID;

public interface MessageService {

    // Create: 새로운 메시지 생성
    Message createMessage(String content, UUID authorId, UUID channelId);

    // Read: ID를 통해 메시지 가져오기
    Message getMessageById(UUID messageId);

    // Read: 특정 채널의 모든 메시지 가져오기
    List<Message> getMessagesByChannel(UUID channelId);

    // Read: 특정 작성자의 모든 메시지 가져오기
    List<Message> getMessagesByAuthor(UUID authorId);

    // Update: 메시지 내용 수정
    void updateMessage(UUID messageId, String updatedContent);

    // Delete: 메시지 삭제
    void deleteMessage(UUID messageId);
}