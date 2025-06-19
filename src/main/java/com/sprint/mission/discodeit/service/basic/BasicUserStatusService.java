package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.common.ErrorMessages;
import com.sprint.mission.discodeit.dto.data.UserStatusDto;
import com.sprint.mission.discodeit.dto.request.UserStatusCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.mapper.UserStatusMapper;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.UserStatusService;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class BasicUserStatusService implements UserStatusService {

    public final UserRepository userRepository;
    public final UserStatusRepository userStatusRepository;
    public final UserStatusMapper userStatusMapper;

    @Transactional
    @Override
    public UserStatusDto create(UserStatusCreateRequest request) {
        UUID userId = request.userId();
        log.info("[userStatus] 생성 요청: userId={}", userId);

        User user = userRepository.findById(userId)
            .orElseThrow(() -> {
                log.warn("[userStatus] 생성 실패 - 존재하지 않는 userId: userId={}", userId);
                return new NoSuchElementException(
                    ErrorMessages.format("User", ErrorMessages.ERROR_NOT_FOUND));
            });

        if (user.getStatus() != null) {
            log.warn("[userStatus] 생성 실패 - userStatus 중복됨: userId={}", userId);
            throw new IllegalArgumentException(
                ErrorMessages.format("UserStatus", ErrorMessages.ERROR_EXISTS));
        }

        Instant lastActiveAt = request.lastActiveAt();
        UserStatus status = new UserStatus(user, lastActiveAt);
        userStatusRepository.save(status);
        log.info("[userStatus] 생성 완료: userStatusId={}, userId={}", status.getId(), userId);

        return userStatusMapper.toDto(status);
    }

    @Transactional(readOnly = true)
    @Override
    public UserStatusDto find(UUID userStatusId) {
        return userStatusRepository.findById(userStatusId)
            .map(userStatusMapper::toDto)
            .orElseThrow(() -> new NoSuchElementException(
                ErrorMessages.format("UserStatus", ErrorMessages.ERROR_NOT_FOUND)));
    }

    @Transactional(readOnly = true)
    @Override
    public List<UserStatusDto> findAll() {
        log.info("[userStatus] 전체 조회 요청");
        List<UserStatusDto> userStatusDtos = userStatusRepository.findAll().stream()
            .map(userStatusMapper::toDto)
            .toList();
        log.info("[userStatus] 전체 조회 응답: size={}", userStatusDtos.size());

        return userStatusDtos;
    }

    @Transactional
    @Override
    public UserStatusDto update(UUID userStatusId, UserStatusUpdateRequest request) {
        Instant newLastActiveAt = request.newLastActiveAt();
        log.info("[readStatus] 업데이트 요청: userStatusId={}, newLastActiveAt={}", userStatusId,
            newLastActiveAt);

        UserStatus userStatus = userStatusRepository.findById(userStatusId)
            .orElseThrow(() -> {
                log.warn("[readStatus] 업데이트 실패 - 존재하지 않는 id: userStausId={}", userStatusId);
                return new NoSuchElementException(
                    ErrorMessages.format("UserStatus", ErrorMessages.ERROR_NOT_FOUND));
            });

        userStatus.update(newLastActiveAt);
        log.info("[readStatus] 업데이트 완료: userStatusId={}, newLastActive={}", userStatusId,
            newLastActiveAt);

        return userStatusMapper.toDto(userStatus);
    }

    @Transactional
    @Override
    public UserStatusDto updateByUserId(UUID userId, UserStatusUpdateRequest request) {
        Instant newLastActiveAt = request.newLastActiveAt();
        log.info("[readStatus] 업데이트 요청: userId={}, newLastActiveAt={}", userId, newLastActiveAt);

        UserStatus userStatus = userStatusRepository.findByUserId(userId)
            .orElseThrow(() -> {
                log.warn("[readStatus] 업데이트 실패 - 존재하지 않는 id: userId={}", userId);
                return new NoSuchElementException(
                    ErrorMessages.format("UserStatus", ErrorMessages.ERROR_NOT_FOUND));
            });
        userStatus.update(newLastActiveAt);
        log.info("[readStatus] 업데이트 완료: userId={}, newLastActiveAt={}", userId, newLastActiveAt);

        return userStatusMapper.toDto(userStatus);
    }

    @Transactional
    @Override
    public void delete(UUID id) {
        if (!userStatusRepository.existsById(id)) {
            log.warn("[userStatus] 삭제 실패 - 존재하지 않는 id: id={}", id);
            throw new NoSuchElementException(
                ErrorMessages.format("UserStatus", ErrorMessages.ERROR_NOT_FOUND));
        }
        userStatusRepository.deleteById(id);
        log.info("[userStatus] 삭제 완료: id={}", id);
    }
}
