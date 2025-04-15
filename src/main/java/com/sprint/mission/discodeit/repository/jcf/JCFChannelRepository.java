package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;

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
public class JCFChannelRepository {
    private final List<Channel> data = new ArrayList<>();

    public void saveChannel(Channel channel) {
        data.add(channel);
    }

    // need update
    public List<Channel> findChannelsByUserId(List<UUID> channelIds) {
        List<Channel> result = new ArrayList<>();
        for (UUID channelId : channelIds) {
            result.add(findChannelById(channelId));
        }
        return result;
    }

    public Channel findChannelById(UUID channelId) {
        return data.stream()
                .filter(channel -> channel.getId().equals(channelId))
                .findFirst()
                .orElse(null);
    }

    public List<Channel> findAllChannel() {
        return data;
    }

    public void updateChannelName(UUID id, String title) {
        for (Channel channel : data) {
            if (channel.getId().equals(id)) {
                channel.setTitle(title);
            }
        }
    }

    public void deleteChannel(UUID channelId) {

        data.removeIf(channel -> channel.getId().equals(channelId));
    }


    public void addMessageInChannel(UUID channelId, Message message) {
        for (Channel channel : data) {
            if (channel.getId().equals(channelId)) {
                channel.getMessages().add(message);
            }
        }
    }

    public void deleteMessageInChannel(UUID channelId, UUID messageId) {
        findChannelById(channelId).getMessages().removeIf(message -> message.getId().equals(messageId));
    }

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
