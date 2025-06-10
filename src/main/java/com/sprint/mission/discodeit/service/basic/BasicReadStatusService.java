package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.request.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.request.ReadStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ReadStatusService;
import java.time.Instant;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class BasicReadStatusService implements ReadStatusService {

    private final ReadStatusRepository readStatusRepository;
    private final UserRepository userRepository;
    private final ChannelRepository channelRepository;

    @Override
    @Transactional(
        rollbackFor = Exception.class,
        propagation = Propagation.REQUIRED,
        isolation = Isolation.READ_COMMITTED)
    public ReadStatus create(ReadStatusCreateRequest request) {
        UUID userId = request.userId();
        UUID channelId = request.channelId();

        User user = userRepository.findById(userId).orElseThrow(NoSuchElementException::new);
        Channel channel = channelRepository.findById(channelId)
            .orElseThrow(NoSuchElementException::new);

        if (!userRepository.existsById(userId)) {
            throw new NoSuchElementException("User with id " + userId + " does not exist");
        }
        if (!channelRepository.existsById(channelId)) {
            throw new NoSuchElementException("Channel with id " + channelId + " does not exist");
        }
        if (readStatusRepository.findAllByUserId(userId).stream()
            .anyMatch(readStatus -> readStatus.getChannel().getId().equals(channelId))) {
            throw new IllegalArgumentException(
                "ReadStatus with userId " + userId + " and channelId " + channelId
                    + " already exists");
        }

        Instant lastReadAt = request.lastReadAt();
        ReadStatus readStatus = new ReadStatus(user, channel, lastReadAt);
        return readStatusRepository.save(readStatus);
    }

    @Override
    @Transactional(readOnly = true,
        rollbackFor = Exception.class,
        propagation = Propagation.REQUIRED,
        isolation = Isolation.READ_COMMITTED)
    public ReadStatus find(UUID readStatusId) {
        return readStatusRepository.findById(readStatusId)
            .orElseThrow(() -> new NoSuchElementException(
                "ReadStatus with id " + readStatusId + " not found"));
    }

    @Override
    public List<ReadStatus> findAllByUserId(UUID userId) {
        return readStatusRepository.findAllByUserId(userId).stream()
            .toList();
    }

    @Override
    @Transactional(
        rollbackFor = Exception.class,
        propagation = Propagation.REQUIRED,
        isolation = Isolation.READ_COMMITTED)
    public ReadStatus update(UUID readStatusId, ReadStatusUpdateRequest request) {
        Instant newLastReadAt = request.newLastReadAt();
        ReadStatus readStatus = readStatusRepository.findById(readStatusId)
            .orElseThrow(() -> new NoSuchElementException(
                "ReadStatus with id " + readStatusId + " not found"));
        readStatus.update(newLastReadAt);
        return readStatusRepository.save(readStatus);
    }

    @Override
    @Transactional(
        rollbackFor = Exception.class,
        propagation = Propagation.REQUIRED,
        isolation = Isolation.READ_COMMITTED)
    public void delete(UUID readStatusId) {
        if (!readStatusRepository.existsById(readStatusId)) {
            throw new NoSuchElementException("ReadStatus with id " + readStatusId + " not found");
        }
        readStatusRepository.deleteById(readStatusId);
    }
}
