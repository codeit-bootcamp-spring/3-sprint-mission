package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.UserStatus.UserStatusCreateRequest;
import com.sprint.mission.discodeit.dto.UserStatus.UserStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.UserStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BasicUserStatusService implements UserStatusService {

    private final UserStatusRepository userStatusRepository;
    private final UserRepository userRepository;

    /**
     * 주어진 생성 요청 DTO를 기반으로 UserStatus 생성
     *
     * @param createRequestDTO 생성 요청 DTO
     * @return 생성된 UserStatus
     * @throws NoSuchElementException 해당 ID의 유저가 존재하지 않는 경우
     * @throws IllegalStateException  같은 유저ID의 UserStatus가 이미 존재하는 경우
     */
    @Override
    public UserStatus create(UserStatusCreateRequest createRequestDTO) {
        UUID userId = createRequestDTO.userId();

        // 관련된 User가 존재하지 않으면 예외처리
        if (!userRepository.existsById(userId)) {
            throw new NoSuchElementException("해당 ID의 유저가 존재하지 않습니다.");
        }

        // 같은 User와 관련된 객체가 이미 존재하면 예외처리
        if (userStatusRepository.findByUserId(userId).isPresent()) {
            throw new IllegalStateException("이미 존재하는 UserStatus입니다.");
        }

        Instant lastActiveAt = createRequestDTO.lastActiveAt();

        UserStatus userStatus = UserStatus.builder()
                .userId(userId)
                .lastActiveAt(lastActiveAt)
                .build();

        return userStatus;
    }

    /**
     * 레포지토리로부터 읽어온 UserStatus 데이터 전체 조회
     *
     * @return 조회된 UserStatus 데이터
     */
    @Override
    public List<UserStatus> findAll() {
        return userStatusRepository.findAll();
    }

    /**
     * 주어진 ID에 해당하는 UserStatus 조회
     *
     * @param id 조회 대상 UserStatus ID
     * @return 조회된 UserStatus
     * @throws NoSuchElementException 해당 ID의 UserStatus가 존재하지 않는 경우
     */
    @Override
    public UserStatus find(UUID id) {
        return userStatusRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("해당 ID의 UserStatus가 존재하지 않습니다."));
    }

    /**
     * 주어진 유저ID에 해당하는 UserStatus 조회
     *
     * @param userId 조회할 UserStatus의 유저ID
     * @return 조회된 UserStatus
     * @throws NoSuchElementException 해당 유저ID의 UserStatus가 존재하지 않는 경우
     */
    @Override
    public UserStatus findByUserId(UUID userId) {
        return userStatusRepository.findByUserId(userId)
                .orElseThrow(() -> new NoSuchElementException("해당 유저ID의 UserStatus가 존재하지 않습니다."));
    }

    /**
     * 주어진 ID에 해당하는 UserStatus를 수정 요청 DTO 값으로 수정
     *
     * @param userStatusId     수정 대상 UserStatus ID
     * @param updateRequestDTO 수정 요청 DTO
     * @return 수정된 UserStatus
     * @throws NoSuchElementException 해당 ID의 UserStatus가 존재하지 않는 경우
     */
    @Override
    public UserStatus update(UUID userStatusId, UserStatusUpdateRequest updateRequestDTO) {
        UserStatus userStatus = userStatusRepository.findById(userStatusId)
                .orElseThrow(() -> new NoSuchElementException("해당 ID의 UserStatus가 존재하지 않습니다."));

        Instant lastActiveAt = updateRequestDTO.newLastActiveAt();

        userStatus.update(lastActiveAt);

        userStatusRepository.save(userStatus);
        return userStatus;
    }

    /**
     * 주어진 유저ID에 해당하는 UserStatus를 수정 요청 DTO 값으로 수정
     *
     * @param userId           수정 대상 UserStatus의 유저ID
     * @param updateRequestDTO 수정 요청 DTO
     * @return 수정된 UserStatus
     * @throws NoSuchElementException 해당 유저ID의 UserStatus가 존재하지 않는 경우
     */
    @Override
    public UserStatus updateByUserId(UUID userId, UserStatusUpdateRequest updateRequestDTO) {
        UserStatus userStatus = userStatusRepository.findByUserId(userId)
                .orElseThrow(() -> new NoSuchElementException("해당 유저ID의 UserStatus가 존재하지 않습니다."));

        Instant lastActiveAt = updateRequestDTO.newLastActiveAt();

        userStatus.update(lastActiveAt);

        userStatusRepository.save(userStatus);
        return userStatus;
    }

    /**
     * 주어진 ID에 해당하는 UserStatus 삭제
     *
     * @param id 삭제 대상 UserStatus ID
     */
    @Override
    public void delete(UUID id) {
        userStatusRepository.deleteById(id);
    }
}
