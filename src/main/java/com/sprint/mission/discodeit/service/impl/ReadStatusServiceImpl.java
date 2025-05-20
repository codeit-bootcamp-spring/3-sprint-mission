package com.sprint.mission.discodeit.service.impl;

import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.service.ReadStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ReadStatusServiceImpl implements ReadStatusService {
    private final ReadStatusRepository readStatusRepository;

    @Override
    public ReadStatus markAsRead(UUID messageId, UUID userId) {
        Optional<ReadStatus> existing = readStatusRepository.findByMessageIdAndUserId(messageId, userId);
        if (existing.isPresent()) {
            return existing.get();
        }

        ReadStatus newStatus = new ReadStatus(
                UUID.randomUUID(),
                messageId,
                userId,
                Instant.now()
        );
        return readStatusRepository.save(newStatus);
    }

    @Override
    public Optional<ReadStatus> getReadStatus(UUID messageId, UUID userId) {
        return readStatusRepository.findByMessageIdAndUserId(messageId, userId);
    }

    @Override
    public List<ReadStatus> getAllReadStatuses() {
        return readStatusRepository.findAll();
    }

    @Override
    public List<ReadStatus> getByMessageId(UUID messageId) {
        return readStatusRepository.findByMessageId(messageId);
    }

    @Override
    public List<ReadStatus> getByUserId(UUID userId) {
        return readStatusRepository.findByUserId(userId);
    }
}
