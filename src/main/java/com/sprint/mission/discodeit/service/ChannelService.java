package com.sprint.mission.discodeit.service;

/*
 * 도메인 모델 별 CRUD(생성, 읽기, 모두 읽기, 수정, 삭제) 기능을 인터페이스로 선언
 * */

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.User;

import java.util.List;
import java.util.UUID;

public interface ChannelService {

    public Channel create(String name, ChannelType type, String description, UUID ownerId);

    public Channel find(UUID channelId);

    public List<Channel> findAll();

    public Channel update(UUID channelId, String newName, String newDescription);

    public void delete(UUID channelId);

    public void addMessageToChannel(UUID channelId, UUID messageId);

    public void addAttendeeToChannel(UUID channelId, UUID userId);

    public List<User> findAttendeesByChannel(UUID channelId);

//    public User joinChannel(Channel ch, User user);
//
//    public User leaveChannel(Channel ch, User user);

}
