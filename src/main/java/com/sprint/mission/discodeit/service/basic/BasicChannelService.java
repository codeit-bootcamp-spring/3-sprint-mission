package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.ChannelDto;
import com.sprint.mission.discodeit.dto.request.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.mapper.ChannelMapper;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BasicChannelService implements ChannelService {

    private final ChannelRepository channelRepository;
    private final MessageRepository messageRepository;
    private final ReadStatusRepository readStatusRepository;
    private final UserRepository userRepository;
    private final ChannelMapper channelMapper;

    @Override
    @Transactional
    public ChannelDto create(PublicChannelCreateRequest request) {
        String name = request.name();
        String description = request.description();

        Channel channel = new Channel(ChannelType.PUBLIC, name, description);
        Channel newChannel = channelRepository.save(channel);
        return channelMapper.toDto(newChannel);
    }

    @Override
    @Transactional
    public ChannelDto create(PrivateChannelCreateRequest request) {
        Channel channel = new Channel(ChannelType.PRIVATE, null, null);
        Channel createdChannel = channelRepository.save(channel);

        for (UUID userId : request.participantIds()) {
            User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
            ReadStatus readStatus = new ReadStatus(user, createdChannel,
                createdChannel.getCreatedAt());
            readStatusRepository.save(readStatus);
        }

        Channel newChannel = channelRepository.save(channel);
        return channelMapper.toDto(newChannel);
    }

    @Override
    public Optional<ChannelDto> find(UUID channelId) {
        return channelRepository.findById(channelId)
            .map(channelMapper::toDto);
    }

    @Override
    public List<ChannelDto> findAllByUserId(UUID userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        List<UUID> mySubscribedChannelIds = readStatusRepository.findAllByUserId(userId).stream()
            .map(readStatus -> readStatus.getChannel().getId())
            .toList();

        return channelRepository.findAll().stream()
            .filter(channel -> channel.getType().equals(ChannelType.PUBLIC)
                || mySubscribedChannelIds.contains(channel.getId())
            )
            .map(channelMapper::toDto)
            .toList();
    }

    @Override
    @Transactional
    public ChannelDto update(UUID channelId, PublicChannelUpdateRequest request) {
        String newName = request.newName();
        String newDescription = request.newDescription();

        Channel channel = channelRepository.findById(channelId)
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 채널입니다."));

        if (channel.getType() == ChannelType.PRIVATE) {
            throw new IllegalArgumentException("PRIVATE 채널은 수정이 불가능합니다.");
        }

        channel.update(newName, newDescription);
        Channel updatedChannel = channelRepository.save(channel);
        return channelMapper.toDto(updatedChannel);
    }

    @Override
    @Transactional
    public void deleteChannel(UUID channelId) {

        Channel channel = channelRepository.findById(channelId)
            .orElseThrow(() -> new IllegalArgumentException("채널이 존재하지 않습니다."));

        /*List<Message> messages = messageRepository.findByChannelId(channelId);
        messageRepository.deleteAll(messages);

        List<ReadStatus> readStatuses = readStatusRepository.findByChannelId(channelId);
        readStatusRepository.deleteAll(readStatuses);*/

        messageRepository.deleteByChannelId(channelId);
        readStatusRepository.deleteByChannelId(channelId);

        channelRepository.deleteById(channelId);
    }
}