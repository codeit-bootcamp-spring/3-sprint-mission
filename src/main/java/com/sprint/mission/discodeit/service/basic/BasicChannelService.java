package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.Channel.ChannelFindRequest;
import com.sprint.mission.discodeit.dto.Channel.ChannelUpdateRequest;
import com.sprint.mission.discodeit.dto.Channel.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.Channel.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.ChannelDto;
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
import java.time.Instant;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Transactional
public class BasicChannelService implements ChannelService {

    private final ChannelRepository channelRepository;
    private final UserRepository userRepository;
    private final ReadStatusRepository readStatusRepository;
    private final MessageRepository messageRepository;
    private final ChannelMapper channelMapper;

    @Override
    public ChannelDto create(PublicChannelCreateRequest request) {
        Channel channel = new Channel(ChannelType.PUBLIC, request.name(), request.description());
        Channel saved = channelRepository.save(channel);
        return channelMapper.toDto(saved);
    }

    @Override
    public ChannelDto create(PrivateChannelCreateRequest request) {
        Channel channel = new Channel(ChannelType.PRIVATE, null, null);
        Channel createChannel = channelRepository.save(channel);

        request.participantIds().forEach(userId -> {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new NoSuchElementException("해당 유저가 존재하지 않습니다."));
            ReadStatus readStatus = new ReadStatus(user, createChannel, Instant.EPOCH);
            readStatusRepository.save(readStatus);
        });

        return channelMapper.toDto(createChannel);
    }

    @Override
    public ChannelDto find(ChannelFindRequest request) {
        Channel channel = channelRepository.findById(request.id())
                .orElseThrow(() -> new NoSuchElementException("해당 id를 가진 채널은 없습니다."));
        return channelMapper.toDto(channel);
    }

    @Override
    public List<ChannelDto> findAllByUserId(UUID userId) {
        List<Channel> channels = channelRepository.findAll();
        return channels.stream()
                .filter(channel -> channel.getType() == ChannelType.PUBLIC
                        || readStatusRepository.findAllByUserId(userId).stream()
                        .map(ReadStatus::getChannel)
                        .map(Channel::getId)
                        .toList()
                        .contains(channel.getId()))
                .map(channelMapper::toDto)
                .toList();
    }

    @Override
    public ChannelDto update(UUID id, ChannelUpdateRequest request) {
        Channel channel = channelRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("해당 id를 가진 채널은 없습니다."));
        if (channel.getType().equals(ChannelType.PRIVATE)) {
            throw new RuntimeException("Private 채널은 수정할 수 없습니다.");
        }
        channel.update(request.newName(), request.newDescription());

        Channel saved = channelRepository.save(channel);
        return channelMapper.toDto(saved);
    }

    @Override
    public void delete(UUID channelId) {
        if (!channelRepository.existsById(channelId)) {
            throw new NoSuchElementException("해당 id를 가진 채널은 없습니다.");
        }
        channelRepository.deleteById(channelId);
        readStatusRepository.deleteAllByChannelId(channelId);
        messageRepository.deleteAllByChannelId(channelId);
    }
}