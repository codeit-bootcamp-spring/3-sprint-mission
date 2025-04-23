package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.repository.jcf.JcfChannelRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * packageName    : com.sprint.mission.discodeit.service.jcf
 * fileName       : JcfChannelService2
 * author         : doungukkim
 * date           : 2025. 4. 17.
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2025. 4. 17.        doungukkim       최초 생성
 */
@Service
public class JcfChannelService implements ChannelService {
    JcfChannelRepository jcr = new JcfChannelRepository();

    @Override

    public Channel createChannel(String name) {
        Objects.requireNonNull(name, "이름 입력 없음: JcfChannelService.createChannel");
        return jcr.createChannelByName(name);
    }

    @Override
    public Channel findChannelById(UUID channelId) {
        Objects.requireNonNull(channelId, "no channel Id: JcfChannelService.findChannelById");
        Channel result = jcr.findChannelById(channelId);
        Objects.requireNonNull(result, "No Channel: JcfChannelService.findChannelById");
        return result;
    }


    @Override
    public List<Channel> findAllChannel() {
        return jcr.findAllChannel();
    }

    @Override
    public void updateChannel(UUID channelId, String name) {
        Objects.requireNonNull(channelId, "no channelId: JcfChannelService.updateChannel");
        Objects.requireNonNull(name, "no name: JcfChannelService.updateChannel");
        jcr.updateChannel(channelId, name);
    }

    @Override
    public void deleteChannel(UUID channelId) {
        Objects.requireNonNull(channelId, "no channelId: JcfChannelService.deleteChannel");
        jcr.deleteChannel(channelId);
    }
}
