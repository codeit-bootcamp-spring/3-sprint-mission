package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.data.ChannelDTO;
import com.sprint.mission.discodeit.dto.entity.ChannelType;
import com.sprint.mission.discodeit.dto.entity.Message;
import com.sprint.mission.discodeit.dto.entity.ReadStatus;
import com.sprint.mission.discodeit.dto.request.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.dto.entity.Channel;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import lombok.RequiredArgsConstructor;

import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
public class BasicChannelService implements ChannelService {
    private final ChannelRepository channelRepository;
    private final MessageRepository messageRepository;
    private final ReadStatusRepository readStatusRepository;

    @Override
    public Channel createPublicChannel(PublicChannelCreateRequest channelCreateRequest) {
        if (getChannelByName(channelCreateRequest.getName()) != null) {
            throw new IllegalArgumentException("[Channel] 이미 존재하는 채널명입니다. (" + channelCreateRequest.getName() + ")");
        }

        Channel channel = Channel.ofPublic(channelCreateRequest.getName(), channelCreateRequest.getDescription());
        channelRepository.save(channel);
        return channel;
    }

    @Override
    public Channel createPrivateChannel(PrivateChannelCreateRequest channelCreateRequest) {
        Channel channel = Channel.ofPrivate(channelCreateRequest.getMemberIds());
        channelRepository.save(channel);

        channelCreateRequest.getMemberIds().forEach(userId ->
            readStatusRepository.save(ReadStatus.of(userId, channel.getId()))
        );
        return channel;
    }

    @Override
    public ChannelDTO getChannel(UUID id) {
        Channel channel = channelRepository.loadById(id);
        Instant lastMessageAt = getLastMessageInChannel(channel.getId());

        return new ChannelDTO(
                channel.getType(),
                channel.getName(),
                channel.getId(),
                lastMessageAt,
                channel.getMemberIds()
        );
    }

    @Override
    public List<ChannelDTO> getAllChannelsByUserId(UUID userId) {
        return channelRepository.loadAll().stream()
                .filter(channel ->
                        channel.getType() == ChannelType.PUBLIC
                                || (channel.getType() == ChannelType.PRIVATE && channel.getMemberIds().contains(userId))
                )
                .map(channel -> {
                    Instant lastMessageAt = getLastMessageInChannel(channel.getId());
                    return new ChannelDTO(
                            channel.getType(),
                            channel.getName(),
                            channel.getId(),
                            lastMessageAt,
                            channel.getMemberIds()
                    );
                })
                .toList();
    }

    @Override
    public Channel getChannelByName(String name) {
        return channelRepository.loadByName(name);
    }

    @Override
    public Instant getLastMessageInChannel(UUID channelId) {
        return messageRepository.loadByChannelId(channelId).stream()
                .map(Message::getCreatedAt)
                .max(Comparator.naturalOrder())
                .orElse(null);
    }

    @Override
    public void updateChannel(PublicChannelUpdateRequest publicChannelUpdateRequest) {
        Channel channel = channelRepository.loadById(publicChannelUpdateRequest.getChannelId());

        if (channel == null || channel.getType() == ChannelType.PRIVATE) {
            throw new IllegalArgumentException("[Channel] 유효하지 않은 채널이거나 PRIVATE 채널입니다.");
        }

        try {
            System.out.println(channelRepository.update(publicChannelUpdateRequest.getChannelId(), publicChannelUpdateRequest.getName()));
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void deleteChannel(UUID id) {
        Channel channel = channelRepository.loadById(id);

        try {
            messageRepository.deleteByChannelId(id);
            channel.getMemberIds().forEach(readStatusRepository::deleteByUserId);
            channelRepository.delete(id);
        } catch (RuntimeException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void joinChannel(UUID userId, UUID channelId) {
        Channel channel = channelRepository.loadById(channelId);
        if (channel != null) {
            channelRepository.join(userId, channelId);
        }
    }

    @Override
    public void leaveChannel(UUID userId, UUID channelId) {
        Channel channel = channelRepository.loadById(channelId);
        if (channel == null || !channel.isMember(userId)) {
            System.out.println("[Channel] 유효하지 않은 채널 혹은 사용자입니다.");
        } else {
            channelRepository.leave(userId, channelId);
        }
    }
}
