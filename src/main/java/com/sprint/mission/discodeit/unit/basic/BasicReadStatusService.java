package com.sprint.mission.discodeit.unit.basic;

import com.sprint.mission.discodeit.dto.readStatus.*;
import com.sprint.mission.discodeit.dto.readStatus.request.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.readStatus.request.ReadStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.mapper.advanced.ReadStatusMapper;
import com.sprint.mission.discodeit.repository.jpa.JpaChannelRepository;
import com.sprint.mission.discodeit.repository.jpa.JpaReadStatusRepository;
import com.sprint.mission.discodeit.repository.jpa.JpaUserRepository;
import com.sprint.mission.discodeit.unit.ReadStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * packageName    : com.sprint.mission.discodeit.service.basic
 * fileName       : BasicReadStatusService
 * author         : doungukkim
 * date           : 2025. 4. 28.
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2025. 4. 28.        doungukkim       최초 생성
 */
@Service("basicReadStatusService")
@RequiredArgsConstructor
@Transactional
public class BasicReadStatusService implements ReadStatusService {

    private final JpaReadStatusRepository readStatusRepository;
    private final JpaUserRepository userRepository;
    private final JpaChannelRepository channelRepository;
    private final ReadStatusMapper readStatusMapper;


    @Override
    public List<JpaReadStatusResponse> findAllByUserId(UUID userId) {
        List<ReadStatus> readStatusList = Optional.ofNullable(readStatusRepository.findAllByUserId(userId))
                .orElseThrow(() -> new IllegalStateException("userId로 찾을 수 없음: BasicReadStatusService.findAllByUserId"));


        List<JpaReadStatusResponse> responses = new ArrayList<>();
        for (ReadStatus readStatus : readStatusList) {
            responses.add(
                    readStatusMapper.toDto(readStatus)
            );

        }
        return responses;
    }

    @Override
    public JpaReadStatusResponse create(ReadStatusCreateRequest request) {
        UUID userId = request.userId();
        UUID channelId = request.channelId();

        User user = userRepository.findById(userId).orElseThrow(() -> new NoSuchElementException("user with id " + userId + " not found"));
        Channel channel = channelRepository.findById(channelId).orElseThrow(() -> new NoSuchElementException("channel with id " + channelId + " not found"));

        // ReadStatus 중복 방지
        if (readStatusRepository.existsByUserAndChannel(user, channel)) {
            throw new IllegalArgumentException("readStatus with userId " + request.userId() + " and channelId " + request.channelId() + " already exists");
        }

        ReadStatus readStatus = ReadStatus.builder()
                .user(user)
                .channel(channel)
                .lastReadAt(request.lastReadAt())
                .build();
        readStatusRepository.save(readStatus);

        JpaReadStatusResponse response = readStatusMapper.toDto(readStatus);
        return response;
    }


    @Override
    public JpaReadStatusResponse update(UUID readStatusId, ReadStatusUpdateRequest request) {

        ReadStatus readStatus = readStatusRepository.findById(readStatusId).orElseThrow(() -> new NoSuchElementException("readStatus with id " + readStatusId + " not found"));
        readStatus.setLastReadAt(request.newLastReadAt());

        JpaReadStatusResponse response = new JpaReadStatusResponse(
                readStatus.getId(),
                readStatus.getUser().getId(),
                readStatus.getChannel().getId(),
                readStatus.getLastReadAt()
        );

        return response;
    }
}
