
package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

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

@Repository
@ConditionalOnProperty(name = "repository.type", havingValue = "jcf")
public class JcfChannelRepository implements ChannelRepository {

    Map<UUID, Channel> data = new HashMap<>();

    @Override
    public Channel createPrivateChannelByName() {
        Channel channel = new Channel();
        data.put(channel.getId(), channel);
        return channel;
    }

    @Override
    public Channel createPublicChannelByName(String name, String description) {
        Channel channel = new Channel(name, description);
        data.put(channel.getId(), channel);
        return channel;
    }

    @Override
    public Channel findChannelById(UUID channelId) {
        return data.get(channelId);
    }

    @Override
    public List<Channel> findAllChannel() {
        return data.values().stream().toList();
    }

    @Override
    public void updateChannel(UUID channelId, String name) {
        if (data.get(channelId) == null) {
            throw new RuntimeException("파일 없음: JcfChannelRepository.updateChannel");
        }
        data.get(channelId).setName(name);
    }

    @Override
    public void deleteChannel(UUID channelId) {
        if (!data.containsKey(channelId)) {
            throw new IllegalStateException("no data to delete");
        }
        data.remove(channelId);
    }

}
