package com.sprint.mission.discodeit.refactor.service.file;

import com.sprint.mission.discodeit.refactor.entity.Channel2;
import com.sprint.mission.discodeit.refactor.repository.file.FileChannelRepository2;
import com.sprint.mission.discodeit.refactor.service.ChannelService2;
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
public class FileChannelService2 implements ChannelService2 {
    FilePathUtil filePathUtil = new FilePathUtil();
    FileSerializer fileSerializer = new FileSerializer();

    FileChannelRepository2 fcr = new FileChannelRepository2(filePathUtil, fileSerializer);

    @Override
    public Channel2 createChannel(String name) {
        return fcr.createChannelByName(name);
    }

    @Override
    public Channel2 findChannelById(UUID channelId) {
        return fcr.findChannelById(channelId);
    }

    @Override
    public List<Channel2> findAllChannel() {
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
