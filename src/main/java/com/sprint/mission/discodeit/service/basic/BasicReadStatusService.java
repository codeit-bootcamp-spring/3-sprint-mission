package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.Dto.readStatus.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.Dto.readStatus.ReadStatusCreateResponse;
import com.sprint.mission.discodeit.Dto.readStatus.ReadStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
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

    @Override
    public ResponseEntity<ReadStatusCreateResponse> create(ReadStatusCreateRequest request) {
        ReadStatus readStatus = readStatusRepository.createByUserId(request.userId(), request.channelId());
        ReadStatusCreateResponse readStatusCreateResponse = new ReadStatusCreateResponse(readStatus.getId(), readStatus.getUserId(), readStatus.getChannelId());
        return ResponseEntity.status(HttpStatus.CREATED).body(readStatusCreateResponse);
    }

    @Override
    public ReadStatus findById(UUID readStatusId) {
      return  Optional.ofNullable(readStatusRepository.findById(readStatusId)).orElseThrow(() -> new IllegalStateException("no read status to find"));
    }

    @Override
    public ResponseEntity<List<ReadStatus>> findAllByUserId(UUID userId) {
        List<ReadStatus> readStatusList = Optional.ofNullable(readStatusRepository.findAllByUserId(userId))
                .orElseThrow(() -> new IllegalStateException("userId로 찾을 수 없음: BasicReadStatusService.findAllByUserId"));
        return ResponseEntity.status(HttpStatus.OK)
                .body(readStatusList);
    }

    @Override
    public ResponseEntity<?> update(ReadStatusUpdateRequest request) {
        readStatusRepository.updateUpdatedTime(
                request.readStatusId(),
                request.newTime()
        );

        return ResponseEntity.status(HttpStatus.OK)
                .body(Map.of("message", "time updated" + request.newTime()));
    }

    @Override
    public void delete(UUID readStatusId) {
        Objects.requireNonNull(readStatusId, "no readStatusId to delete");
        readStatusRepository.deleteReadStatusById(readStatusId);
    }
}
