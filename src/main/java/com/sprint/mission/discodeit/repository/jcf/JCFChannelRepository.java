package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.ChannelRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * packageName    : com.sprint.mission.discodeit.repository.jcf
 * fileName       : JCFChannelRepository
 * author         : doungukkim
 * date           : 2025. 4. 15.
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2025. 4. 15.        doungukkim       최초 생성
 */
public class JCFChannelRepository implements ChannelRepository {
    private final List<Channel> data = new ArrayList<>();

    @Override
    public void saveChannel(Channel channel) {
        try{
            data.add(channel);
        } catch (Exception e) {
        }
    }
    @Override
    public Channel findChannelById(UUID channelId) {
        return data.stream()
                .filter(channel -> channel.getId().equals(channelId))
                .findFirst()
                .orElse(null);
    }

    @Override
    public List<Channel> findChannelsByChannelIds(List<UUID> channelIds) {
        List<Channel> result = new ArrayList<>();
        for (UUID channelId : channelIds) {
            result.add(findChannelById(channelId));
        }
        return result;
    }


    @Override
    public List<Channel> findAllChannels() {
        return data;
    }
    @Override
    public void updateChannelNameById(UUID id, String title) {
        for (Channel channel : data) {
            if (channel.getId().equals(id)) {
                channel.setTitle(title);
            }
        }
    }
    @Override
    public void deleteChannelById(UUID channelId) {
        data.removeIf(channel -> channel.getId().equals(channelId));
    }

    @Override
    public void addMessageInChannel(UUID channelId, Message message) {
        for (Channel channel : data) {
            if (channel.getId().equals(channelId)) {
                channel.getMessages().add(message);
            }
        }
    }
    @Override
    public void deleteMessageInChannel(UUID channelId, UUID messageId) {
        findChannelById(channelId).getMessages().removeIf(message -> message.getId().equals(messageId));
    }
    @Override
    public void addUserInChannel(UUID channelId, UUID userId) {
        for (Channel channel : data) {
            if (channel.getId().equals(channelId)) {
                List<UUID> usersIds = channel.getUsersIds();
                usersIds.add(userId);
                channel.setUsersIds(usersIds);
            }
        }
    }
































}
