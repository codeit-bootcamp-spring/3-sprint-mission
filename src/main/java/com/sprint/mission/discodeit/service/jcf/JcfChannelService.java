package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.Dto.channel.*;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.jcf.JcfChannelRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
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
@Profile("jcf")
public class JcfChannelService implements ChannelService {
    private final JcfChannelRepository jcfChannelRepository;


    // empty
    @Override
    public List<ChannelFindResponse> findAllByUserId(ChannelFindByUserIdRequest request) {
        return List.of();
    }
    // empty
    @Override
    public ChannelFindResponse find(ChannelFindRequest request) {
        return null;
    }
    // empty
    @Override
    public ChannelCreateResponse createChannel(PrivateChannelCreateRequest request) {
        return null;
    }
    // empty
    @Override
    public ChannelCreateResponse createChannel(PublicChannelCreateRequest request) {
        return null;
    }
    // empty
    @Override
    public void update(ChannelUpdateRequest request) {}
    // empty
    @Override
    public List<Channel> findAllChannel() {
        return List.of();
    }

    @Override
    public void deleteChannel(UUID channelId) {
        Objects.requireNonNull(channelId, "no channelId: JcfChannelService.deleteChannel");
        jcfChannelRepository.deleteChannel(channelId);
    }
}
