package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.Dto.userStatus.UpdateUserStatusResponse;
import com.sprint.mission.discodeit.Dto.userStatus.UserStatusCreateRequest;
import com.sprint.mission.discodeit.Dto.userStatus.UserStatusCreateResponse;
import com.sprint.mission.discodeit.Dto.userStatus.UserStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.repository.jpa.JpaUserStatusRepository;
import com.sprint.mission.discodeit.service.UserStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;

/**
 * packageName    : com.sprint.mission.discodeit.service.basic fileName       :
 * BasicUserStatusService author         : doungukkim date           : 2025. 4. 28. description    :
 * =========================================================== DATE              AUTHOR NOTE
 * ----------------------------------------------------------- 2025. 4. 28.        doungukkim 최초 생성
 */
@Service("basicUserStatusService")
@RequiredArgsConstructor
public class BasicUserStatusService implements UserStatusService {

  private final JpaUserStatusRepository userStatusRepository;
//  private final UserStatusRepository userStatusRepository;
//  private final UserRepository userRepository;

  @Override
  public UserStatusCreateResponse create(UserStatusCreateRequest request) {
//    UUID userId = request.userId();
//
//    userRepository.findUserById(userId); // 없으면 throw error 날림
//
//    if (userStatusRepository.findUserStatusByUserId(userId) != null) { // userStatus 이미 있으면 예외 발생
//      throw new RuntimeException("user status already exist");
//    }
//
//    UserStatus userStatus = userStatusRepository.createUserStatus(request.userId());
//    boolean online = userStatusRepository.isOnline(userStatus.getId()); // throw
//
//    return new UserStatusCreateResponse(userStatus.getId(), userStatus.getUserId(), online);
    return null;
  }


  @Override
  public UserStatus find(UUID userStatusId) {
//    Objects.requireNonNull(userStatusId, "no userStatusId param");
//    return userStatusRepository.findById(userStatusId);
    return null;
  }

  @Override
  public List<UserStatus> findAll() {
//    return userStatusRepository.findAllUserStatus();
    return null;
  }

  @Override
  public void update(UserStatusUpdateRequest request) {
//    userStatusRepository.update(
//        request.userStatusId(), request.newTime()
//    );
  }


  @Override
  public UpdateUserStatusResponse updateByUserId(UUID userId, Instant newLastActiveAt) {
//    UserStatus userStatus = userStatusRepository.findUserStatusByUserId(userId);
//
//    if (userStatus == null) {
//      throw new NoSuchElementException("UserStatus with userId " + userId + " not found");
//    }
//    userStatusRepository.updateByUserId(
//        Objects.requireNonNull(userId, "no userId in param"),
//        Objects.requireNonNull(newLastActiveAt, "no userId in param")
//    );
//
//    boolean online = userStatusRepository.isOnline(userStatus.getId());
//
//    UpdateUserStatusResponse response = new UpdateUserStatusResponse(
//        userStatus.getId(),
//        userStatus.getCreatedAt(),
//        userStatus.getUpdatedAt(),
//        userStatus.getUserId(),
//        newLastActiveAt,
//        online
//    );
//
//    return response;
    return null;
  }

  @Override
  public void delete(UUID userStatusId) {
//    Objects.requireNonNull(userStatusId, "no userStatusId");
//    userStatusRepository.deleteById(userStatusId); // throw

  }
}
