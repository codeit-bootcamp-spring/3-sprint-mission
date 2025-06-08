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
import java.time.Instant;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
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

        if (readStatusRepository.existsByUser_IdAndChannel_Id(userId, channelId)) {
            throw new IllegalArgumentException(
                "ReadStatus already exists for userId " + userId + " and channelId " + channelId);
        }

        User user = userRepository.findById(userId)
            .orElseThrow(() -> new NoSuchElementException("User with id " + userId + " not found"));

        Channel channel = channelRepository.findById(channelId)
            .orElseThrow(
                () -> new NoSuchElementException("Channel with id " + channelId + " not found"));

        ReadStatus readStatus = new ReadStatus(user, channel, request.lastReadAt());
        return readStatusMapper.toResponse(readStatusRepository.save(readStatus));
    }


    @Transactional(readOnly = true)
    @Override
    public ReadStatusResponse find(UUID readStatusId) {
        ReadStatus readStatus = readStatusRepository.findById(readStatusId)
            .orElseThrow(
                () -> new NoSuchElementException(
                    "ReadStatus with id " + readStatusId + " not found"));
        return readStatusMapper.toResponse(readStatus);
    }

    @Transactional(readOnly = true)
    @Override
    public List<ReadStatusResponse> findAllByUserId(UUID userId) {
        return readStatusRepository.findAllByUser_Id(userId).stream()
            .map(readStatusMapper::toResponse)
            .collect(Collectors.toList());
    }


    @Transactional
    @Override
    public ReadStatusResponse update(UUID readStatusId, ReadStatusUpdateRequest request) {
        Instant newLastReadAt = request.newLastReadAt();

        ReadStatus readStatus = readStatusRepository.findById(readStatusId)
            .orElseThrow(() -> new NoSuchElementException(
                "ReadStatus with id " + readStatusId + " not found"));

        readStatus.update(newLastReadAt); // 변경 감지
        return readStatusMapper.toResponse(readStatus); // save 불필요
    }

    @Transactional
    @Override
    public void delete(UUID readStatusId) {
        readStatusRepository.deleteById(readStatusId);
    }
}
