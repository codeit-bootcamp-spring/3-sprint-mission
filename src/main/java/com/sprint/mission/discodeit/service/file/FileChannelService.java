package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.repository.file.FileChannelRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.util.FilePathUtil;
import com.sprint.mission.discodeit.util.FileSerializer;

import java.util.List;
import java.util.UUID;

/**
 * packageName    : com.sprint.mission.discodeit.refactor.service.file
 * fileName       : FileChannelService2
 * author         : doungukkim
 * date           : 2025. 4. 17.
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2025. 4. 17.        doungukkim       최초 생성
 */
public class FileChannelService implements ChannelService {
    FileChannelRepository fcr = new FileChannelRepository();

    @Override
    public Channel createChannel(String name) {
        return fcr.createChannelByName(name);
    }

    @Override
    public Channel findChannelById(UUID channelId) {
        return fcr.findChannelById(channelId);
    }

    @Override
    public List<Channel> findAllChannel() {
        return fcr.findAllChannel();
    }

    @Override
    public void updateChannel(UUID channelId, String name) {
        fcr.updateChannel(channelId, name);
    }

    @Override
    public void deleteChannel(UUID channelId) {
        fcr.deleteChannel(channelId);

    }
}
