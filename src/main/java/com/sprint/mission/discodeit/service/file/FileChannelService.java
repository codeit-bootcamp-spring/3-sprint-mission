package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.Dto.channel.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.Dto.channel.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.file.FileChannelRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * packageName    : com.sprint.mission.discodeit.service.file
 * fileName       : FileChannelService2
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
public class FileChannelService implements ChannelService {
    private final FileChannelRepository fileChannelRepository;
//    private final FileReadStatusRepository FileReadStatusRepository;

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
        Objects.requireNonNull(channelId, "체널 아이디 입력 없음: FileChannelService.findChannelById");
        Channel result = fileChannelRepository.findChannelById(channelId);
        Objects.requireNonNull(result, "찾는 채널 없음: FileChannelService.findChannelById");
        return result;
    }

    @Override
    public List<Channel> findAllChannel() {
        return fileChannelRepository.findAllChannel();
    }

    @Override
    public void updateChannel(UUID channelId, String name) {
        Objects.requireNonNull(channelId, "채널 아이디 입력 없음: FileChannelService.updateChannel");
        Objects.requireNonNull(name, "이름 입력 없음: FileChannelService.updateChannel");
        fileChannelRepository.updateChannel(channelId, name);
    }

    @Override
    public void deleteChannel(UUID channelId) {
        Objects.requireNonNull(channelId, "no channelId: FileChannelService.deleteChannel");
        fileChannelRepository.deleteChannel(channelId);
    }
}
