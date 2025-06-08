package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.assembler.UserAssembler;
import com.sprint.mission.discodeit.dto.UserDto;
import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserUpdateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.exception.DuplicateUserException;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.BinaryContentService;
import com.sprint.mission.discodeit.service.UserService;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
@Transactional
public class BasicUserService implements UserService {

  private final UserRepository userRepository;
  private final UserStatusRepository userStatusRepository;
  //만약 binaryContentService를 쓰지 않는다면 binaryContentService의 코드가 여기서도 반복되는데
  // 이때는 그냥 의존하는것이 낫나? vs 중복된 코드를 써도 분리가 좋나? -> binaryContentService를 사용할것.
  private final BinaryContentService binaryContentService;

  private final UserAssembler userAssembler;

  @Override
  public UserDto create(UserCreateRequest userCreateRequest,
      Optional<BinaryContentCreateRequest> profileCreateRequest) {
    // 1. validation (name, email이 유니크 해야함)
    if (this.hasSameEmailOrName(userCreateRequest.username(), userCreateRequest.email())) {
      throw new DuplicateUserException();
    }

    // 2. 프로필 이미지 id 생성( 없으면 null 반환)
    BinaryContent nullableBinaryContent = profileCreateRequest.map(
        this.binaryContentService::create).orElse(null);

    User user = new User(userCreateRequest.username(), userCreateRequest.email(),
        userCreateRequest.password(), nullableBinaryContent);

    // 3. DB저장
    User createdUser = this.userRepository.save(user);

    // 4. UserStatus 인스턴스 생성 및 DB 저장
    UserStatus userStatus = new UserStatus(user);
    this.userStatusRepository.save(userStatus);

    return userAssembler.toDtoWithOnline(createdUser);
  }

  @Override
  public UserDto find(UUID userId) {
    User user = this.userRepository
        .findById(userId)
        .orElseThrow(() -> new NoSuchElementException("User with id " + userId + " not found"));

    return userAssembler.toDtoWithOnline(user);
  }


  //TODO : 구현할것
  @Override
  public List<UserDto> find(String username) {
    return List.of();
  }

  @Override
  public List<UserDto> findAll() {
    List<UserDto> users = this.userRepository.findAll()
        .stream()
        .map(userAssembler::toDtoWithOnline)
        .toList();

    return users;
  }

  @Override
  public UserDto update(UUID userId, UserUpdateRequest updateRequest,
      Optional<BinaryContentCreateRequest> profileCreateRequest) {
    // 0. validation (username, email이 유니크 해야함)
    if (this.hasSameEmailOrName(updateRequest.newUsername(), updateRequest.newEmail())) {
      throw new DuplicateUserException();
    }

    User user = this.userRepository.findById(userId).
        orElseThrow(() -> new NoSuchElementException("User with id " + userId + " not found"));

    // 1. 프로필 이미지 업데이트
    profileCreateRequest.map(binaryContentCreateRequest -> {
      BinaryContent profileBinaryContent = this.binaryContentService.create(
          binaryContentCreateRequest);
      user.update(updateRequest.newUsername(), updateRequest.newEmail(),
          updateRequest.newPassword(), profileBinaryContent);
      return null;
    });

    user.update(updateRequest.newUsername(), updateRequest.newEmail(), updateRequest.newPassword(),
        null);

    /* 업데이트 후 다시 DB 저장 */
    this.userRepository.save(user);

    User updatedUser = this.userRepository.findById(userId).
        orElseThrow(() -> new NoSuchElementException("User with id " + userId + " not found"));

    return userAssembler.toDtoWithOnline(updatedUser);

  }


  //관련된 도메인도 같이 삭제( BinaryContent, UserStatus )
  @Override
  public void delete(UUID userId) {
    User user = this.userRepository.findById(userId).
        orElseThrow(() -> new NoSuchElementException("User with userId " + userId + " not found"));

    /* BinaryContentRepository에서 프로필사진 삭제 */
    this.binaryContentService.delete(user.getProfile().getId());

    /* UserStatusRepository에서 해당 객체 삭제 */
    UserStatus userStatus = this.userStatusRepository.findByUserId(user.getId()).
        orElseThrow(() -> new NoSuchElementException(
            "UserStatus with userId " + user.getId() + " not found"));
    this.userStatusRepository.deleteById(userStatus.getId());

    /* UserRepository에서 해당 객체 삭제 */
    this.userRepository.deleteById(userId);
  }


  @Override
  public boolean hasSameEmailOrName(String username, String email) {
    List<User> users = this.userRepository.findAll();

    return users.stream()
        .anyMatch((user) -> {
          return user.getEmail().equals(email) && user.getUsername().equals(username);
        });
  }

}
