package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.request.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.request.ReadStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ReadStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BasicReadStatusService implements ReadStatusService {
    private final ReadStatusRepository readStatusRepository;
    private final UserRepository userRepository;
    private final ChannelRepository channelRepository;

    @Override
    public ReadStatus create(ReadStatusCreateRequest readStatusCreateRequest) {
        UUID userId = readStatusCreateRequest.userId();
        UUID channelId = readStatusCreateRequest.channelId();

        if (userRepository.loadById(userId) == null) {
            throw new IllegalArgumentException("[ReadStatus] 존재하지 않은 사용자입니다. (" + userId + ")");
        }

        if (channelRepository.loadById(channelId) == null) {
            throw new IllegalArgumentException("[ReadStatus] 존재하지 채널 사용자입니다. (" + channelId + ")");
        }

        if (readStatusRepository
                .loadAllByUserId(userId)
                .stream()
                .anyMatch(readStatus -> readStatus.getChannelId().equals(channelId))) {
            throw new IllegalArgumentException("[ReadStatus] 이미 관련된 객체가 존재합니다. (user=" + userId + ", channel=" + channelId + ")");
        }

        Instant lastReadAt = readStatusCreateRequest.lastReadAt();
        ReadStatus readStatus = ReadStatus.of(userId, channelId, lastReadAt);
        return readStatusRepository.save(readStatus);
    }

    @Override
    public List<ReadStatus> findAllByUserId(UUID userId) {
        return readStatusRepository.loadAllByUserId(userId);
    }

    @Override
    public ReadStatus update(UUID readStatusId, ReadStatusUpdateRequest request) {
        Instant newLastReadAt = request.newLastReadAt();
        ReadStatus readStatus = readStatusRepository.loadById(readStatusId)
                .orElseThrow(
                        () -> new NoSuchElementException("[ReadStatus] 유효하지 않은 읽음 상태 (readStatusId=" + readStatusId + ")"));
        readStatus.update(newLastReadAt);
        return readStatusRepository.save(readStatus);
    }

    @Override
    public void delete(UUID readStatusId) {
        readStatusRepository.deleteByUserId(readStatusId);
    }
}
