package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.ReadStatusDto;
import com.sprint.mission.discodeit.dto.request.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.request.ReadStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.DiscodeitException;
import com.sprint.mission.discodeit.exception.ErrorCode;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.ReadStatusMapper;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ReadStatusService;
import java.time.Instant;
import java.util.List;
import java.util.Map;
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
        User user = userRepository.findById(request.userId())
            .orElseThrow(() -> new UserNotFoundException(
                ErrorCode.USER_NOT_FOUND,
                Map.of("userId", request.userId())
            ));

        Channel channel = channelRepository.findById(request.channelId())
            .orElseThrow(() -> new DiscodeitException(
                ErrorCode.CHANNEL_NOT_FOUND,
                Map.of("channelId", request.channelId())
            ));

        Optional<ReadStatus> existing = readStatusRepository
            .findByUserIdAndChannelId(request.userId(), request.channelId());
        if (existing.isPresent()) {
            throw new DiscodeitException(
                ErrorCode.READ_STATUS_ALREADY_EXISTS,
                Map.of(
                    "userId", request.userId(),
                    "channelId", request.channelId()
                )
            );
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
            .orElseThrow(() -> new DiscodeitException(
                ErrorCode.READ_STATUS_NOT_FOUND,
                Map.of("readStatusId", id)
            ));

        readStatus.updateLastReadAt(newLastReadAt);
        ReadStatus updatedReadStatus = readStatusRepository.save(readStatus);
        return readStatusMapper.toDto(updatedReadStatus);
    }


    @Override
    @Transactional
    public void delete(UUID id) {
        if (readStatusRepository.findById(id).isEmpty()) {
            throw new DiscodeitException(
                ErrorCode.READ_STATUS_NOT_FOUND,
                Map.of("readStatusId", id)
            );
        }
        readStatusRepository.deleteById(id);
    }
}
