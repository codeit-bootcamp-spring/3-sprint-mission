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
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.time.Instant;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

@Slf4j
@Validated
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class BasicUserStatusService implements UserStatusService {

  private final UserStatusRepository userStatusRepository;
  private final UserRepository userRepository;
  private final UserStatusMapper userStatusMapper;

  @Override
  @Transactional
  public UserStatusDto create(@Valid UserStatusCreateRequest request) {
    UUID userId = request.userId();

    User user = userRepository.findById(userId)
        .orElseThrow(() -> {
          log.error("사용자 조회 실패 - userId={}", userId);
          return new NoSuchElementException("유효하지 않은 사용자 (userId=" + userId + ")");
        });

    if (userStatusRepository.findByUserId(userId).isPresent()) {
      throw new IllegalArgumentException("이미 존재하는 userId입니다. (userId=" + userId + ")");
    }

    Instant lastActiveAt = request.lastActiveAt();
    UserStatus userStatus = new UserStatus(user, lastActiveAt);
    log.info("사용자 읽음 상태 entity 생성: {}", userStatus);

    userStatusRepository.save(userStatus);
    return userStatusMapper.toDto(userStatus);
  }

  @Override
  public UserStatusDto find(@NotNull UUID userStatusId) {
    UserStatus userStatus =  userStatusRepository.findById(userStatusId)
        .orElseThrow(() -> {
          log.error("사용자 상태 조회 실패 - userStatusId={}", userStatusId);
          return new NoSuchElementException("존재하지 않는 사용자 상태입니다. (userStatusId=" + userStatusId + ")");
        });
    return userStatusMapper.toDto(userStatus);
  }

  @Override
  public List<UserStatusDto> findAll() {
    List<UserStatus> userStatuses = userStatusRepository.findAll();
    return userStatuses.stream()
        .map(userStatusMapper::toDto)
        .collect(Collectors.toList());
  }

  @Override
  @Transactional
  public UserStatusDto update(@NotNull UUID userStatusId, @Valid UserStatusUpdateRequest request) {
    Instant newLastActiveAt = request.newLastActiveAt();

    UserStatus userStatus = userStatusRepository.findById(userStatusId)
        .orElseThrow(() -> {
          log.error("사용자 상태 조회 실패 - userStatusId={}", userStatusId);
          return new NoSuchElementException("존재하지 않는 사용자 상태입니다. (userStatusId=" + userStatusId + ")");
        });
    userStatus.update(newLastActiveAt);

    return userStatusMapper.toDto(userStatus);
  }

  @Override
  @Transactional
  public UserStatusDto updateByUserId(@NotNull UUID userId, @Valid UserStatusUpdateRequest request) {
    Instant newLastActiveAt = request.newLastActiveAt();

    UserStatus userStatus = userStatusRepository.findByUserId(userId)
        .orElseThrow(() -> {
          log.error("사용자 상태 조회 실패 - userId={}", userId);
          return new NoSuchElementException("존재하지 않는 사용자 상태입니다. (userId=" + userId + ")");
        });
    userStatus.update(newLastActiveAt);

    return userStatusMapper.toDto(userStatus);
  }

  @Override
  @Transactional
  public void delete(@NotNull UUID userStatusId) {
    UserStatus status = userStatusRepository.findById(userStatusId)
        .orElseThrow(() -> {
          log.error("사용자 상태 조회 실패 - userStatusId={}", userStatusId);
          return new NoSuchElementException("존재하지 않는 사용자 상태입니다. (userStatusId=" + userStatusId + ")");
        });
    userStatusRepository.delete(status);
  }
}
