package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserUpdateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.exception.user.UserAlreadyExistsException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Slf4j
public class BasicUserService implements UserService {

  private final UserMapper userMapper;
  private final UserRepository userRepository;
  private final UserStatusRepository userStatusRepository;
  private final BinaryContentService binaryContentService;
  private final BinaryContentStorage binaryContentStorage;
  private final BinaryContentRepository binaryContentRepository;

  @Override
  @Transactional
  public UserDto create(
      UserCreateRequest userCreateRequest,
      Optional<BinaryContentCreateRequest> profileCreateRequest
  ) {

    String username = userCreateRequest.username();
    String email = userCreateRequest.email();

    if (userRepository.existsByUsername(username)) {
      log.warn("이미 존재하는 username입니다. username={}", username);
      throw UserAlreadyExistsException.fromUsername(username);
    }

    if (userRepository.existsByEmail(email)) {
      log.warn("이미 존재하는 email입니다. email={}", email);
      throw UserAlreadyExistsException.fromEmail(email);
    }

    log.debug("프로필 이미지 생성 시작 request={}", profileCreateRequest);
    BinaryContent nullableProfile = profileCreateRequest
//        .map(binaryContentService::create)
//        .map(binaryContentMapper::toEntity)
        .map(profileRequest -> {
          String fileName = profileRequest.fileName();
          String contentType = profileRequest.contentType();
          byte[] bytes = profileRequest.bytes();

          log.debug("프로필 이미지 객체 생성 시작");
          BinaryContent binaryContent = new BinaryContent(fileName, (long) bytes.length,
              contentType);
          log.debug("프로필 이미지 객체 생성 완료 binaryContentId={}", binaryContent.getId());

          log.debug("[binaryContentRepository] 프로필 이미지 저장 시작 binaryContentId={}",
              binaryContent.getId());
          binaryContentRepository.save(binaryContent);
          log.debug("[binaryContentRepository] 프로필 이미지 저장 완료 binaryContentId={}",
              binaryContent.getId());

          log.debug("[binaryContentStorage] 프로필 이미지 저장 시작 binaryContentId={}",
              binaryContent.getId());
          binaryContentStorage.put(binaryContent.getId(), bytes);
          log.debug("[binaryContentStorage] 프로필 이미지 저장 완료 binaryContentId={}",
              binaryContent.getId());

          return binaryContent;
        })
        .orElse(null);

    String password = userCreateRequest.password();

    log.debug("유저 생성 시작 request={}", userCreateRequest);
    User user =
        User.builder()
            .username(username)
            .email(email)
            .password(password)
            .profile(nullableProfile)
            .build();
    log.debug("유저 생성 완료 userId={}", user.getId());

    userRepository.save(user);
    log.debug("유저 저장 완료 userId={}", user.getId());

    Instant now = Instant.now();

    log.debug("유저 상태 생성 시작 request={}", userCreateRequest);
    UserStatus userStatus =
        UserStatus.builder()
            .user(user)
            .lastActiveAt(now)
            .build();
    log.debug("유저 상태 생성 완료 userStatusId={}", userStatus.getId());

    userStatusRepository.save(userStatus);
    log.debug("유저 상태 저장 완료 userStatusId={}", userStatus.getId());

    return userMapper.toDto(user);
  }

  @Override
  @Transactional(readOnly = true)
  public UserDto find(UUID id) {
    return userRepository.findById(id)
        .map(userMapper::toDto)
        .orElseThrow(() -> UserNotFoundException.fromUserId(id));
  }

  @Override
  @Transactional(readOnly = true)
  public UserDto findByUsername(String username) {
    return userRepository.findByUsername(username)
        .map(userMapper::toDto)
        .orElseThrow(() -> UserNotFoundException.fromUsername(username));
  }


  @Override
  @Transactional(readOnly = true)
  public UserDto findByEmail(String email) {
    return userRepository.findByEmail(email)
        .map(userMapper::toDto)
        .orElseThrow(() -> UserNotFoundException.fromUsername(email));

  }

  @Override
  @Transactional(readOnly = true)
  public List<UserDto> findAll() {
    return userRepository.findAllWithProfileAndUserStatus()
        .stream()
        .map(userMapper::toDto)
        .toList();
  }

