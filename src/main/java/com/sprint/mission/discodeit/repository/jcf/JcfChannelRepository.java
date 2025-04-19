
package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.repository.ChannelRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * packageName    : com.sprint.mission.discodeit.refactor.repository
 * fileName       : JcfChannelRepository
 * author         : doungukkim
 * date           : 2025. 4. 17.
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2025. 4. 17.        doungukkim       최초 생성
 */
public class JcfChannelRepository implements ChannelRepository {

    Map<UUID, Channel> data = new HashMap<>();

    public Channel createChannelByName(String name) {
        Channel channel = new Channel(name);
        data.put(channel.getId(), channel);
        return channel;
    }

    public Channel findChannelById(UUID channelId) {
        return data.get(channelId);
    }

    public List<Channel> findAllChannel() {
        return data.values().stream().toList();
    }

    public void updateChannel(UUID channelId, String name) {
        if (data.get(channelId) == null) {
            throw new RuntimeException("파일 없음: JcfChannelRepository.updateChannel");
        }
        data.get(channelId).setName(name);
    }

    public void deleteChannel(UUID channelId) {
        data.remove(channelId);
    }

}
