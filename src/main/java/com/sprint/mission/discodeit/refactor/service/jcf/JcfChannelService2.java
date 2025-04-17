package com.sprint.mission.discodeit.refactor.service.jcf;

import com.sprint.mission.discodeit.refactor.entity.Channel2;
import com.sprint.mission.discodeit.refactor.repository.jcf.JcfChannelRepository2;
import com.sprint.mission.discodeit.refactor.service.ChannelService2;
import com.sprint.mission.discodeit.refactor.service.MessageService2;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
public class JcfChannelService2 implements ChannelService2 {
    JcfChannelRepository2 jcr = new JcfChannelRepository2();

    @Override
    public Channel2 createChannel(String name) {
        return jcr.createChannelByName(name);
    }

    @Override
    public Channel2 findChannelById(UUID channelId) {
        return jcr.findChannelById(channelId);
    }

    @Override
    public List<Channel2> findAllChannel() {
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
