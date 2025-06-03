package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.ReadStatusDto;
import com.sprint.mission.discodeit.dto.request.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.request.ReadStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.mapper.ReadStatusMapper;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ReadStatusService;
import jakarta.transaction.Transactional;
import java.time.Instant;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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
            .orElseThrow(() -> new IllegalArgumentException("해당 유저가 존재하지 않습니다."));

        Channel channel = channelRepository.findById(request.channelId())
            .orElseThrow(() -> new IllegalArgumentException("해당 채널이 존재하지 않습니다."));

        Optional<ReadStatus> existing = readStatusRepository
            .findByUserIdAndChannelId(request.userId(), request.channelId());
        if (existing.isPresent()) {
            throw new IllegalArgumentException("해당 유저와 채널의 ReadStatus는 이미 존재합니다.");
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
            .orElseThrow(() -> new NoSuchElementException("해당 ReadStatus가 존재하지 않습니다."));

        readStatus.updateLastReadAt(newLastReadAt);
        ReadStatus updatedReadStatus = readStatusRepository.save(readStatus);
        return readStatusMapper.toDto(updatedReadStatus);
    }


    @Override
    @Transactional
    public void delete(UUID id) {
        if (readStatusRepository.findById(id).isEmpty()) {
            throw new IllegalArgumentException("삭제할 ReadStatus가 존재하지 않습니다.");
        }
        readStatusRepository.deleteById(id);
    }
}
