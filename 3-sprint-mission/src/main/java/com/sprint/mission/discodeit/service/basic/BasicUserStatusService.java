package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.data.UserStatusDto;
import com.sprint.mission.discodeit.dto.request.UserStatusCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.mapper.UserStatusMapper;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.UserStatusService;
import java.time.Instant;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Slf4j
public class BasicUserStatusService implements UserStatusService {

  private final UserStatusRepository userStatusRepository;
  private final UserRepository userRepository;
  private final UserStatusMapper userStatusMapper;

  @Override
  @Transactional
  public UserStatusDto create(UserStatusCreateRequest request) {
    UUID userId = request.userId();

    if (!userRepository.existsById(userId)) {
      log.warn("존재하지 않는 사용자입니다. userId={}", userId);
    }
    if (userStatusRepository.findByUserId(userId).isPresent()) {
      log.warn("이미 존재하는 사용자 상태입니다. userId={}", userId);
    }

    User user = userRepository.findById(userId)
        .orElseThrow(
            () -> new NoSuchElementException("User with id " + userId + " does not exist"));
    Optional.ofNullable(user.getUserStatus())
        .ifPresent(userStatus -> {
          throw new IllegalArgumentException("UserStatus already exists");
        });

    Instant lastActiveAt = request.lastLoginTime();

    log.debug("UserStatus 객체 생성 시작 request={}", request);
    UserStatus userStatus =
        UserStatus
            .builder()
            .user(user)
            .lastActiveAt(lastActiveAt)
            .build();
    log.debug("UserStatus 객체 생성 완료 userId={}", userStatus.getUser().getId());

    log.debug("[userStatusRepository] 데이터 저장 시작 userStatusId={}", userStatus.getId());
    userStatusRepository.save(userStatus);
    log.debug("[userStatusRepository] 데이터 저장 완료 userStatusId={}", userStatus.getId());

    return userStatusMapper.toDto(userStatus);
  }

  @Override
  @Transactional(readOnly = true)
  public UserStatusDto find(UUID id) {
    return userStatusRepository.findById(id)
        .map(userStatusMapper::toDto)
        .orElseThrow(() -> new NoSuchElementException("UserStatus with id " + id + " not found"));
  }

  @Override
  @Transactional(readOnly = true)
  public List<UserStatusDto> findAll() {
    return userStatusRepository.findAll().stream()
        .map(userStatusMapper::toDto)
        .toList();
  }

  @Override
  @Transactional
  public UserStatusDto update(UUID id, UserStatusUpdateRequest request) {
    Instant newLastActiveAt = request.newLastActiveAt();

    UserStatus userStatus = userStatusRepository.findById(id)
        .orElseThrow(() -> {
          log.warn("존재하지 않는 사용자 상태입니다. userStatusId={}", id);
          return new NoSuchElementException("UserStatus with id " + id + " not found");
        });

    log.debug("UserStatus 업데이트 시작 userStatusId={}", userStatus.getId());
    userStatus.update(newLastActiveAt);
    log.debug("UserStatus 업데이트 완료 userStatusId={}", userStatus.getId());

    return userStatusMapper.toDto(userStatus);
  }

  @Override
  @Transactional
  public UserStatusDto updateByUserId(UUID userId, UserStatusUpdateRequest userStatusUpdateDto) {
    Instant newLastActiveAt = userStatusUpdateDto.newLastActiveAt();

    UserStatus userStatus = userStatusRepository.findByUserId(userId)
        .orElseThrow(
            () -> {
              log.warn("존재하지 않는 사용자 상태입니다. userId={}", userId);
              return new NoSuchElementException("UserStatus with userId " + userId + " not found");
            });

    log.debug("UserStatus 업데이트 시작 userId={}", userStatus.getUser().getId());
    userStatus.update(newLastActiveAt);
    log.debug("UserStatus 업데이트 완료 userId={}", userStatus.getUser().getId());

    return userStatusMapper.toDto(userStatus);
  }

  @Override
  @Transactional
  public void delete(UUID id) {
    if (!userStatusRepository.existsById(id)) {
      log.warn("존재하지 않는 사용자 상태입니다. userStatusId={}", id);
    }

    log.debug("[userStatusRepository] 데이터 삭제 시작 userStatusId={}", id);
    userStatusRepository.deleteById(id);
    log.debug("[userStatusRepository] 데이터 삭제 완료 userStatusId={}", id);
  }

}
