package com.sprint.mission.discodeit.v1.repository.jcf;

import com.sprint.mission.discodeit.v1.entity.Channel1;
import com.sprint.mission.discodeit.v1.entity.Message1;
import com.sprint.mission.discodeit.v1.repository.ChannelRepository1;

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
public class JCFChannelRepository1 implements ChannelRepository1 {
    private final List<Channel1> data = new ArrayList<>();

    @Override
    public void saveChannel(Channel1 channel) {
        try{
            data.add(channel);
        } catch (Exception e) {
        }
    }
    @Override
    public Channel1 findChannelById(UUID channelId) {
        return data.stream()
                .filter(channel -> channel.getId().equals(channelId))
                .findFirst()
                .orElse(null);
    }

    @Override
    public List<Channel1> findChannelsByChannelIds(List<UUID> channelIds) {
        List<Channel1> result = new ArrayList<>();
        for (UUID channelId : channelIds) {
            result.add(findChannelById(channelId));
        }
        return result;
    }


    @Override
    public List<Channel1> findAllChannels() {
        return data;
    }
    @Override
    public void updateChannelNameById(UUID id, String title) {
        for (Channel1 channel : data) {
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
    public void addMessageInChannel(UUID channelId, Message1 message) {
        for (Channel1 channel : data) {
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
        for (Channel1 channel : data) {
            if (channel.getId().equals(channelId)) {
                List<UUID> usersIds = channel.getUsersIds();
                usersIds.add(userId);
                channel.setUsersIds(usersIds);
            }
        }
    }
































}
