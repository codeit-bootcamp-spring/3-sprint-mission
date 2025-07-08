package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.data.UserStatusDto;
import com.sprint.mission.discodeit.dto.request.UserStatusCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.exception.userstatus.DuplicateUserStatusException;
import com.sprint.mission.discodeit.exception.userstatus.UserStatusNotFoundException;
import com.sprint.mission.discodeit.mapper.UserStatusMapper;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.UserStatusService;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class BasicUserStatusService implements UserStatusService {

    private final UserRepository userRepository;
    private final UserStatusRepository userStatusRepository;
    private final UserStatusMapper userStatusMapper;

    @Override
    public UserStatusDto create(UserStatusCreateRequest request) {
        log.debug("사용자 상태 생성 시작 : userId = {}", request.userId());
        UUID userId = request.userId();

        User user = userRepository.findById(userId)
            .orElseThrow(() -> new NoSuchElementException("User with id " + userId + " not found"));
        Optional.ofNullable(user.getStatus())
            .ifPresent(status -> {
                throw DuplicateUserStatusException.withUserId(userId);
            });

        Instant lastActiveAt = request.lastActiveAt();
        UserStatus userStatus = new UserStatus(user, lastActiveAt);
        userStatusRepository.save(userStatus);

        log.info("사용자 상태 생성 완료 : id = {}, userId = {}", userStatus.getId(), userId);
        return userStatusMapper.toDto(userStatus);
    }

    @Override
    @Transactional(readOnly = true)
    public UserStatusDto find(UUID userStatusId) {
        log.debug("사용자 상태 조회 시작: id = {}", userStatusId);
        UserStatusDto dto = userStatusRepository.findById(userStatusId)
            .map(userStatusMapper::toDto)
            .orElseThrow(() -> UserStatusNotFoundException.withId(userStatusId));
        log.info("사용자 상태 조회 완료: id = {}", userStatusId);
        return dto;
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserStatusDto> findAll() {
        log.debug("전체 사용자 상태 목록 조회 시작");
        List<UserStatusDto> dtos = userStatusRepository.findAll().stream()
            .map(userStatusMapper::toDto)
            .toList();
        log.info("전체 사용자 상태 목록 조회 완료 : 조회된 항목 수 = {}", dtos.size());
        return dtos;
    }

    @Override
    public UserStatusDto update(UUID userStatusId, UserStatusUpdateRequest request) {
        Instant newLastActiveAt = request.newLastActiveAt();
        log.debug("사용자 상태 수정 시작 : id = {}, newLastActiveAt = {}", userStatusId, newLastActiveAt);

        UserStatus userStatus = userStatusRepository.findById(userStatusId)
            .orElseThrow(
                () -> UserStatusNotFoundException.withId(userStatusId));

        Instant newLastOnline = request.newLastActiveAt();

        userStatus.update(newLastOnline);

        log.info("사용자 상태 수정 완료 : id = {}", userStatusId);
        return userStatusMapper.toDto(userStatus);
    }

    @Override
    public UserStatusDto updateByUserId(UUID userId, UserStatusUpdateRequest request) {

        Instant newLastOnlineAt = request.newLastActiveAt();
        log.debug("사용자 ID 기반 상태 수정 시작 : userId = {}, newLastActiveAt = {}", userId,
            newLastOnlineAt);

        UserStatus userStatus = userStatusRepository.findByUserId(userId)
            .orElseThrow(
                () -> UserStatusNotFoundException.withId(userId));
        userStatus.update(newLastOnlineAt);

        log.info("사용자 ID 기반 상태 수정 완료 : userId = {}", userId);
        return userStatusMapper.toDto(userStatus);
    }

    @Override
    public void delete(UUID userStatusId) {
        log.debug("사용자 상태 삭제 시작 : id = {}", userStatusId);
        if (!userStatusRepository.existsById(userStatusId)) {
            throw UserStatusNotFoundException.withId(userStatusId);
        }
        userStatusRepository.deleteById(userStatusId);
        log.info("사용자 상태 삭제 완료 : id = {}", userStatusId);
    }

}
