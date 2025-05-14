package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.userstatus.UserStatusRequestDTO;
import com.sprint.mission.discodeit.dto.userstatus.UserStatusResponseDTO;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.exception.NotFoundUserException;
import com.sprint.mission.discodeit.exception.NotFoundUserStatusException;
import com.sprint.mission.discodeit.exception.UserStatusAlreadyExistsException;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service("basicUserStatusService")
@RequiredArgsConstructor
public class BasicUserStatusService {

    private final UserStatusRepository userStatusRepository;
    private final UserRepository userRepository;

    public void save(UserStatusRequestDTO userStatusRequestDTO) {
        if (userRepository.findById(userStatusRequestDTO.getUserId()).isEmpty()) {
            throw new NotFoundUserException();
        }

        if (userStatusRepository.findByUserId(userStatusRequestDTO.getUserId()).isPresent()) {
            throw new UserStatusAlreadyExistsException();
        }

        UserStatus userStatus = UserStatusRequestDTO.toEntity(userStatusRequestDTO);

        userStatusRepository.save(userStatus);
    }

    public UserStatusResponseDTO findById(UUID id) {
        UserStatus userStatus = findUserStatus(id);

        return UserStatusResponseDTO.toDTO(userStatus);
    }

    public List<UserStatusResponseDTO> findAll() {
        return userStatusRepository.findAll().stream()
                .map(UserStatusResponseDTO::toDTO)
                .toList();
    }

    public UserStatusResponseDTO update(UUID id, UserStatusRequestDTO userStatusRequestDTO) {
        UserStatus userStatus = findUserStatus(id);

        userStatus.updateLastLoginTime(userStatusRequestDTO.getLastLoginTime());
        userStatusRepository.save(userStatus);

        return UserStatusResponseDTO.toDTO(userStatus);
    }

    public UserStatusResponseDTO updateByUserId(UUID userId) {
        UserStatus userStatus = userStatusRepository.findByUserId(userId)
                .orElseThrow(NotFoundUserStatusException::new);

        // 현재 시각으로 마지막 접속 시간 변경
        userStatus.updateLastLoginTime(Instant.now());
        userStatusRepository.save(userStatus);

        return UserStatusResponseDTO.toDTO(userStatus);
    }

    public void deleteById(UUID id) {
        userStatusRepository.deleteById(id);
    }

    private UserStatus findUserStatus(UUID id) {
        return userStatusRepository.findById(id)
                .orElseThrow(NotFoundUserStatusException::new);
    }
}
