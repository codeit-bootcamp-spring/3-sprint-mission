package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.UserService;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class BasicChannelService implements ChannelService {
    private final ChannelRepository channelRepository;
    //
    private final UserService userService;

    public BasicChannelService(ChannelRepository channelRepository, UserService userService) {
        this.channelRepository = channelRepository;
        this.userService = userService;
    }

    @Override
    public Channel create(Channel channel) {
        return this.channelRepository.write(channel);
    }

    @Override
    public Channel find(UUID channelId) {
        return this.channelRepository.read(channelId);
    }

    @Override
    public List<Channel> findAll() {
        return this.channelRepository.readAll();
    }

    @Override
    public Channel update(UUID channelId, String newName, String newDescription) {
        Channel channel = this.find(channelId);
        channel.update(newName, newDescription);

        return this.create(channel);
    }

    @Override
    public void delete(UUID channelId) {
        this.channelRepository.delete(channelId);
    }

    @Override
    public void addMessageToChannel(UUID channelId, UUID messageId) {
        Channel channel = this.find(channelId);
        channel.addMessage(messageId);
        this.create(channel);
    }

    @Override
    public void addAttendeeToChannel(UUID channelId, UUID userId) {
        Channel channel = this.find(channelId);
        channel.addAttendee(userId);
        this.create(channel);
    }

    @Override
    public void removeAttendeeToChannel(UUID channelId, UUID userId) {
        Channel channel = this.find(channelId);
        channel.removeAttendee(userId);
        this.create(channel);
    }

    @Override
    public List<User> findAttendeesByChannel(UUID channelId) {
        Channel channel = this.find(channelId);
        List<User> attendees = new ArrayList<>();
        channel.getAttendees().forEach((userId -> {
            attendees.add(this.userService.find(userId));
        }));

        return attendees;
    }
}
