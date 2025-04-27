package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.Dto.channel.ChannelFindResponse;
import com.sprint.mission.discodeit.Dto.channel.ChannelUpdateRequest;
import com.sprint.mission.discodeit.Dto.channel.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.Dto.channel.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.repository.jcf.JcfChannelRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import lombok.RequiredArgsConstructor;
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
    public List<ChannelFindResponse> findAllByUserId(UUID userId) {
        return List.of();
    }
    // empty
    @Override
    public ChannelFindResponse findChannel(UUID channelId) {
        return null;
    }
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

    // empty
    @Override
    public void updateChannelName(ChannelUpdateRequest request) {

    }

    @Override
    public void deleteChannel(UUID channelId) {
        Objects.requireNonNull(channelId, "no channelId: JcfChannelService.deleteChannel");
        jcfChannelRepository.deleteChannel(channelId);
    }
}
