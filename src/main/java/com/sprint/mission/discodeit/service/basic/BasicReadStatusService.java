package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.ReadStatusDto;
import com.sprint.mission.discodeit.dto.request.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.request.ReadStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.channel.ChannelNotFoundException;
import com.sprint.mission.discodeit.exception.readStatus.ReadStatusAlreadyExistException;
import com.sprint.mission.discodeit.exception.readStatus.ReadStatusNotFoundException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.ReadStatusMapper;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ReadStatusService;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BasicReadStatusService implements ReadStatusService {

    private final ReadStatusRepository readStatusRepository;
    private final UserRepository userRepository;
    private final ChannelRepository channelRepository;
    private final ReadStatusMapper readStatusMapper;

    @Override
    @Transactional
    public ReadStatusDto create(ReadStatusCreateRequest request) {
        UUID userId = request.userId();
        UUID channelId = request.channelId();

        User user = userRepository.findById(userId)
            .orElseThrow(() -> UserNotFoundException.withId(userId));

        Channel channel = channelRepository.findById(channelId)
            .orElseThrow(() -> ChannelNotFoundException.withId(channelId));

        Optional<ReadStatus> existing = readStatusRepository
            .findByUserIdAndChannelId(userId, channelId);
        if (existing.isPresent()) {
            throw ReadStatusAlreadyExistException.withUserIdAndChannelId(userId, channelId);
        }

        ReadStatus readStatus = new ReadStatus(user, channel, request.lastReadAt());
        ReadStatus newReadStatus = readStatusRepository.save(readStatus);

        return readStatusMapper.toDto(newReadStatus);
    }

    @Override
    public Optional<ReadStatusDto> findById(UUID id) {
        return readStatusRepository.findById(id)
            .map(readStatusMapper::toDto);
    }

    @Override
    public List<ReadStatusDto> findAllByUserId(UUID userId) {
        return readStatusRepository.findAllByUserId(userId).stream()
            .map(readStatusMapper::toDto)
            .toList();
    }

    @Override
    @Transactional
    public ReadStatusDto update(UUID id, ReadStatusUpdateRequest request) {
        Instant newLastReadAt = request.newLastReadAt();
        ReadStatus readStatus = readStatusRepository.findById(id)
            .orElseThrow(() -> ReadStatusNotFoundException.withId(id));

        readStatus.updateLastReadAt(newLastReadAt);
        ReadStatus updatedReadStatus = readStatusRepository.save(readStatus);
        return readStatusMapper.toDto(updatedReadStatus);
    }


    @Override
    @Transactional
    public void delete(UUID id) {
        if (readStatusRepository.findById(id).isEmpty()) {
            throw ReadStatusNotFoundException.withId(id);
        }
        readStatusRepository.deleteById(id);
    }
}
