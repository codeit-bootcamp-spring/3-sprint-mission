package com.sprint.mission.discodeit.service;

/*
 * 도메인 모델 별 CRUD(생성, 읽기, 모두 읽기, 수정, 삭제) 기능을 인터페이스로 선언
 * */

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;

import java.util.List;
import java.util.UUID;

public interface ChannelService {

    public void create(Channel ch);

    public Channel read(UUID id);

    public List<Channel> readAll();

    public Channel update(UUID id, String name);

    public boolean delete(UUID id);

    public User joinChannel(Channel ch, User user);

    public User leaveChannel(Channel ch, User user);

    public List<User> readAttendees(Channel ch);

    // Q. 메세지 로직은 MessageService에 있어야하는데?
//    public void sendMessage(Channel ch, Message message);
//
//    public List<Message> readMessages(Channel ch);

}
