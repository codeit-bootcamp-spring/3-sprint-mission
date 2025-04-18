package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.repository.jcf.JcfChannelRepository;
import com.sprint.mission.discodeit.service.ChannelService;

import java.util.List;
import java.util.UUID;

/**
 * packageName    : com.sprint.mission.discodeit.refactor.service.jcf
 * fileName       : JcfChannelService2
 * author         : doungukkim
 * date           : 2025. 4. 17.
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2025. 4. 17.        doungukkim       최초 생성
 */
public class JcfChannelService implements ChannelService {
    JcfChannelRepository jcr = new JcfChannelRepository();

    @Override
    public Channel createChannel(String name) {
        return jcr.createChannelByName(name);
    }

    @Override
    public Channel findChannelById(UUID channelId) {
        return jcr.findChannelById(channelId);
    }

    @Override
    public List<Channel> findAllChannel() {
        return jcr.findAllChannel();
    }

    @Override
    public void updateChannel(UUID channelId, String name) {
        jcr.updateChannel(channelId, name);
    }

    @Override
    public void deleteChannel(UUID channelId) {
        jcr.deleteChannel(channelId);
    }
}
