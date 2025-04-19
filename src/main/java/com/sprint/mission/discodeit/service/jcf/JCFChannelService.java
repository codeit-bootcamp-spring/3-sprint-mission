package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.service.ChannelService;

import java.util.*;

public class JCFChannelService implements ChannelService {
    private final Map<UUID, Channel> data; //database

    public JCFChannelService() {
        this.data = new HashMap<>();
    }

    @Override
    public Channel create(String name, ChannelType type, String description) {
        Channel channel = new Channel(name, type, description);
        this.data.put(channel.getId(), channel);

        return channel;
    }

    @Override
    public Channel find(UUID channelId) {
        Channel channelNullable = this.data.get(channelId);

        return Optional.ofNullable(channelNullable).orElseThrow(() -> new NoSuchElementException("Channel with id " + channelId + " not found"));
    }

    @Override
    public List<Channel> findAll() {
        return new ArrayList<>(this.data.values());
    }

    @Override
    public Channel update(UUID channelId, String newName, String newDescription) {
        Channel channelNullable = this.data.get(channelId);
        Channel channel = Optional.ofNullable(channelNullable).orElseThrow(() -> new NoSuchElementException("Channel with id " + channelId + " not found"));
        channel.update(newName, newDescription);

        return channel;
    }

    @Override
    public void delete(UUID channelId) {
        if (!this.data.containsKey(channelId)) {
            throw new NoSuchElementException("Channel with id " + channelId + " not found");
        }
        this.data.remove(channelId);
    }

    @Override
    public void addMessageToChannel(UUID channelId, UUID messageId) {
        Channel channelNullable = this.data.get(channelId);
        Channel channel = Optional.ofNullable(channelNullable).orElseThrow(() -> new NoSuchElementException("Channel with id " + channelId + " not found"));

        channel.addMessage(messageId);
    }

    //    @Override
//    public List<User> getAttendees(Channel ch) {
//        Channel selected = this.data.get(ch.getId());
//        return selected.getAttendees();
//    }
//
//    @Override
//    public User joinChannel(Channel ch, User user) {
//        Channel selectedChannel = this.data.get(ch.getId());
//        //TODO : 참조변수 추가 방법 체크
//        selectedChannel.getAttendees().addAll(Arrays.asList(user));
//
//        return user;
//    }
//
//    @Override
//    public User leaveChannel(Channel ch, User user) {
//        Channel selectedChannel = this.data.get(ch.getId());
//        //TODO : 참조변수 방법 체크
//        selectedChannel.getAttendees().remove(user);
//
//        return user;
//    }
//
//    @Override
//    public List<User> readAttendees(Channel ch) {
//        Channel selectedChannel = this.data.get(ch.getId());
//        return selectedChannel.getAttendees();
//    }
//
}
