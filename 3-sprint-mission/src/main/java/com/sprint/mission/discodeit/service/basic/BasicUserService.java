package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.data.UserDTO;
import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserUpdateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.mapper.BinaryContentMapper;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.BinaryContentService;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import java.time.Instant;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Transactional
public class BasicUserService implements UserService {

  private final UserMapper userMapper;
  private final UserRepository userRepository;
  private final UserStatusRepository userStatusRepository;
  private final BinaryContentService binaryContentService;
  private final BinaryContentStorage binaryContentStorage;
  private final BinaryContentRepository binaryContentRepository;
  private final BinaryContentMapper binaryContentMapper;

  @Override
  @Transactional
  public UserDTO create(
      UserCreateRequest userCreateRequest,
      Optional<BinaryContentCreateRequest> profileCreateRequest
  ) {

    String username = userCreateRequest.username();
    String email = userCreateRequest.email();

    if (userRepository.existsByUsername(username)) {
      throw new IllegalArgumentException("이미 존재하는 username입니다.");
    }

    if (userRepository.existsByEmail(email)) {
      throw new IllegalArgumentException("이미 존재하는 email입니다.");
    }

    BinaryContent nullableProfile = profileCreateRequest
        .map(profileRequest -> {
          BinaryContent binaryContent =
              BinaryContent.builder()
                  .fileName(profileRequest.fileName())
                  .contentType(profileRequest.contentType())
                  .size((long) profileRequest.bytes().length)
                  .build();
//          binaryContentService.create(profileRequest);
          binaryContentRepository.save(binaryContent);
          binaryContentStorage.put(binaryContent.getId(), profileRequest.bytes());
          return binaryContent;
        })
        .orElse(null);

    String password = userCreateRequest.password();

    User user =
        User.builder()
            .username(username)
            .email(email)
            .password(password)
            .profile(nullableProfile)
            .build();

    Instant now = Instant.now();
    UserStatus userStatus =
        UserStatus.builder()
            .user(user)
            .lastActiveAt(now)
            .build();

    userRepository.save(user);
    userStatusRepository.save(userStatus);
    return userMapper.toDTO(user);
  }

  @Override
  @Transactional(readOnly = true)
  public UserDTO find(UUID id) {
    return userRepository.findById(id)
        .map(userMapper::toDTO)
        .orElseThrow(() -> new NoSuchElementException("해당 사용자가 존재하지 않습니다."));
  }

  @Override
  @Transactional(readOnly = true)
  public UserDTO findByUsername(String username) {
    return userRepository.findByUsername(username)
        .map(userMapper::toDTO)
        .orElseThrow(() -> new NoSuchElementException("해당 사용자가 존재하지 않습니다."));
  }


  @Override
  @Transactional(readOnly = true)
  public UserDTO findByEmail(String email) {
    return userRepository.findByEmail(email)
        .map(userMapper::toDTO)
        .orElseThrow(() -> new NoSuchElementException("해당 사용자가 존재하지 않습니다."));

  }

  @Override
  @Transactional(readOnly = true)
  public List<UserDTO> findAll() {
    return userRepository.findAllWithProfileAndUserStatus()
        .stream()
        .map(userMapper::toDTO)
        .toList();
  }

  @Override
  public UserDTO update(UUID userId, UserUpdateRequest userUpdateDTO,
      Optional<BinaryContentCreateRequest> optionalProfileCreateDTO) {
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new NoSuchElementException("해당 사용자가 존재하지 않습니다."));

    String newUsername = userUpdateDTO.newUsername();
    String newEmail = userUpdateDTO.newEmail();
    BinaryContent nullableProfile;

    if (userRepository.existsByUsername(newUsername)) {
      throw new IllegalArgumentException("이미 존재하는 유저 아이디입니다.");
    }

    if (userRepository.existsByEmail(newEmail)) {
      throw new IllegalArgumentException("이미 존재하는 이메일입니다.");
    }

    if (optionalProfileCreateDTO.isPresent()) {
      binaryContentService.delete(user.getProfile().getId());

      nullableProfile = optionalProfileCreateDTO
          .map(profileRequest -> {
            BinaryContent binaryContent =
                BinaryContent.builder()
                    .fileName(profileRequest.fileName())
                    .contentType(profileRequest.contentType())
                    .size((long) profileRequest.bytes().length)
                    .build();
//          binaryContentService.create(profileRequest);
            binaryContentRepository.save(binaryContent);
            binaryContentStorage.put(binaryContent.getId(), profileRequest.bytes());
            return binaryContent;
          })
          .orElse(null);
    } else {
      nullableProfile = user.getProfile();
    }

    String newPassword = userUpdateDTO.newPassword();
    user.update(newUsername, newEmail, newPassword, nullableProfile);

    return userMapper.toDTO(userRepository.save(user));
  }


  @Override
  public void delete(UUID id) {
    User user = userRepository.findById(id)
        .orElseThrow(() -> new NoSuchElementException("해당 사용자가 존재하지 않습니다."));

    Optional.ofNullable(user.getProfile().getId())
        .ifPresent(binaryContentService::delete);
    userStatusRepository.deleteByUserId(id);
    userRepository.deleteById(id);
  }
}
