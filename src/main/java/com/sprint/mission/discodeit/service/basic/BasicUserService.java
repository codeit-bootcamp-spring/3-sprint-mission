package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentDTO;
import com.sprint.mission.discodeit.dto.user.UserRequestDTO;
import com.sprint.mission.discodeit.dto.user.UserResponseDTO;
import com.sprint.mission.discodeit.dto.user.UserUpdateDTO;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.exception.duplicate.DuplicateEmailException;
import com.sprint.mission.discodeit.exception.duplicate.DuplicateNameException;
import com.sprint.mission.discodeit.exception.notfound.NotFoundUserException;
import com.sprint.mission.discodeit.exception.notfound.NotFoundUserStatusException;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.UserService;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service("basicUserService")
@RequiredArgsConstructor
public class BasicUserService implements UserService {

  private final UserRepository userRepository;
  private final BinaryContentRepository binaryContentRepository;
  private final UserStatusRepository userStatusRepository;

  @Override
  public User create(UserRequestDTO userRequestDTO, BinaryContentDTO binaryContentDTO) {
    String username = userRequestDTO.username();
    String email = userRequestDTO.email();

    if (userRepository.existsByUsername(username)) {
      throw new DuplicateNameException(username);
    }

    if (userRepository.existsByEmail(email)) {
      throw new DuplicateEmailException(email);
    }

    User user = UserRequestDTO.toEntity(userRequestDTO);

    // 프로필 이미지를 등록한 경우
    if (binaryContentDTO != null) {
      BinaryContent profileImage = BinaryContentDTO.toEntity(binaryContentDTO);
      user.updateProfile(profileImage);
      binaryContentRepository.save(profileImage);
    }

    UserStatus userStatus = new UserStatus(user, Instant.now());

    userStatusRepository.save(userStatus);
    userRepository.save(user);

    return user;
  }

  @Override
  public UserResponseDTO findById(UUID id) {
    User user = findUser(id);

    UserStatus userStatus = findUserStatus(id);

    // 마지막 접속 시간 확인
    user.updateStatus(userStatus);

    return User.toDTO(user);
  }

  @Override
  public List<UserResponseDTO> findAll() {
    List<UserResponseDTO> users = userRepository.findAll().stream()
        .map(user -> {
          UserStatus userStatus = findUserStatus(user.getId());
          user.updateStatus(userStatus);
          return User.toDTO(user);
        })
        .toList();

    return users;
  }


  @Override
  public UserResponseDTO update(UUID id, UserUpdateDTO userUpdateDTO,
      BinaryContentDTO binaryContentDTO) {
    User user = findUser(id);

    String newUsername = userUpdateDTO.newUsername();
    String newEmail = userUpdateDTO.newEmail();

    if (newUsername != null) {
      userRepository.findByUsername(newUsername)
          .filter(u -> !u.getId().equals(user.getId()))
          .ifPresent(u -> {
            throw new DuplicateNameException(newUsername);
          });
      user.updateName(newUsername);
    }

    if (newEmail != null) {
      userRepository.findByEmail(newEmail)
          .filter(u -> !u.getId().equals(user.getId()))
          .ifPresent(u -> {
            throw new DuplicateEmailException(newEmail);
          });
      user.updateEmail(newEmail);
    }

    // 프로필 이미지 처리
    BinaryContent profile = user.getProfile();
    if (binaryContentDTO != null) {
      BinaryContent profileImage = BinaryContentDTO.toEntity(binaryContentDTO);
      if (profile != null) {
        binaryContentRepository.deleteById(profile.getId());
      }
      user.updateProfile(profileImage);
      binaryContentRepository.save(profileImage);
    } else if (profile != null) {
      binaryContentRepository.deleteById(profile.getId());
      user.updateProfile(null);
    }

    Optional.ofNullable(userUpdateDTO.newPassword()).ifPresent(user::updatePassword);

    userRepository.save(user);

    return User.toDTO(user);
  }

  @Override
  public void deleteById(UUID id) {
    User user = findUser(id);

    userRepository.deleteById(id);
    userStatusRepository.deleteByUserId(id);

    if (user.getProfile() != null) {
      binaryContentRepository.deleteById(user.getProfile().getId());
    }
  }

  private User findUser(UUID id) {
    return userRepository.findById(id)
        .orElseThrow(NotFoundUserException::new);
  }

  private UserStatus findUserStatus(UUID id) {
    return userStatusRepository.findByUserId(id)
        .orElseThrow(NotFoundUserStatusException::new);
  }
}
