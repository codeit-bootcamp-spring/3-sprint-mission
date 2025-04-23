package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.file.FileChannelRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.util.FilePathUtil;
import com.sprint.mission.discodeit.v1.service.file.FileChannelService1;
import com.sprint.mission.discodeit.v1.service.jcf.JCFChannelService1;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
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
 * 2025. 4. 17.        doungukkim       null 확인 로직 추가
 */
public class BasicChannelService implements ChannelService {

    private final ChannelRepository cr;

    public BasicChannelService(ChannelRepository cr) {
        this.cr = cr;
    }

    @Override
    public Channel createChannel(String name) {
        Objects.requireNonNull(name, "이름을 입력 없음: BasicChannelService.createChannel");
        return cr.createChannelByName(name);
    }

    @Override
    public Channel findChannelById(UUID channelId) {
        Objects.requireNonNull(channelId, "체널 아이디 입력 없음: BasicChannelService.findChannelById");
        Channel result = cr.findChannelById(channelId);
        Objects.requireNonNull(result, "찾는 채널 없음: BasicChannelService.findChannelById");
        return result;
    }

    @Override
    public List<Channel> findAllChannel() {
        return cr.findAllChannel();
    }

    @Override
    public void updateChannel(UUID channelId, String name) {

        Objects.requireNonNull(channelId, "채널 아이디 입력 없음: BasicChannelService.updateChannel");
        Objects.requireNonNull(name, "이름 입력 없음: BasicChannelService.updateChannel");
        cr.updateChannel(channelId, name);
    }

    @Override
    public void deleteChannel(UUID channelId) {
        Objects.requireNonNull(channelId, "no channelId: BasicChannelService.deleteChannel");
        cr.deleteChannel(channelId);
    }
}
