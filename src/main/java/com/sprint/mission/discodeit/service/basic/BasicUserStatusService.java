package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.userstatus.UserStatusRequestDto;
import com.sprint.mission.discodeit.dto.userstatus.UserStatusResponseDto;
import com.sprint.mission.discodeit.dto.userstatus.UserStatusUpdateDto;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.exception.alreadyexist.UserStatusAlreadyExistsException;
import com.sprint.mission.discodeit.exception.notfound.NotFoundUserException;
import com.sprint.mission.discodeit.exception.notfound.NotFoundUserStatusException;
import com.sprint.mission.discodeit.mapper.struct.UserStatusStructMapper;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.UserStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service("basicUserStatusService")
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BasicUserStatusService implements UserStatusService {

    private final UserStatusRepository userStatusRepository;
    private final UserRepository userRepository;
    private final UserStatusStructMapper userStatusMapper;

    @Override
    @Transactional
    public UserStatusResponseDto create(UserStatusRequestDto userStatusRequestDTO) {
        UUID userId = userStatusRequestDTO.userId();
        if (!userRepository.existsById(userId)) {
            throw new NotFoundUserException();
        }

        if (userStatusRepository.existsByUserId(userId)) {
            throw new UserStatusAlreadyExistsException();
        }

        User user = findUser(userStatusRequestDTO.userId());
        Instant lastActiveAt = userStatusRequestDTO.lastActiveAt();

        UserStatus userStatus = new UserStatus(user, lastActiveAt);

        userStatusRepository.save(userStatus);

        return userStatusMapper.toDto(userStatus);
    }

    @Override
    public UserStatusResponseDto findById(UUID id) {
        UserStatus userStatus = findUserStatus(id);

        return userStatusMapper.toDto(userStatus);
    }

    @Override
    public List<UserStatusResponseDto> findAll() {
        return userStatusRepository.findAll().stream()
                .map(userStatusMapper::toDto)
                .toList();
    }

    @Override
    @Transactional
    public UserStatusResponseDto update(UUID id, UserStatusUpdateDto userStatusUpdateDTO) {
        UserStatus userStatus = findUserStatus(id);

        userStatus.updatelastActiveAt(userStatusUpdateDTO.newLastActiveAt());
        userStatusRepository.save(userStatus);

        return userStatusMapper.toDto(userStatus);
    }

    @Override
    @Transactional
    public UserStatusResponseDto updateByUserId(UUID userId,
                                                UserStatusUpdateDto userStatusUpdateDTO) {
        UserStatus userStatus = userStatusRepository.findByUserId(userId)
                .orElseThrow(NotFoundUserStatusException::new);

        userStatus.updatelastActiveAt(userStatusUpdateDTO.newLastActiveAt());
        userStatusRepository.save(userStatus);

        return userStatusMapper.toDto(userStatus);
    }

    @Override
    @Transactional
    public void deleteById(UUID id) {
        userStatusRepository.deleteById(id);
    }

    private UserStatus findUserStatus(UUID id) {
        return userStatusRepository.findById(id)
                .orElseThrow(NotFoundUserStatusException::new);
    }

    private User findUser(UUID id) {
        return userRepository.findById(id)
                .orElseThrow(NotFoundUserException::new);
    }
}
