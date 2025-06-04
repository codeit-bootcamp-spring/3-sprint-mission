package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.annotation.Logging;
import com.sprint.mission.discodeit.dto.UserStatus.UserStatusCreateRequest;
import com.sprint.mission.discodeit.dto.UserStatus.UserStatusDto;
import com.sprint.mission.discodeit.dto.UserStatus.UserStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.mapper.UserStatusMapper;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.UserStatusService;
import java.time.Instant;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Logging
@RequiredArgsConstructor
public class BasicUserStatusService implements UserStatusService {

    private final UserStatusRepository userStatusRepository;
    private final UserRepository userRepository;

    private final UserStatusMapper userStatusMapper;

    /**
     * 주어진 생성 요청 DTO를 기반으로 UserStatus 생성
     *
     * @param createRequest 생성 요청 DTO
     * @return 생성된 UserStatus
     */
    @Override
    @Transactional
    public UserStatusDto create(UserStatusCreateRequest createRequest) {

        UUID userId = createRequest.userId();

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("존재하지 않는 유저입니다."));

        // 같은 User와 관련된 객체가 이미 존재하면 예외처리
        if (userStatusRepository.findByUser_Id(userId).isPresent()) {
            throw new IllegalStateException("이미 존재하는 UserStatus입니다.");
        }

        UserStatus userStatus = UserStatus.builder()
                .user(user)
                .lastActiveAt(Instant.now())
                .build();

        userStatusRepository.save(userStatus);
        return userStatusMapper.toDto(userStatus);
    }

    /**
     * UserStatus 전체 조회
     *
     * @return 조회된 UserStatus 데이터
     */
    @Override
    @Transactional(readOnly = true)
    public List<UserStatusDto> findAll() {

        List<UserStatus> userStatuses = userStatusRepository.findAll();

        return userStatuses.stream()
                .map(userStatusMapper::toDto)
                .toList();
    }

    /**
     * 주어진 ID에 해당하는 UserStatus 조회
     *
     * @param id 조회할 UserStatus ID
     * @return 조회된 UserStatus
     */
    @Override
    @Transactional(readOnly = true)
    public UserStatusDto find(UUID id) {

        UserStatus userStatus = userStatusRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("존재하지 않는 UserStatus입니다."));

        return userStatusMapper.toDto(userStatus);
    }

    /**
     * 주어진 ID에 해당하는 UserStatus 수정
     *
     * @param userStatusId  수정할 UserStatus ID
     * @param updateRequest 수정 요청 DTO
     * @return 수정된 UserStatus
     */
    @Override
    @Transactional
    public UserStatusDto update(UUID userStatusId, UserStatusUpdateRequest updateRequest) {

        UserStatus userStatus = userStatusRepository.findById(userStatusId)
                .orElseThrow(() -> new NoSuchElementException("존재하지 않는 UserStatus입니다."));

        userStatus.update(updateRequest.newLastActiveAt());

        userStatusRepository.save(userStatus);
        return userStatusMapper.toDto(userStatus);
    }

    /**
     * 주어진 유저ID에 해당하는 UserStatus 수정
     *
     * @param userId        수정할 UserStatus의 유저ID
     * @param updateRequest 수정 요청 DTO
     * @return 수정된 UserStatus
     */
    @Override
    @Transactional
    public UserStatusDto updateByUserId(UUID userId, UserStatusUpdateRequest updateRequest) {

        UserStatus userStatus = userStatusRepository.findByUser_Id(userId)
                .orElseThrow(() -> new NoSuchElementException("존재하지 않는 UserStatus입니다."));

        userStatus.update(updateRequest.newLastActiveAt());

        userStatusRepository.save(userStatus);
        return userStatusMapper.toDto(userStatus);
    }

    /**
     * 주어진 ID에 해당하는 UserStatus 삭제
     *
     * @param id 삭제할 UserStatus ID
     */
    @Override
    @Transactional
    public void delete(UUID id) {
        
        UserStatus foundUserStatus = userStatusRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("존재하지 않는 UserStatus입니다."));

        userStatusRepository.deleteById(foundUserStatus.getId());
    }
}
