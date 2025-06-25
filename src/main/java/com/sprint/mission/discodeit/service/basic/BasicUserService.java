package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserUpdateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.exception.Common.ResourceNotFoundException;
import com.sprint.mission.discodeit.exception.User.UserAlreadyExistException;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
@Service
@RequiredArgsConstructor
@Slf4j
public class BasicUserService implements UserService {

  private final UserRepository userRepository;
  private final BinaryContentRepository binaryContentRepository;
  private final BinaryContentStorage binaryContentStorage;

  private final UserMapper userMapper;

  @Transactional
  @Override
  public UserDto create(UserCreateRequest userCreateRequest,
      Optional<BinaryContentCreateRequest> optionalProfileCreateRequest) {
    // 1. validation (name, email이 유니크 해야함)
    if (userRepository.existsByUsernameOrEmail(userCreateRequest.username(),
        userCreateRequest.email())) {
      throw new UserAlreadyExistException();
    }

    // 2. 프로필 이미지 id 생성( 없으면 null 반환)
    BinaryContent nullableProfile = optionalProfileCreateRequest.map(
        profileRequest -> {
          BinaryContent binaryContent = new BinaryContent(profileRequest.fileName(),
              (long) profileRequest.bytes().length,
              profileRequest.contentType());
          binaryContentRepository.save(binaryContent);
          binaryContentStorage.put(binaryContent.getId(), profileRequest.bytes());
          return binaryContent;
        }).orElse(null);

    User user = new User(userCreateRequest.username(), userCreateRequest.email(),
        userCreateRequest.password(), nullableProfile);

    UserStatus userStatus = new UserStatus(user, Instant.now());

    // 3. DB저장
    User createdUser = this.userRepository.save(user);

    return userMapper.toDto(createdUser);
  }

  @Override
  public UserDto find(UUID userId) {
    User user = this.userRepository
        .findById(userId)
        .orElseThrow(() -> new ResourceNotFoundException(("UserId = " + userId)));

    return userMapper.toDto(user);
  }


  //TODO : 구현할것
  @Override
  public List<UserDto> find(String username) {
    return List.of();
  }

  @Override
  public List<UserDto> findAll() {
    return this.userRepository.findAllWithProfileAndStatus()
        .stream()
        .map(userMapper::toDto)
        .toList();
  }


  @Transactional
  @Override
  public UserDto update(UUID userId, UserUpdateRequest updateRequest,
      Optional<BinaryContentCreateRequest> optionalProfileCreateRequest) {
    // 0. validation (username, email이 유니크 해야함)
    if (userRepository.existsByUsernameOrEmail(updateRequest.newUsername(),
        updateRequest.newEmail())) {
      throw new UserAlreadyExistException();
    }

    User user = this.userRepository.findById(userId).
        orElseThrow(() -> new ResourceNotFoundException(("UserId = " + userId)));

    // 1. 프로필 이미지 id 생성( 없으면 null 반환)
    BinaryContent nullableProfile = optionalProfileCreateRequest.map(
        profileRequest -> {
          BinaryContent binaryContent = new BinaryContent(profileRequest.fileName(),
              (long) profileRequest.bytes().length,
              profileRequest.contentType());
          binaryContentRepository.save(binaryContent);
          binaryContentStorage.put(binaryContent.getId(), profileRequest.bytes());
          return binaryContent;
        }).orElse(null);

    // 2. user 업데이트
    user.update(updateRequest.newUsername(), updateRequest.newEmail(), updateRequest.newPassword(),
        nullableProfile);

    return userMapper.toDto(user);
  }


  //관련된 도메인도 같이 삭제( BinaryContent, UserStatus) -> CascadeType.ALL로 알아서 삭제
  @Transactional
  @Override
  public void delete(UUID userId) {
    if (!this.userRepository.existsById(userId)) {
      throw new ResourceNotFoundException(("UserId = " + userId));
    }

    /* UserRepository에서 해당 객체 삭제 */
    this.userRepository.deleteById(userId);
  }

}