  @Override
  @Transactional
  public UserDto update(UUID userId, UserUpdateRequest userUpdateDto,
      Optional<BinaryContentCreateRequest> optionalProfileCreateDto) {
    User user = userRepository.findById(userId)
        .orElseThrow(() -> {
          log.warn("해당 사용자가 존재하지 않습니다. userId={}", userId);
          return UserNotFoundException.fromUserId(userId);
        });

    String newUsername = userUpdateDto.newUsername();
    String newEmail = userUpdateDto.newEmail();
    BinaryContent nullableProfile;

    if (userRepository.existsByUsername(newUsername)) {
      log.warn("이미 존재하는 유저 이름입니다. username={}", newUsername);
    }

    if (userRepository.existsByEmail(newEmail)) {
      log.warn("이미 존재하는 이메일입니다. email={}", newEmail);
    }

    if (optionalProfileCreateDto.isPresent() && user.getProfile() != null) {
      binaryContentService.delete(user.getProfile().getId());
      log.debug("유저 프로필 삭제 완료 userId={}, profileId={}", user.getId(), user.getProfile().getId());
    }

    if (optionalProfileCreateDto.isPresent()) {
      log.debug("프로필 이미지 생성 시작 request={}", optionalProfileCreateDto);
      nullableProfile = optionalProfileCreateDto
//          .map(binaryContentService::create)
//          .map(binaryContentMapper::toEntity)
          .map(profileRequest -> {
            String fileName = profileRequest.fileName();
            String contentType = profileRequest.contentType();
            byte[] bytes = profileRequest.bytes();

            log.debug("프로필 이미지 객체 생성 시작 request={}", profileRequest);
            BinaryContent binaryContent = new BinaryContent(fileName, (long) bytes.length,
                contentType);
            log.debug("프로필 이미지 객체 생성 완료 profileId={}", binaryContent.getId());

            log.debug("[binaryContentRepository] 프로필 이미지 저장 시작 profileId={}",
                binaryContent.getId());
            binaryContentRepository.save(binaryContent);
            log.debug("[binaryContentRepository] 프로필 이미지 저장 완료 profileId={}",
                binaryContent.getId());

            log.debug("[binaryContentStorage] 프로필 이미지 저장 시작 profileId={}", binaryContent.getId());
            binaryContentStorage.put(binaryContent.getId(), bytes);
            log.debug("[binaryContentStorage] 프로필 이미지 저장 완료 profileId={}", binaryContent.getId());

            return binaryContent;
          })
          .orElse(null);
      log.debug("프로필 이미지 생성 완료 profileId={}", nullableProfile.getId());
    } else {
      nullableProfile = user.getProfile();
    }

    String newPassword = userUpdateDto.newPassword();

    log.debug("유저 업데이트 시작 userId={}", user.getId());
    user.update(newUsername, newEmail, newPassword, nullableProfile);
    log.debug("유저 업데이트 완료 userId={}", user.getId());

    log.debug("[userRepository] 유저 저장 시작 userId={}", user.getId());
    userRepository.save(user);
    log.debug("[userRepository] 유저 저장 완료 userId={}", user.getId());

    return userMapper.toDto(user);
  }


  @Override
  @Transactional
  public void delete(UUID id) {
    User user = userRepository.findById(id)
        .orElseThrow(() -> {
          log.warn("해당 사용자가 존재하지 않습니다. userId={}", id);
          return UserNotFoundException.fromUserId(id);
        });

    log.debug("[binaryContentService] 유저 삭제 시작 userId={}", user.getId());
    Optional.ofNullable(user.getProfile())
        .map(BinaryContent::getId)
        .ifPresent(binaryContentService::delete);
    log.debug("[binaryContentService] 유저 프로필 삭제 완료 userId={}", user.getId());

    userStatusRepository.deleteByUserId(id);
    log.debug("[userStatusRepository] 유저 상태 삭제 완료 userId={}", user.getId());

    userRepository.deleteById(id);
    log.debug("[userRepository] 유저 삭제 완료 userId={}", user.getId());
  }
}
