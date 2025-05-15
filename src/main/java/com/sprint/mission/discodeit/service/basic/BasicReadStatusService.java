package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.readStatus.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.readStatus.ReadStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ReadStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BasicReadStatusService implements ReadStatusService {

    private final ReadStatusRepository readStatusRepository;
    private final UserRepository userRepository;
    private final ChannelRepository channelRepository;

    @Override
    public ReadStatus createReadStatus(ReadStatusCreateRequest request) {
        userRepository.findById(request.userId())
                .orElseThrow(() -> new NoSuchElementException("User not found with id: " + request.userId()));
        channelRepository.findById(request.channelId())
                .orElseThrow(() -> new NoSuchElementException("Channel not found with id: " + request.channelId()));

        Optional<ReadStatus> existingStatus = readStatusRepository.findByUserIdAndChannelId(request.userId(), request.channelId());
        if (existingStatus.isPresent()) {
            throw new IllegalStateException("ReadStatus already exists for user " + request.userId() + " and channel " + request.channelId());
        }

        ReadStatus newReadStatus = new ReadStatus(
                request.userId(),
                request.channelId(),
                request.lastReadAt()
        );
        return readStatusRepository.save(newReadStatus);
    }

    @Override
    public ReadStatus findReadStatusById(UUID readStatusId) {
        return readStatusRepository.findById(readStatusId)
                .orElseThrow(() -> new NoSuchElementException("ReadStatus not found with id: " + readStatusId));
    }

    @Override
    public List<ReadStatus> findAllReadStatusesByUserId(UUID userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("User not found with id: " + userId + ". Cannot find read statuses."));
        return readStatusRepository.findAllByUserId(userId);
    }

    @Override
    public ReadStatus updateReadStatus(UUID readStatusId, ReadStatusUpdateRequest request) {
        ReadStatus readStatus = findReadStatusById(readStatusId);
        readStatus.update(request.newLastReadAt());
        return readStatusRepository.save(readStatus);
    }

    @Override
    public void deleteReadStatus(UUID readStatusId) {
        if (!readStatusRepository.existsById(readStatusId)) {
            throw new NoSuchElementException("ReadStatus not found with id: " + readStatusId + ". Cannot delete.");
        }
        readStatusRepository.deleteById(readStatusId);
    }
}
