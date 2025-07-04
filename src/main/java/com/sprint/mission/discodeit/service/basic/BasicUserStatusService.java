package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.request.UserStatusCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserStatusUpdateRequest;
import com.sprint.mission.discodeit.dto.response.UserStatusResponse;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.exception.userstatus.UserStatusAlreadyExistsException;
import com.sprint.mission.discodeit.exception.userstatus.UserStatusNotFoundException;
import com.sprint.mission.discodeit.mapper.UserStatusMapper;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.UserStatusService;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
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

    @Transactional(readOnly = true)
    @Override
    public UserStatusResponse find(UUID id) {
        log.info("[BasicUserStatusService] Finding user status. [id={}]", id);

        return userStatusRepository.findById(id)
            .map(userStatusMapper::toResponse)
            .orElseThrow(() -> {
                log.warn("[BasicUserStatusService] User status not found. [id={}]", id);
                return new UserStatusNotFoundException(id);
            });
    }

    @Transactional(readOnly = true)
    @Override
    public List<UserStatusResponse> findAll() {
        log.info("[BasicUserStatusService] Finding all user statuses.");

        List<UserStatusResponse> result = userStatusRepository.findAll().stream()
            .map(userStatusMapper::toResponse)
            .collect(Collectors.toList());

        log.debug("[BasicUserStatusService] User statuses found. [count={}]", result.size());
        return result;
    }

    @Transactional
    @Override
    public UserStatusResponse create(UserStatusCreateRequest request) {
        UUID userId = request.userId();
        log.info("[BasicUserStatusService] Creating user status. [userId={}]", userId);

        User user = userRepository.findById(userId)
            .orElseThrow(() -> {
                log.warn("[BasicUserStatusService] User not found. [userId={}]", userId);
                return new UserNotFoundException(userId.toString());
            });

        if (userStatusRepository.findByUserId(userId).isPresent()) {
            log.warn("[BasicUserStatusService] User status already exists. [userId={}]", userId);
            throw new UserStatusAlreadyExistsException(userId);
        }

        UserStatus userStatus = new UserStatus(user, request.lastActiveAt());
        userStatusRepository.save(userStatus);

        log.debug("[BasicUserStatusService] User status created. [id={}]", userStatus.getId());
        return userStatusMapper.toResponse(userStatus);
    }

    @Transactional
    @Override
    public UserStatusResponse update(UUID userStatusId, UserStatusUpdateRequest request) {
        log.info("[BasicUserStatusService] Updating user status. [id={}]", userStatusId);

        UserStatus userStatus = userStatusRepository.findById(userStatusId)
            .orElseThrow(() -> {
                log.warn("[BasicUserStatusService] User status not found for update. [id={}]",
                    userStatusId);
                return new UserStatusNotFoundException(userStatusId);
            });

        userStatus.update(request.newLastActiveAt());

        log.debug("[BasicUserStatusService] User status updated. [id={}]", userStatusId);
        return userStatusMapper.toResponse(userStatus);
    }

    @Transactional
    @Override
    public UserStatusResponse updateByUserId(UUID userId, UserStatusUpdateRequest request) {
        log.info("[BasicUserStatusService] Updating user status by userId. [userId={}]", userId);

        UserStatus userStatus = userStatusRepository.findByUserId(userId)
            .orElseThrow(() -> {
                log.warn("[BasicUserStatusService] User status not found for userId. [userId={}]",
                    userId);
                return new UserStatusNotFoundException(userId);
            });

        userStatus.update(request.newLastActiveAt());

        log.debug("[BasicUserStatusService] User status updated by userId. [userId={}]", userId);
        return userStatusMapper.toResponse(userStatus);
    }

    @Transactional
    @Override
    public UserStatusResponse delete(UUID id) {
        log.info("[BasicUserStatusService] Deleting user status. [id={}]", id);

        UserStatus userStatus = userStatusRepository.findById(id)
            .orElseThrow(() -> {
                log.warn("[BasicUserStatusService] User status not found for deletion. [id={}]",
                    id);
                return new UserStatusNotFoundException(id);
            });

        userStatusRepository.deleteById(id);

        log.debug("[BasicUserStatusService] User status deleted. [id={}]", id);
        return userStatusMapper.toResponse(userStatus);
    }
}
