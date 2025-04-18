package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;

import java.util.Map;
import java.util.UUID;

public interface MessageService {
    // 생성
    public Message createMessage(Channel sendChannel, User sendUser, String msgContent);
    // 전체 조회
    public Map<UUID, Message> getMessages();
    // 조회(ID)
    public Message getMessageById(UUID id);
    // 조회(내용)
    public Message getMessageByContent(String msgContent);
    // 수정
    public Message updateMessage(Message message, String msgContent);
    // 삭제
    public Message deleteMessage(UUID id);
    // 저장
    void saveMessages();
}
