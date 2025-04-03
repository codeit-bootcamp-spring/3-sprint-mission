package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Message;

import java.util.List;
import java.util.UUID;

/*
 * 도메인 모델 별 CRUD(생성, 읽기, 모두 읽기, 수정, 삭제) 기능을 인터페이스로 선언
 * */

public interface MessageService {

    public void create(Message msg);

    public Message read(UUID id);

    public List<Message> readAll();

    public Message update(Message msg);

    public boolean delete(Message msg);

}
