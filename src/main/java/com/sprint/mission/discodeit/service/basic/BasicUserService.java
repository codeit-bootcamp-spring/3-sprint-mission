package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.CreateBinaryContentRequest;
import com.sprint.mission.discodeit.dto.CreateUserRequest;
import com.sprint.mission.discodeit.dto.UserDto;
import com.sprint.mission.discodeit.dto.UpdateUserRequest;
import com.sprint.mission.discodeit.entitiy.BinaryContent;
import com.sprint.mission.discodeit.entitiy.User;
import com.sprint.mission.discodeit.entitiy.UserStatus;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.UserService;
import com.sun.jdi.request.DuplicateRequestException;
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
  public User create(CreateUserRequest userRequest,
      Optional<CreateBinaryContentRequest> binaryContentRequest) {
    User user = new User(null, userRequest.username(), userRequest.password(), userRequest.email());
    //중복검사후 아닐경우 save
    try {
      if (!userRepository.duplicateCheck(user)) {
        binaryContentRequest.ifPresent(request -> {
          BinaryContent binaryContent = new BinaryContent(
              request.filename(),
              request.contentType(),
              request.bytes()
          );
          binaryContentRepository.save(binaryContent);
          user.setProfileId(binaryContent.getId());
        });
        userRepository.save(user);
        userStatusRepository.save(new UserStatus(user.getId()));
        return user;
      } else {
        throw new DuplicateRequestException();
      }
    } catch (DuplicateRequestException e) {
      System.out.println("중복된 name 또는 email 입니다.");
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
  public void update(UpdateUserRequest request,
      Optional<CreateBinaryContentRequest> binaryContentRequest) {
    User user = new User(null, request.username(), request.password(), request.email());
    user.setFriends(request.friends());
    UUID profileId = userRepository.readById(request.id()).get().getProfileId();

    binaryContentRequest.ifPresent(
        b -> {
          BinaryContent binaryContent = new BinaryContent(b.filename(), b.contentType(), b.bytes());
          if (profileId != null) {
            binaryContentRepository.update(profileId, binaryContent);
          } else {
            binaryContentRepository.save(binaryContent);
            user.setProfileId(binaryContent.getId());
          }
        });
    userRepository.update(request.id(), user);

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
