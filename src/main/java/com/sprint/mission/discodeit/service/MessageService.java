package com.sprint.mission.discodeit.service;


import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;

import java.util.List;
import java.util.UUID;

// 도메인 모델 별 CRUD(생성, 읽기, 모두 읽기, 수정, 삭제) 기능을 인터페이스로 선언
public interface MessageService {

    void createMessage(Message message);                   // 생성

    Message readMessage(UUID id);                          // 특정 메세지 조회

    List<Message> readMessageByType(String messageType);   // 특정 타입만 다중 조회

    List<Message> readAllMessages();                       // 전체 메세지 조회

    Message updateMessage(UUID id, Message message);       // 특정 메세지 수정

    boolean deleteMessage(UUID id);                        // 특정 메세지 제거


//    // 메세지 전송 기능
//    public void sendMessage(User fromUser, Channel toChannel, String content, String messageType);
}
