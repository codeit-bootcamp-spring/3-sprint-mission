package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.file.FileChannelRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.v1.service.file.FileChannelService1;
import com.sprint.mission.discodeit.v1.service.jcf.JCFChannelService1;

import java.util.List;
import java.util.UUID;

/**
 * packageName    : com.sprint.mission.discodeit.service.basic
 * fileName       : BasicChannelService
 * author         : doungukkim
 * date           : 2025. 4. 17.
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2025. 4. 17.        doungukkim       최초 생성
 */
public class BasicChannelService implements ChannelService {
    private ChannelRepository cr;

    public BasicChannelService(ChannelRepository cr) {
        this.cr = cr;
    }

    @Override
    public Channel createChannel(String name) {
        return cr.createChannelByName(name);
    }

    @Override
    public Channel findChannelById(UUID channelId) {
        return cr.findChannelById(channelId);
    }

    @Override
    public List<Channel> findAllChannel() {
        return cr.findAllChannel();
    }

    @Override
    public void updateChannel(UUID channelId, String name) {
        cr.updateChannel(channelId, name);
    }

    @Override
    public void deleteChannel(UUID channelId) {
        cr.deleteChannel(channelId);

    }
}
