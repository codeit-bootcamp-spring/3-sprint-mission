
package com.sprint.mission.discodeit.refactor.repository.jcf;

import com.sprint.mission.discodeit.refactor.entity.Channel2;
import com.sprint.mission.discodeit.refactor.repository.ChannelRepository2;

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
public class JcfChannelRepository2 implements ChannelRepository2 {

    Map<UUID, Channel2> data = new HashMap<>();

    public Channel2 createChannelByName(String name) {
        Channel2 channel = new Channel2(name);
        data.put(channel.getId(), channel);
        return channel;
    }

    public Channel2 findChannelById(UUID channelId) {
        return data.get(channelId);
    }

    public List<Channel2> findAllChannel() {
        return data.values().stream().toList();
    }

    public void updateChannel(UUID channelId, String name) {
        data.get(channelId).setName(name);
    }

    public void deleteChannel(UUID channelId) {
        data.remove(channelId);
    }

}
