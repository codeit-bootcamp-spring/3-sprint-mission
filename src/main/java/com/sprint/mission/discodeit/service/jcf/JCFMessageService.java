package com.sprint.mission.discodeit.service.jcf;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.service.MessageService;

public class JCFMessageService implements MessageService {

    // 데이터를 저장하는 필드 (ID를 키로 사용)
    private final Map<UUID, Message> data;

    // 생성자: HashMap을 사용해 초기화
    public JCFMessageService() {
        this.data = new HashMap<>();
    }

    @Override
    public Message createMessage(String content, UUID authorId, UUID channelId) {
        Message message = new Message(content, authorId, channelId); // 새로운 메시지 생성
        data.put(message.getMessageId(), message); // 데이터 저장
        return message;
    }

    @Override
    public Message getMessageById(UUID messageId) {
        return data.get(messageId); // ID로 메시지 조회
    }

    @Override
    public List<Message> getMessagesByChannel(UUID channelId) {
        // 특정 채널의 메시지 필터링
        return data.values().stream()
                .filter(message -> message.getChannelId().equals(channelId))
                .collect(Collectors.toList());
    }

    @Override
    public List<Message> getMessagesByAuthor(UUID authorId) {
        // 특정 작성자의 메시지 필터링
        return data.values().stream()
                .filter(message -> message.getAuthorId().equals(authorId))
                .collect(Collectors.toList());
    }

    @Override
    public void updateMessage(UUID messageId, String updatedContent) {
        Message message = data.get(messageId);
        if (message != null) {
            message.updateContent(updatedContent); // 메시지 내용 업데이트
        }
    }

    @Override
    public void deleteMessage(UUID messageId) {
        data.remove(messageId); // 메시지 데이터 삭제
    }
}
