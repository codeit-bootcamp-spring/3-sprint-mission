package com.sprint.mission.discodeit.service.basic;


import com.sprint.mission.discodeit.dto.UserStatusCreateRequest;
import com.sprint.mission.discodeit.dto.UserStatusUpdateRequest;
import com.sprint.mission.discodeit.entitiy.User;
import com.sprint.mission.discodeit.entitiy.UserStatus;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.UserStatusService;
import com.sun.jdi.request.DuplicateRequestException;
import java.time.Instant;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BasicUserStatusService implements UserStatusService {

  private final UserStatusRepository userStatusRepository;
  private final UserRepository userRepository;

  @Override
  public UserStatus create(UserStatusCreateRequest request) {
    Optional<User> user = userRepository.readById(request.userId());
    try {
      if (user.isEmpty()) {
        throw new NoSuchElementException("해당id의 User는 존재하지 않습니다.");
      } else {
        List<UserStatus> read = userStatusRepository.read();
        boolean duplicatedUserStatus = read.stream()
            .anyMatch(userStatus -> userStatus.getUserId().equals(request.userId()));
        if (duplicatedUserStatus) {
          throw new DuplicateRequestException("중복된 userStatus입니다.");
        } else {
          return userStatusRepository.save(
              new UserStatus(request.userId(), request.lastActiveAt()));
        }
      }
    } catch (NoSuchElementException | DuplicateRequestException e) {
      System.out.println(e);
      return null;
    }
  }

  @Override
  public UserStatus find(UUID userStatusId) {
    Optional<UserStatus> userStatus = userStatusRepository.readById(userStatusId);
    try {
      if (userStatus.isPresent()) {
        return userStatus.get();
      } else {
        throw new NoSuchElementException("해당 userStatusId는 존재하지 않습니다");
      }
    } catch (NoSuchElementException e) {
      System.out.println(e);
      return null;
    }
  }

  @Override
  public UserStatus findByUserId(UUID userId) {
    return userStatusRepository.readByUserId(userId).orElseThrow(NoSuchElementException::new);
  }

  @Override
  public List<UserStatus> findAll() {
    return userStatusRepository.read();
  }

  @Override
  public void update(UUID userStatusId, UserStatusUpdateRequest request) {
    Optional<UserStatus> userStatus = userStatusRepository.readById(userStatusId);
    try {
      UserStatus updatedUserStatus = userStatus.map((u) -> {
        UserStatus tempUserStatus = new UserStatus(u.getUserId(), request.newLastActiveAt());
        tempUserStatus.setUpdatedAt(request.newLastActiveAt());
        userStatusRepository.update(u.getId(), tempUserStatus);
        return tempUserStatus;
      }).orElseThrow(NoSuchElementException::new);
    } catch (NoSuchElementException e) {
      System.out.println(e);
    }
  }

  @Override
  public void updateByUserId(UUID userId, UserStatusUpdateRequest request) {
    Optional<UserStatus> userStatus = userStatusRepository.readByUserId(userId);
    try {
      UserStatus updatedUserStatus = userStatus.map(u -> {
        UserStatus tempUserStatus = new UserStatus(userId, request.newLastActiveAt());
        tempUserStatus.setLastActiveAt(request.newLastActiveAt());
        tempUserStatus.setUpdatedAt(Instant.now());
        userStatusRepository.update(u.getId(), tempUserStatus);
        return tempUserStatus;
      }).orElseThrow(NoSuchElementException::new);
    } catch (NoSuchElementException e) {
      System.out.println(e);
    }
  }

  @Override
  public void delete(UUID userStatusId) {
    userStatusRepository.delete(userStatusId);
  }
}

