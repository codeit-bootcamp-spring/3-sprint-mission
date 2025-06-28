package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.annotation.Logging;
import com.sprint.mission.discodeit.dto.UserStatus.UserStatusCreateRequest;
import com.sprint.mission.discodeit.dto.UserStatus.UserStatusDto;
import com.sprint.mission.discodeit.dto.UserStatus.UserStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.exception.User.UserNotFoundException;
import com.sprint.mission.discodeit.exception.UserStatus.UserStatusAlreadyExistsException;
import com.sprint.mission.discodeit.exception.UserStatus.UserStatusNotFoundException;
import com.sprint.mission.discodeit.mapper.UserStatusMapper;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.UserStatusService;
import java.util.List;
import java.util.Optional;
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
     * @throws UserNotFoundException            유저가 존재하지 않는 경우
     * @throws UserStatusAlreadyExistsException 같은 User와 관련된 UserStatus가 이미 존재하는 경우
     */
    @Override
    @Transactional
    public UserStatusDto create(UserStatusCreateRequest createRequest) {

        UUID userId = createRequest.userId();

        User user = userRepository.findById(userId)
                .orElseThrow(() -> UserNotFoundException.byId(userId));

        Optional.ofNullable(user.getStatus())
                .ifPresent(status -> {
                    throw new UserStatusAlreadyExistsException(status.getId());
                });

        UserStatus userStatus = UserStatus.builder()
                .user(user)
                .lastActiveAt(createRequest.lastActiveAt())
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

        return userStatusRepository.findAll().stream()
                .map(userStatusMapper::toDto)
                .toList();
    }

    /**
     * 주어진 ID에 해당하는 UserStatus 조회
     *
     * @param id 조회할 UserStatus ID
     * @return 조회된 UserStatus
     * @throws UserStatusNotFoundException UserStatus가 존재하지 않는 경우
     */
    @Override
    @Transactional(readOnly = true)
    public UserStatusDto find(UUID id) {

        UserStatus userStatus = userStatusRepository.findById(id)
                .orElseThrow(() -> UserStatusNotFoundException.byId(id));

        return userStatusMapper.toDto(userStatus);
    }

    /**
     * 주어진 ID에 해당하는 UserStatus 수정
     *
     * @param userStatusId  수정할 UserStatus ID
     * @param updateRequest 수정 요청 DTO
     * @return 수정된 UserStatus
     * @throws UserStatusNotFoundException UserStatus가 존재하지 않는 경우
     */
    @Override
    @Transactional
    public UserStatusDto update(UUID userStatusId, UserStatusUpdateRequest updateRequest) {

        UserStatus userStatus = userStatusRepository.findById(userStatusId)
                .orElseThrow(() -> UserStatusNotFoundException.byId(userStatusId));

        userStatus.update(updateRequest.newLastActiveAt());
        return userStatusMapper.toDto(userStatus);
    }

    /**
     * 주어진 유저ID에 해당하는 UserStatus 수정
     *
     * @param userId        수정할 UserStatus의 유저ID
     * @param updateRequest 수정 요청 DTO
     * @return 수정된 UserStatus
     * @throws UserStatusNotFoundException 유저ID와 일치하는 UserStatus가 없는 경우
     */
    @Override
    @Transactional
    public UserStatusDto updateByUserId(UUID userId, UserStatusUpdateRequest updateRequest) {

        UserStatus userStatus = userStatusRepository.findByUserId(userId)
                .orElseThrow(() -> UserStatusNotFoundException.byUserId(userId));

        userStatus.update(updateRequest.newLastActiveAt());
        return userStatusMapper.toDto(userStatus);
    }

    /**
     * 주어진 ID에 해당하는 UserStatus 삭제
     *
     * @param id 삭제할 UserStatus ID
     * @throws UserStatusNotFoundException UserStatus가 존재하지 않는 경우
     */
    @Override
    @Transactional
    public void delete(UUID id) {

        if (!userStatusRepository.existsById(id)) {
            throw UserStatusNotFoundException.byId(id);
        }

        userStatusRepository.deleteById(id);
    }
}
