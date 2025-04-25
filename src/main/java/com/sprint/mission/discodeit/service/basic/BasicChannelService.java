package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.Dto.channel.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.Dto.channel.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

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
@Primary
@Service("basicChannelService")
@RequiredArgsConstructor
public class BasicChannelService implements ChannelService {

    private final ChannelRepository channelRepository;
    private final MessageRepository messageRepository;
    private final ReadStatusRepository readStatusRepository;


    @Override
    public Channel createChannel(PrivateChannelCreateRequest request) {
        Objects.requireNonNull(request, "채널 입력 없음");
        List<User> users = request.getUsers();
        List<UUID> userIds = users.stream().map(User::getId).toList();

        // channel 생성
        Channel channel = channelRepository.createPrivateChannelByName();
        // readstatus 생성
        System.out.println("BasicChannelService.createChannel");
        readStatusRepository.createReadStatusByUserId(userIds, channel.getId())
                .forEach(e -> System.out.println("readStatus :" + e.getId()));
        System.out.println();

        return channel;
    }

    @Override
    public Channel createChannel(PublicChannelCreateRequest request) {
        Objects.requireNonNull(request, "채널 입력 없음");
        String channelName = Optional.ofNullable(request.getChannelName()).orElse("");
        String description = Optional.ofNullable(request.getDescription()).orElse("");

        List<User> users = request.getUsers();
        List<UUID> userIds = users.stream().map(User::getId).toList();

        // channel 생성
        Channel channel = channelRepository.createPublicChannelByName(channelName, description);

        // readstatus 생성
        System.out.println("BasicChannelService.createChannel");
        readStatusRepository.createReadStatusByUserId(userIds, channel.getId())
                .forEach(e -> System.out.println("readStatus :" + e.getId()));
        System.out.println();

        return channel;
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
