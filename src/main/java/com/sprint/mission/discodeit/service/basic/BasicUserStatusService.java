package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.UserStatus.UserStatusCreateRequest;
import com.sprint.mission.discodeit.dto.UserStatus.UserStatusUpdateRequest;
import com.sprint.mission.discodeit.dto.UserStatusDto;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.mapper.UserStatusMapper;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.UserStatusService;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Transactional
public class BasicUserStatusService implements UserStatusService {
    private final UserStatusRepository userStatusRepository;
    private final UserRepository userRepository;
    private final UserStatusMapper userStatusMapper;

    @Override
    public UserStatusDto create(UserStatusCreateRequest request) {
        User user = userRepository.findById(request.userId())
                .orElseThrow(() -> new NoSuchElementException("해당 유저는 존재하지 않습니다."));

        if (userStatusRepository.findByUserId(request.userId()).isPresent()) {
            throw new IllegalStateException("해당 유저의 상태는 이미 등록되어 있습니다.");
        }

        UserStatus userStatus = userStatusRepository.save(new UserStatus(user, request.recentStatusAt()));
        return userStatusMapper.toDto(userStatus);
    }

    @Override
    public UserStatusDto find(UUID id) {
        return userStatusMapper.toDto(
                userStatusRepository.findByUserId(id)
                        .orElseThrow(() -> new NoSuchElementException("해당하는 유저 상태가 존재하지 않습니다."))
        );
    }

    @Override
    public List<UserStatusDto> findAll() {
        return userStatusRepository.findAll().stream()
                .map(userStatusMapper::toDto)
                .toList();
    }

    @Override
    public UserStatusDto update(UUID id, UserStatusUpdateRequest request) {
        UserStatus userStatus = userStatusRepository.findByUserId(id)
                .orElseThrow(() -> new NoSuchElementException("해당 유저 상태가 존재하지 않습니다."));
        userStatus.update(request.newLastActiveAt());
        return userStatusMapper.toDto(userStatus);
    }

    @Override
    public UserStatusDto updateByUserId(UUID userId, UserStatusUpdateRequest request) {
        return update(userId, request);
    }

    @Override
    public void delete(UUID id) {
        UserStatus userStatus = userStatusRepository.findByUserId(id)
                .orElseThrow(() -> new NoSuchElementException("해당 유저 상태가 존재하지 않습니다."));
        userStatusRepository.delete(userStatus);
    }
}