package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.ChannelService;

import java.util.*;

public class JCFChannelService implements ChannelService {
    private final Map<UUID, Channel> data; //database

    public JCFChannelService(Map<UUID, Channel> data) {
        this.data = data;
    }

    @Override
    public void create(Channel ch) {
        this.data.put(ch.getId(), ch);

    }

    @Override
    public Channel read(UUID id) {
        return this.data.get(id);
    }

    @Override
    public List<Channel> readAll() {
        return new ArrayList<>(this.data.values());
    }

    @Override
    public Channel update(Channel ch) {
        Channel selected = this.data.get(ch.getId());
        selected.update(ch);
        return selected;
    }

    @Override
    public boolean delete(Channel ch) {
        this.data.remove(ch.getId());

        //TODO : update return value
        return true;
    }

    @Override
    public User joinChannel(Channel ch, User user) {
        Channel selectedChannel = this.data.get(ch.getId());
        //TODO : 참조변수 추가 방법 체크
        selectedChannel.getAttendees().addAll(Arrays.asList(user));

        return user;
    }

    @Override
    public User leaveChannel(Channel ch, User user) {
        Channel selectedChannel = this.data.get(ch.getId());
        //TODO : 참조변수 방법 체크
        selectedChannel.getAttendees().remove(user);

        return user;
    }

    @Override
    public List<User> readAttendees(Channel ch) {
        Channel selectedChannel = this.data.get(ch.getId());
        return selectedChannel.getAttendees();
    }

    @Override
    public void sendMessage(Channel ch, Message msg) {
        Channel selectedChannel = this.data.get(ch.getId());
        //TODO : 참조변수 추가 방법 체크
        selectedChannel.getMessages().addAll(Arrays.asList(msg));
    }

    @Override
    public List<Message> readMessages(Channel ch) {
        Channel selectedChannel = this.data.get(ch.getId());
        return selectedChannel.getMessages();
    }

}
