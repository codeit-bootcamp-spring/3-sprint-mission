package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.data.ReadStatusDto;
import com.sprint.mission.discodeit.dto.request.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.request.ReadStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.channel.ChannelNotFoundException;
import com.sprint.mission.discodeit.exception.readStatus.DuplicateReadStatusException;
import com.sprint.mission.discodeit.exception.readStatus.ReadStatusNotFoundException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.ReadStatusMapper;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ReadStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class BasicReadStatusService implements ReadStatusService {

    private final ReadStatusRepository readStatusRepository;
    private final UserRepository userRepository;
    private final ChannelRepository channelRepository;
    private final ReadStatusMapper readStatusMapper;

    @Transactional
    @Override
    public ReadStatusDto create(ReadStatusCreateRequest request) {
        User user = userRepository.findById(request.userId()).orElseThrow(() -> new UserNotFoundException(request.userId()));

        Channel channel = channelRepository.findById(request.channelId()).orElseThrow(() -> new ChannelNotFoundException(request.channelId()));


        if (readStatusRepository.findAllByUserId(user.getId()).stream()
                .anyMatch(readStatus -> readStatus.getChannel().getId().equals(channel.getId()))) {
            throw new DuplicateReadStatusException(user.getId(), channel.getId());
        }

        ReadStatus readStatus = new ReadStatus(user, channel, request.lastReadAt());
        return readStatusMapper.toDto(readStatusRepository.save(readStatus));
    }

    @Transactional(readOnly = true)
    @Override
    public ReadStatusDto find(UUID readStatusId) {
        return readStatusMapper.toDto(readStatusRepository.findById(readStatusId).orElseThrow(() -> new ReadStatusNotFoundException(readStatusId))
        );
    }

    @Transactional(readOnly = true)
    @Override
    public List<ReadStatusDto> findAllByUserId(UUID userId) {
        return readStatusRepository.findAllByUserId(userId).stream().map(readStatusMapper::toDto).toList();
    }

    @Transactional
    @Override
    public ReadStatusDto update(UUID readStatusId, ReadStatusUpdateRequest request) {
        Instant newLastReadAt = request.newLastReadAt();

        ReadStatus readStatus = readStatusRepository.findById(readStatusId).orElseThrow(() -> new ReadStatusNotFoundException(readStatusId));

        readStatus.update(newLastReadAt, request.newNotificationEnabled());



        return readStatusMapper.toDto(readStatusRepository.save(readStatus));
    }

    @Transactional
    @Override
    public void delete(UUID readStatusId) {

        if (!readStatusRepository.existsById(readStatusId)) {
            throw new ReadStatusNotFoundException(readStatusId);
        }

        readStatusRepository.deleteById(readStatusId);
    }
}
