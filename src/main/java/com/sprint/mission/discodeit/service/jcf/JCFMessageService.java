package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.MessageService;

import java.util.*;
import java.util.stream.Collectors;

// JCF 활용, 데이터를 저장할 수 있는 필드(data)를 final 로 선언, 생성자에서 초기화
// data 필드를 활용해 생성, 조회, 수정, 삭제하는 메소드를 구현
public class JCFMessageService implements MessageService {
    private final Map<UUID, Message> data;

    public JCFMessageService(Map<UUID, Message> data) {
        this.data = new HashMap<>();
    }


    // Create Override
    @Override
    public void createMessage(Message message) {        // 새로운 메세지 생성
        data.put(message.getMessageId(), message);
    }

    // Read Override
    @Override
    public Message readMessage(UUID id) {       // 특정 ID를 가진 메세지 조회
        return data.get(id);
    }

    @Override
    public List<Message> readMessageByType(String messageType) {        // 중복 가능한 값(타입)에 따른 다중 조회
        return data.values().stream()       // Stream API
                .filter(message -> message.getMessageType().equals(messageType))    // 람다식 적용
                .collect(Collectors.toList());      // List 자료형으로 저장
    }

    @Override
    public List<Message> readAllMessages() {        // 전체 메세지 조회
        return new ArrayList<>(data.values());
    }

    // Update Override
    @Override
    public Message updateMessage(UUID id, Message message) {
        if (data.containsKey(id)) {     // 특정 메세지의 ID 탐색 후 정보 수정
            data.put(id, message);      // 덮어씌우기
            return message;
        }
        return null;
    }

    // Delete Override
    @Override
    public boolean deleteMessage(UUID id) {     // 특정 ID를 가진 메세지 제거
        return data.remove(id) != null;
    }




//    // SendMessage Override           << 대기
//    @Override
//    public void sendMessage(User fromUser, Channel toChannel, String content, String messageType) {                    // 대상, 장소, 내용      + 추가점 (타임스탬프)
//
//    }
}
