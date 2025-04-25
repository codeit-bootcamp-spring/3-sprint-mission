package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.Dto.channel.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.Dto.channel.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.repository.jcf.JcfChannelRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
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
@RequiredArgsConstructor
public class JcfChannelService implements ChannelService {
    private final JcfChannelRepository jcfChannelRepository;


    // empty
    @Override
    public Channel createChannel(PrivateChannelCreateRequest request) {
        return null;
    }
    // empty
    @Override
    public Channel createChannel(PublicChannelCreateRequest request) {
        return null;
    }


    @Override
    public Channel findChannelById(UUID channelId) {
        Objects.requireNonNull(channelId, "no channel Id: JcfChannelService.findChannelById");
        Channel result = jcfChannelRepository.findChannelById(channelId);
        Objects.requireNonNull(result, "No Channel: JcfChannelService.findChannelById");
        return result;
    }


    @Override
    public List<Channel> findAllChannel() {
        return jcfChannelRepository.findAllChannel();
    }

    @Override
    public void updateChannel(UUID channelId, String name) {
        Objects.requireNonNull(channelId, "no channelId: JcfChannelService.updateChannel");
        Objects.requireNonNull(name, "no name: JcfChannelService.updateChannel");
        jcfChannelRepository.updateChannel(channelId, name);
    }

    @Override
    public void deleteChannel(UUID channelId) {
        Objects.requireNonNull(channelId, "no channelId: JcfChannelService.deleteChannel");
        jcfChannelRepository.deleteChannel(channelId);
    }
}
