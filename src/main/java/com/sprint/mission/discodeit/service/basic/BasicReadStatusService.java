package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.request.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.request.ReadStatusUpdateRequest;
import com.sprint.mission.discodeit.dto.response.ReadStatusResponse;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.mapper.ReadStatusMapper;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ReadStatusService;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Slf4j
public class BasicReadStatusService implements ReadStatusService {

    private final ReadStatusRepository readStatusRepository;
    private final UserRepository userRepository;
    private final ChannelRepository channelRepository;
    private final ReadStatusMapper readStatusMapper;

    @Transactional
    @Override
    public ReadStatusResponse create(ReadStatusCreateRequest request) {
        UUID userId = request.userId();
        UUID channelId = request.channelId();

        log.info("[BasicReadStatusService] Creating ReadStatus. [userId={}] [channelId={}]", userId,
            channelId);

        User user = userRepository.findById(userId)
            .orElseThrow(() -> {
                log.warn("[BasicReadStatusService] User not found. [userId={}]", userId);
                return new NoSuchElementException("User with id " + userId + " does not exist");
            });

        Channel channel = channelRepository.findById(channelId)
            .orElseThrow(() -> {
                log.warn("[BasicReadStatusService] Channel not found. [channelId={}]", channelId);
                return new NoSuchElementException(
                    "Channel with id " + channelId + " does not exist");
            });

        if (readStatusRepository.existsByUserIdAndChannelId(userId, channelId)) {
            log.warn(
                "[BasicReadStatusService] ReadStatus already exists. [userId={}] [channelId={}]",
                userId, channelId);
            throw new IllegalArgumentException(
                "ReadStatus with userId " + userId + " and channelId " + channelId
                    + " already exists");
        }

        ReadStatus readStatus = new ReadStatus(user, channel, request.lastReadAt());
        readStatusRepository.save(readStatus);

        log.debug("[BasicReadStatusService] ReadStatus created. [id={}]", readStatus.getId());
        return readStatusMapper.toResponse(readStatus);
    }

    @Transactional(readOnly = true)
    @Override
    public ReadStatusResponse find(UUID readStatusId) {
        log.info("[BasicReadStatusService] Finding ReadStatus. [id={}]", readStatusId);

        return readStatusRepository.findById(readStatusId)
            .map(readStatus -> {
                log.debug("[BasicReadStatusService] ReadStatus found. [id={}]", readStatusId);
                return readStatusMapper.toResponse(readStatus);
            })
            .orElseThrow(() -> {
                log.warn("[BasicReadStatusService] ReadStatus not found. [id={}]", readStatusId);
                return new NoSuchElementException(
                    "ReadStatus with id " + readStatusId + " not found");
            });
    }

    @Transactional(readOnly = true)
    @Override
    public List<ReadStatusResponse> findAllByUserId(UUID userId) {
        log.info("[BasicReadStatusService] Finding ReadStatuses by user. [userId={}]", userId);

        List<ReadStatusResponse> result = readStatusRepository.findAllByUserId(userId).stream()
            .map(readStatusMapper::toResponse)
            .toList();

        log.debug("[BasicReadStatusService] ReadStatuses found. [count={}] [userId={}]",
            result.size(), userId);
        return result;
    }

    @Transactional
    @Override
    public ReadStatusResponse update(UUID readStatusId, ReadStatusUpdateRequest request) {
        log.info("[BasicReadStatusService] Updating ReadStatus. [id={}]", readStatusId);

        ReadStatus readStatus = readStatusRepository.findById(readStatusId)
            .orElseThrow(() -> {
                log.warn("[BasicReadStatusService] ReadStatus not found for update. [id={}]",
                    readStatusId);
                return new NoSuchElementException(
                    "ReadStatus with id " + readStatusId + " not found");
            });

        readStatus.update(request.newLastReadAt());

        log.debug("[BasicReadStatusService] ReadStatus updated. [id={}]", readStatusId);
        return readStatusMapper.toResponse(readStatus);
    }

    @Transactional
    @Override
    public void delete(UUID readStatusId) {
        log.info("[BasicReadStatusService] Deleting ReadStatus. [id={}]", readStatusId);

        if (!readStatusRepository.existsById(readStatusId)) {
            log.warn("[BasicReadStatusService] Cannot delete - ReadStatus not found. [id={}]",
                readStatusId);
            throw new NoSuchElementException("ReadStatus with id " + readStatusId + " not found");
        }

        readStatusRepository.deleteById(readStatusId);
        log.debug("[BasicReadStatusService] ReadStatus deleted. [id={}]", readStatusId);
    }
}
