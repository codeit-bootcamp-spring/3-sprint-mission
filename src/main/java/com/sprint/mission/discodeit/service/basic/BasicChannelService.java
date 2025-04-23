package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
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
@Service("basicChannelService")
@RequiredArgsConstructor
@Primary
public class BasicChannelService implements ChannelService {

    private final ChannelRepository channelRepository;

    @Override
    public Channel createChannel(String name) {
        Objects.requireNonNull(name, "이름을 입력 없음: BasicChannelService.createChannel");
        return channelRepository.createChannelByName(name);
    }

    @Override
    public Channel findChannelById(UUID channelId) {
        Objects.requireNonNull(channelId, "체널 아이디 입력 없음: BasicChannelService.findChannelById");
        Channel result = channelRepository.findChannelById(channelId);
        Objects.requireNonNull(result, "찾는 채널 없음: BasicChannelService.findChannelById");
        return result;
    }

    @Override
    public List<Channel> findAllChannel() {
        return channelRepository.findAllChannel();
    }

    @Override
    public void updateChannel(UUID channelId, String name) {

        Objects.requireNonNull(channelId, "채널 아이디 입력 없음: BasicChannelService.updateChannel");
        Objects.requireNonNull(name, "이름 입력 없음: BasicChannelService.updateChannel");
        channelRepository.updateChannel(channelId, name);
    }

    @Override
    public void deleteChannel(UUID channelId) {
        Objects.requireNonNull(channelId, "no channelId: BasicChannelService.deleteChannel");
        channelRepository.deleteChannel(channelId);
    }
}
