package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.Dto.channel.ChannelCreateResponse;
import com.sprint.mission.discodeit.Dto.readStatus.*;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ReadStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

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
public class BasicReadStatusService implements ReadStatusService {

    private final ReadStatusRepository readStatusRepository;
    private final UserRepository userRepository;
    private final ChannelRepository channelRepository;


    @Override
    public ResponseEntity<?> findAllByUserId(UUID userId) {
        List<ReadStatus> readStatusList = Optional.ofNullable(readStatusRepository.findAllByUserId(userId))
                .orElseThrow(() -> new IllegalStateException("userId로 찾을 수 없음: BasicReadStatusService.findAllByUserId"));

        List<FindReadStatusesResponse> responses = new ArrayList<>();
        for (ReadStatus readStatus : readStatusList) {
            responses.add(new FindReadStatusesResponse(
                    readStatus.getId(),
                    readStatus.getCreatedAt(),
                    readStatus.getUpdatedAt(),
                    readStatus.getUserId(),
                    readStatus.getChannelId(),
                    readStatus.getLastReadAt()
            ));
        }
        return ResponseEntity.status(200).body(responses);
    }

    @Override
    public ResponseEntity<?> create(ReadStatusCreateRequest request) {
        UUID userId = request.userId();
        UUID channelId = request.channelId();
        // user 검증
        if (userRepository.findUserById(userId) == null) {
            return ResponseEntity.status(404).body("user with id " + userId + " not found");
        }
        // channel 검증
        if (channelRepository.findChannelById(channelId) == null) {
            return ResponseEntity.status(404).body("channel with id " + channelId + " not found");
        }
        // ReadStatus 중복 방지
        List<ReadStatus> readStatusesByChannelId = readStatusRepository.findReadStatusesByChannelId(request.channelId());
        for (ReadStatus readStatus : readStatusesByChannelId) {
            if (readStatus.getUserId().equals(request.userId())) {
                return ResponseEntity.status(400).body("readStatus with userId " + request.userId() + " and channelId " + request.channelId() + " already exists");
            }
        }
        ReadStatus readStatus = readStatusRepository.createByUserId(request.userId(), request.channelId(), request.lastReadAt());
        ReadStatusCreateResponse readStatusCreateResponse = new ReadStatusCreateResponse(
                readStatus.getId(),
                readStatus.getCreatedAt(),
                readStatus.getUpdatedAt(),
                readStatus.getUserId(),
                readStatus.getChannelId(),
                readStatus.getLastReadAt()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(readStatusCreateResponse);
    }


    @Override
    public ResponseEntity<?> update(UUID readStatusId, ReadStatusUpdateRequest request) {
        readStatusRepository.updateUpdatedTime(readStatusId, request.newLastReadAt());
        ReadStatus readStatus = readStatusRepository.findById(readStatusId);
        if (readStatus == null) {
            return ResponseEntity.status(404).body("readStatus with id " + readStatusId + " not found");
        }

        UpdateReadStatusResponse response = new UpdateReadStatusResponse(
                readStatus.getId(),
                readStatus.getCreatedAt(),
                readStatus.getUpdatedAt(),
                readStatus.getUserId(),
                readStatus.getChannelId(),
                readStatus.getLastReadAt()
        );

        return ResponseEntity.status(HttpStatus.OK)
                .body(response);
    }

    // --------------------------------------------------------------------------------------------------------------

    @Override
    public ReadStatus findById(UUID readStatusId) {
        return Optional.ofNullable(readStatusRepository.findById(readStatusId)).orElseThrow(() -> new IllegalStateException("no read status to find"));
    }

    @Override
    public void delete(UUID readStatusId) {
        Objects.requireNonNull(readStatusId, "no readStatusId to delete");
        readStatusRepository.deleteReadStatusById(readStatusId);
    }
}
