package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.UserService;

import java.util.List;

public interface ChannelService {
    void create(String name, User user);
    List<Channel> read(String id);
    List<Channel> readAll();
    void update(String id, String name);
    void delete(String id);

}
