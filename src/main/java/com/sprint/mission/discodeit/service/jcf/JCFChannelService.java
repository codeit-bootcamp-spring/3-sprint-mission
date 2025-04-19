package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.UserService;

import java.util.*;

public class JCFChannelService implements ChannelService {
    private final Map<UUID, Channel> data; //database
    private final UserService userService;

    public JCFChannelService(UserService userService) {
        this.data = new HashMap<>();
        this.userService = userService;
    }

    @Override
    public Channel create(Channel channel) {
        this.data.put(channel.getId(), channel);
        this.addAttendeeToChannel(channel.getId(), channel.getOwnerId());

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

    @Override
    public void addAttendeeToChannel(UUID channelId, UUID userId) {
        Channel channelNullable = this.data.get(channelId);
        Channel channel = Optional.ofNullable(channelNullable).orElseThrow(() -> new NoSuchElementException("Channel with id " + channelId + " not found"));

        try {
            this.userService.find(userId);
        } catch (NoSuchElementException e) {
            throw e;
        }

        channel.addAttendee(userId);
    }

    @Override
    public void removeAttendeeToChannel(UUID channelId, UUID userId) {
        Channel channelNullable = this.data.get(channelId);
        Channel channel = Optional.ofNullable(channelNullable).orElseThrow(() -> new NoSuchElementException("Channel with id " + channelId + " not found"));

        try {
            this.userService.find(userId);
        } catch (NoSuchElementException e) {
            throw e;
        }

        channel.removeAttendee(userId);
    }

    @Override
    public List<User> findAttendeesByChannel(UUID channelId) {
        Channel channelNullable = this.data.get(channelId);
        Channel channel = Optional.ofNullable(channelNullable).orElseThrow(() -> new NoSuchElementException("Channel with id " + channelId + " not found"));
        try {
            List<User> attendees = new ArrayList<>();
            channel.getAttendees().forEach((userId -> {
                attendees.add(this.userService.find(userId));
            }));

            return attendees;
        } catch (NoSuchElementException e) {
            throw e;
        }
    }

}
