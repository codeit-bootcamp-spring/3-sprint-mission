package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Message;

import java.util.List;
import java.util.UUID;

/*
 * 도메인 모델 별 CRUD(생성, 읽기, 모두 읽기, 수정, 삭제) 기능을 인터페이스로 선언
 * */

public interface MessageService {

    //QUESTION: 객체 생성 후 -> create 함수.  validation과 관계없이 객체 생성을 먼저 하는데 괜찮은가?
    public Message create(Message message);

    public Message find(UUID messageId);

    public List<Message> findAll();

    public Message update(UUID messageId, String newContent);

    public void delete(UUID messageId);

    public List<Message> findMessagesByChannel(UUID channelId);
}
