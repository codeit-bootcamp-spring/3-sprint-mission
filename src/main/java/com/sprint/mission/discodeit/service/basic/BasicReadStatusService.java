package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.ReadStatus.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.ReadStatus.ReadStatusUpdateRequest;
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

    /**
     * 주어진 생성 요청 DTO를 기반으로 ReadStatus 생성
     *
     * @param createRequestDTO ReadStatus 생성 요청 DTO
     * @return 생성된 ReadStatus
     * @throws NoSuchElementException 채널이나 유저가 존재하지 않는 경우
     * @throws IllegalStateException  같은 Channel,User와 관련된 ReadStatus가 이미 존재하는 경우
     */
    @Override
    public ReadStatus create(ReadStatusCreateRequest createRequestDTO) {
        UUID channelId = createRequestDTO.channelId();
        UUID userId = createRequestDTO.userId();

        // 관련된 Channel, User가 존재하지 않으면 예외처리
        if (!channelRepository.existsById(channelId)) {
            throw new NoSuchElementException("해당 ID의 채널이 존재하지 않습니다.");
        }
        if (!userRepository.existsById(userId)) {
            throw new NoSuchElementException("해당 ID의 유저가 존재하지 않습니다.");
        }
        // 같은 Channel, User와 관련된 객체가 이미 존재하면 예외처리
        if (readStatusRepository.existsByChannelIdAndUserId(channelId, userId)) {
            throw new IllegalStateException("이미 존재하는 ReadStatus입니다.");
        }

        Instant lastReadAt = createRequestDTO.lastReadAt();

        ReadStatus readStatus = ReadStatus.builder()
                .channelId(channelId)
                .userId(userId)
                .lastReadAt(lastReadAt)
                .build();

        readStatusRepository.save(readStatus);
        return readStatus;
    }

    /**
     * 주어진 유저ID로 유저별 ReadStatus 전체 조회
     *
     * @param userId 조회할 ReadStatus의 유저ID
     * @return 조회된 유저별 ReadStatus 리스트
     */
    @Override
    public List<ReadStatus> findAllByUserId(UUID userId) {
        return readStatusRepository.findByUserId(userId);
    }

    /**
     * 주어진 ID에 해당하는 ReadStatus 조회
     *
     * @param id 조회할 ReadStatus ID
     * @return 조회된 ReadStatus
     * @throws NoSuchElementException 해당 ID의 ReadStatus가 존재하지 않는 경우
     */
    @Override
    public ReadStatus find(UUID id) {
        return readStatusRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("해당 ID의 ReadStatus가 존재하지 않습니다."));
    }

    /**
     * 주어진 ID에 해당하는 ReadStatus 수정
     *
     * @param id               수정 대상 ReadStatus ID
     * @param updateRequestDTO 수정 요청 DTO
     * @return 수정된 ReadStatus
     * @throws NoSuchElementException 해당 ID의 ReadStatus가 존재하지 않는 경우
     */
    @Override
    public ReadStatus update(UUID id, ReadStatusUpdateRequest updateRequestDTO) {
        ReadStatus readStatus = readStatusRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("해당 ID의 ReadStatus를 찾을 수 없습니다."));

        Instant lastReadAt = updateRequestDTO.newLastReadAt();

        readStatus.update(lastReadAt);

        readStatusRepository.save(readStatus);
        return readStatus;
    }

    /**
     * 주어진 ID에 해당하는 ReadStatus 삭제
     *
     * @param id 삭제 대상 ReadStatus ID
     */
    @Override
    public void delete(UUID id) {
        readStatusRepository.deleteById(id);
    }
}
