package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.UserCreateRequest;
import com.sprint.mission.discodeit.dto.UserDto;
import com.sprint.mission.discodeit.dto.UserUpdateRequest;
import com.sprint.mission.discodeit.entitiy.BinaryContent;
import com.sprint.mission.discodeit.entitiy.User;
import com.sprint.mission.discodeit.entitiy.UserStatus;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.UserService;
import com.sun.jdi.request.DuplicateRequestException;
import java.time.Instant;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BasicUserService implements UserService {

  private final UserRepository userRepository;
  private final UserStatusRepository userStatusRepository;
  private final BinaryContentRepository binaryContentRepository;


  @Override
  public User create(UserCreateRequest userRequest,
      Optional<BinaryContentCreateRequest> binaryContentRequest) {
    User user = new User(null, userRequest.username(), userRequest.password(), userRequest.email());
    //중복검사후 아닐경우 save
    try {
      if (!userRepository.duplicateCheck(user)) {
        binaryContentRequest.ifPresent(request -> {
          BinaryContent binaryContent = new BinaryContent(
              request.filename(),
              request.contentType(),
              request.bytes(),
              (long) request.bytes().length
          );
          binaryContentRepository.save(binaryContent);
          user.setProfileId(binaryContent.getId());
        });
        userRepository.save(user);
        userStatusRepository.save(new UserStatus(user.getId(), Instant.now()));
        return user;
      } else {
        throw new DuplicateRequestException();
      }
    } catch (DuplicateRequestException e) {
      System.out.println("중복된 name 또는 newEmail 입니다.");
    }
    return null;
  }

  @Override
  public List<UserDto> findAll() {
    List<User> userList = userRepository.read();
    List<UserDto> findUserRespondList = new ArrayList<>();
    findUserRespondList = userList.stream()
        .map(user -> new UserDto(user.getId(), user.getCreatedAt(), user.getUpdatedAt(),
            user.getUsername(), user.getEmail(), user.getProfileId(),
            userStatusRepository.readByUserId(user.getId()).get().IsOnline()))
        .collect(Collectors.toList());
    return findUserRespondList;
  }

  @Override
  public UserDto find(UUID id) {
    Optional<User> user = userRepository.readById(id);
    try {
      return user.map(
              u -> new UserDto(u.getId(), u.getCreatedAt(), u.getUpdatedAt(), u.getUsername(),
                  u.getEmail(), u.getProfileId(),
                  userStatusRepository.readByUserId(u.getId()).get().getOnline()))
          .orElseThrow(NoSuchElementException::new);
    } catch (NoSuchElementException e) {
      System.out.println(e);
      return null;
    }

  }

  @Override
  public void update(UUID userId, UserUpdateRequest request,
      Optional<BinaryContentCreateRequest> binaryContentRequest) {
    User user = userRepository.readById(userId)
        .orElseThrow(() -> new NoSuchElementException("User Id " + userId + " not found"));
    User duplicateCheck = new User(null, request.newUsername(), request.newPassword(), null);
    if (!userRepository.duplicateCheck(duplicateCheck)) {
      UUID nullableProfileId = binaryContentRequest
          .map(profileRequest -> {
            Optional.ofNullable(user.getProfileId())
                .ifPresent(binaryContentRepository::delete);
            BinaryContent binaryContent = new BinaryContent(profileRequest.filename(),
                profileRequest.contentType(), profileRequest.bytes(),
                (long) profileRequest.bytes().length);
            return binaryContentRepository.save(binaryContent).getId();
          })
          .orElse(null);
      user.setPassword(request.newPassword());
      user.setProfileId(nullableProfileId);
      user.setUsername(request.newUsername());
      user.setEmail(request.newEmail());
      user.setUpdatedAt(Instant.now());
      userRepository.update(userId, user);
    } else {
      throw new DuplicateRequestException();
    }

  }

  @Override
  public void delete(UUID userId) {
    Optional<User> user = userRepository.readById(userId);
    UUID profileId;
    try {
      if (user.isPresent()) {
        User user1 = user.get();
        profileId = user1.getProfileId();
        UserStatus userStatus = userStatusRepository.readByUserId(user1.getId()).get();
        userStatusRepository.delete(userStatus.getId());
        binaryContentRepository.delete(profileId);
        userRepository.delete(userId);
      } else {
        throw new NoSuchElementException("존재하지 않는 userId입니다.");
      }
    } catch (NoSuchElementException e) {
      System.out.println(e);
    }

  }
}
