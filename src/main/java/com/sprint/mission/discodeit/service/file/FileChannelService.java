package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.repository.file.FileChannelRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.util.FilePathUtil;
import com.sprint.mission.discodeit.util.FileSerializer;

import java.util.List;
import java.util.Objects;
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
        Objects.requireNonNull(name, "이름을 입력 없음: FileChannelService.createChannel");
        return fcr.createChannelByName(name);
    }

    @Override
    public Channel findChannelById(UUID channelId) {
        Objects.requireNonNull(channelId, "체널 아이디 입력 없음: FileChannelService.findChannelById");
        Channel result = fcr.findChannelById(channelId);
        Objects.requireNonNull(result, "찾는 채널 없음: FileChannelService.findChannelById");
        return result;
    }

    @Override
    public List<Channel> findAllChannel() {
        return fcr.findAllChannel();
    }

    @Override
    public void updateChannel(UUID channelId, String name) {
        Objects.requireNonNull(channelId, "채널 아이디 입력 없음: FileChannelService.updateChannel");
        Objects.requireNonNull(name, "이름 입력 없음: FileChannelService.updateChannel");
        fcr.updateChannel(channelId, name);
    }

    @Override
    public void deleteChannel(UUID channelId) {
        Objects.requireNonNull(channelId, "no channelId: FileChannelService.deleteChannel");
        fcr.deleteChannel(channelId);
    }
}
