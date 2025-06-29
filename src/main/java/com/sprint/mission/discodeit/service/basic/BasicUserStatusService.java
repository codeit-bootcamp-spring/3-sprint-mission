package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.request.UserStatusCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserStatusUpdateRequest;
import com.sprint.mission.discodeit.dto.response.UserStatusDto;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.exception.userstatus.UserStatusAlreadyExistException;
import com.sprint.mission.discodeit.exception.userstatus.UserStatusNotFoundByUserException;
import com.sprint.mission.discodeit.exception.userstatus.UserStatusNotFoundException;
import com.sprint.mission.discodeit.mapper.UserStatusMapper;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.UserStatusService;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class BasicUserStatusService implements UserStatusService {

    private final UserStatusRepository userStatusRepository;
    private final UserRepository userRepository;
    private final UserStatusMapper userStatusMapper;

    private static final Logger logger = Logger.getLogger(BasicUserStatusService.class.getName()); // 필드로 Logger 선언

    // 상태 생성
    @Override
    @Transactional
    public UserStatusDto create(UserStatusCreateRequest request) {

        //유저 조회
        User user = userRepository.findById(request.userId())
                .orElseThrow(
                        () -> new UserStatusNotFoundByUserException(request.userId()));

        // 이미 UserStatus가 존재하는지 검사
        if (userStatusRepository.findUserStatusByUserId((request.userId())).isPresent()) {
            throw new UserStatusAlreadyExistException(request.userId());
        }

        // 새 UserStatus 생성
        Instant lastActiveAt = request.lastActiveAt();
        UserStatus userStatus = new UserStatus(user, lastActiveAt);

        userStatus.setUser(user);
        userStatusRepository.save(userStatus);

        return userStatusMapper.toDto(userStatus);
    }

    // 접속 상태 조회
    @Override
    @Transactional(readOnly = true)
    public UserStatusDto find(UUID userStatusId) {
        return userStatusRepository.findById(userStatusId)
                .map(userStatusMapper::toDto)
                .orElseThrow(
                        () -> new UserStatusNotFoundException(userStatusId));
    }

    // 접속 상태 전체 조회
    @Override
    @Transactional(readOnly = true)
    public List<UserStatusDto> findAll() {
        return userStatusRepository.findAll().stream()
                .map(userStatusMapper::toDto)
                .toList();
    }

    // 접속 상태 아이디로 업데이트
    @Override
    @Transactional
    public UserStatusDto update(UUID userStatusId, UserStatusUpdateRequest request) {
        Instant newLastActiveAt = request.newLastActiveAt();

        UserStatus userStatus = userStatusRepository.findById(userStatusId)
                .orElseThrow(
                        () -> new UserStatusNotFoundException(userStatusId));
        userStatus.update(newLastActiveAt);

        // 변경 감지
        return userStatusMapper.toDto(userStatus);

    }

    // 유저 상태 업데이트 (접속 시간을 기준으로)
    @Override
    @Transactional
    public UserStatusDto updateByUserId(UUID userId, UserStatusUpdateRequest request) {
        Instant newLastActiveAt = request.newLastActiveAt();

        UserStatus userStatus = userStatusRepository.findUserStatusByUserId(userId)
                .orElseThrow(
                        () -> new UserNotFoundException(userId));

        userStatus.update(newLastActiveAt);

        return userStatusMapper.toDto(userStatus);
    }

    // 상태 아이디로 삭제
    @Override
    @Transactional
    public void delete(UUID statusId) {
        UserStatus userStatus = userStatusRepository.findById(statusId)
                .orElseThrow(
                        () -> new UserStatusNotFoundException(statusId));

        // 영속성 전이 활용 -> User를 null로 두어 관계를 끊기
        User user = userStatus.getUser();
        if (user != null) {
            user.setStatus(null);
        }
        userStatusRepository.delete(userStatus);
    }
}