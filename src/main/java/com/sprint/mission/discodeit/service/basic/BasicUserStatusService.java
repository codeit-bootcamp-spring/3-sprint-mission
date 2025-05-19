package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.userstatus.UserStatusRequestDTO;
import com.sprint.mission.discodeit.dto.userstatus.UserStatusResponseDTO;
import com.sprint.mission.discodeit.dto.userstatus.UserStatusUpdateDTO;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.exception.alreadyexist.UserStatusAlreadyExistsException;
import com.sprint.mission.discodeit.exception.notfound.NotFoundUserException;
import com.sprint.mission.discodeit.exception.notfound.NotFoundUserStatusException;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.UserStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service("basicUserStatusService")
@RequiredArgsConstructor
public class BasicUserStatusService implements UserStatusService {

  private final UserStatusRepository userStatusRepository;
  private final UserRepository userRepository;

  @Override
  public UserStatus create(UserStatusRequestDTO userStatusRequestDTO) {
    if (userRepository.findById(userStatusRequestDTO.userId()).isEmpty()) {
      throw new NotFoundUserException();
    }

    if (userStatusRepository.findByUserId(userStatusRequestDTO.userId()).isPresent()) {
      throw new UserStatusAlreadyExistsException();
    }

    UserStatus userStatus = UserStatusRequestDTO.toEntity(userStatusRequestDTO);

    userStatusRepository.save(userStatus);

    return userStatus;
  }

  @Override
  public UserStatusResponseDTO findById(UUID id) {
    UserStatus userStatus = findUserStatus(id);

    return UserStatus.toDTO(userStatus);
  }

  @Override
  public List<UserStatusResponseDTO> findAll() {
    return userStatusRepository.findAll().stream()
        .map(UserStatus::toDTO)
        .toList();
  }

  @Override
  public UserStatusResponseDTO update(UUID id, UserStatusUpdateDTO userStatusUpdateDTO) {
    UserStatus userStatus = findUserStatus(id);

    userStatus.updateLastLoginTime(userStatusUpdateDTO.newLastActiveAt());
    userStatusRepository.save(userStatus);

    return UserStatus.toDTO(userStatus);
  }

  @Override
  public UserStatusResponseDTO updateByUserId(UUID userId,
      UserStatusUpdateDTO userStatusUpdateDTO) {
    UserStatus userStatus = userStatusRepository.findByUserId(userId)
        .orElseThrow(NotFoundUserStatusException::new);

    userStatus.updateLastLoginTime(userStatusUpdateDTO.newLastActiveAt());
    userStatusRepository.save(userStatus);

    return UserStatus.toDTO(userStatus);
  }

  @Override
  public void deleteById(UUID id) {
    userStatusRepository.deleteById(id);
  }

  private UserStatus findUserStatus(UUID id) {
    return userStatusRepository.findById(id)
        .orElseThrow(NotFoundUserStatusException::new);
  }
}
