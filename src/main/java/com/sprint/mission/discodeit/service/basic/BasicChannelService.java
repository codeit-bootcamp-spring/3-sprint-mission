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
import java.time.Instant;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    @Transactional(readOnly = true)
    public ChannelDto find(ChannelFindRequest request) {
        Channel channel = channelRepository.findById(request.id())
                .orElseThrow(() -> new NoSuchElementException("해당 id를 가진 채널은 없습니다."));

        if (channel.getType() == ChannelType.PRIVATE) {
            List<ReadStatus> statuses = readStatusRepository.findAllByChannelId(channel.getId());
            statuses.forEach(rs -> rs.getUser().getUsername());
        }

        messageRepository.findAllByChannelIdOrderByCreatedAtDesc(
                        channel.getId(),
                        PageRequest.of(0, 1, Sort.by("createdAt").descending())
                ).stream()
                .findFirst()
                .ifPresent(m -> channel.setCreatedAt(m.getCreatedAt()));

        return channelMapper.toDto(channel);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ChannelDto> findAllByUserId(UUID userId) {
        List<Channel> channels = channelRepository.findAll();

        List<UUID> joinedChannelIds = readStatusRepository.findAllByUserId(userId)
                .stream()
                .map(rs -> rs.getChannel().getId())
                .toList();

        return channels.stream()
                .filter(ch -> ch.getType() == ChannelType.PUBLIC || joinedChannelIds.contains(ch.getId()))
                .peek(ch -> {
                    messageRepository.findAllByChannelIdOrderByCreatedAtDesc(
                                    ch.getId(),
                                    PageRequest.of(0, 1, Sort.by("createdAt").descending())
                            ).stream()
                            .findFirst()
                            .ifPresent(m -> ch.setCreatedAt(m.getCreatedAt()));

                    if (ch.getType() == ChannelType.PRIVATE) {
                        readStatusRepository.findAllByChannelId(ch.getId())
                                .forEach(rs -> rs.getUser().getUsername());
                    }
                })
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