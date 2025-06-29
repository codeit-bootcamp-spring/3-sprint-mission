package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.mapper.mapstruct.MapperFacade;
import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserUpdateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.BinaryContentService;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.exception.user.DuplicateUserException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
@Service
@Transactional
@Slf4j
public class BasicUserService implements UserService {

  private final UserRepository userRepository;
  private final UserStatusRepository userStatusRepository;
  private final BinaryContentService binaryContentService;
  private final MapperFacade mapperFacade;

  @Override
  public UserDto create(UserCreateRequest userCreateRequest,
      Optional<BinaryContentCreateRequest> optionalProfileCreateRequest) {
    String username = userCreateRequest.username();
    String email = userCreateRequest.email();

    log.info("사용자 생성 요청 - 사용자명: {}, 이메일: {}", username, email);

    if (userRepository.existsByEmail(email)) {
      log.error("이메일 중복 오류 - 이메일: {}", email);
      throw DuplicateUserException.withEmail(email);
    }
    if (userRepository.existsByUsername(username)) {
      log.error("사용자명 중복 오류 - 사용자명: {}", username);
      throw DuplicateUserException.withUsername(username);
    }

    log.debug("사용자 중복 검사 완료 - 사용자명: {}, 이메일: {}", username, email);

    // 개선: BinaryContentService에 위임하여 중복 제거
    BinaryContent profile = binaryContentService.createFromOptional(optionalProfileCreateRequest);
    if (profile != null) {
      log.debug("프로필 이미지 생성 완료 - 파일명: {}", profile.getFileName());
    }

    String password = userCreateRequest.password();
    User user = new User(username, email, password, profile);

    User savedUser = userRepository.save(user);
    log.info("사용자 저장 완료 - ID: {}, 사용자명: {}", savedUser.getId(), savedUser.getUsername());

    Instant now = Instant.now();
    UserStatus userStatus = new UserStatus(savedUser, now);
    userStatusRepository.save(userStatus);
    log.debug("사용자 상태 생성 완료 - 사용자 ID: {}", savedUser.getId());

    log.info("사용자 생성 완료 - ID: {}, 사용자명: {}, 이메일: {}", savedUser.getId(), savedUser.getUsername(), savedUser.getEmail());
    return mapperFacade.toDto(savedUser);
  }

  @Override
  @Transactional(readOnly = true)
  public UserDto find(UUID userId) {
    log.debug("사용자 조회 요청 - ID: {}", userId);
    return userRepository.findById(userId)
        .map(user -> {
          log.debug("사용자 조회 완료 - ID: {}, 사용자명: {}", user.getId(), user.getUsername());
          return mapperFacade.toDto(user);
        })
        .orElseThrow(() -> {
          log.error("사용자 조회 실패 - 존재하지 않는 ID: {}", userId);
          return UserNotFoundException.withUserId(userId);
        });
  }

  @Override
  @Transactional(readOnly = true)
  public List<UserDto> findAll() {
    log.info("전체 사용자 목록 조회 요청");
    List<UserDto> users = mapperFacade.toUserDtoList(userRepository.findAll());
    log.info("전체 사용자 목록 조회 완료 - 총 사용자 수: {}", users.size());
    return users;
  }

  @Override
  public UserDto update(UUID userId, UserUpdateRequest userUpdateRequest,
      Optional<BinaryContentCreateRequest> optionalProfileCreateRequest) {
    log.info("사용자 정보 수정 요청 - ID: {}", userId);

    User user = userRepository.findById(userId)
        .orElseThrow(() -> {
          log.error("사용자 수정 실패 - 존재하지 않는 ID: {}", userId);
          return UserNotFoundException.withUserId(userId);
        });

    log.debug("수정 대상 사용자 조회 완료 - ID: {}, 기존 사용자명: {}, 기존 이메일: {}",
        user.getId(), user.getUsername(), user.getEmail());

    String newUsername = userUpdateRequest.newUsername();
    String newEmail = userUpdateRequest.newEmail();

    if (newEmail != null && !newEmail.equals(user.getEmail()) && userRepository.existsByEmail(newEmail)) {
      log.error("이메일 중복 오류 - 새 이메일: {}", newEmail);
      throw DuplicateUserException.withEmail(newEmail);
    }
    if (newUsername != null && !newUsername.equals(user.getUsername())
        && userRepository.existsByUsername(newUsername)) {
      log.error("사용자명 중복 오류 - 새 사용자명: {}", newUsername);
      throw DuplicateUserException.withUsername(newUsername);
    }

    log.debug("사용자 수정 중복 검사 완료 - 새 사용자명: {}, 새 이메일: {}", newUsername, newEmail);

    // 🚀 개선: BinaryContentService에 위임하여 중복 제거
    BinaryContent newProfile = optionalProfileCreateRequest
        .map(request -> {
          BinaryContent profile = binaryContentService.create(request);
          log.debug("새 프로필 이미지 생성 완료 - 파일명: {}", profile.getFileName());
          return profile;
        })
        .orElse(user.getProfile());

    String newPassword = userUpdateRequest.newPassword();

    user.update(newUsername, newEmail, newPassword, newProfile);
    log.info("사용자 정보 수정 완료 - ID: {}, 새 사용자명: {}, 새 이메일: {}",
        userId, user.getUsername(), user.getEmail());

    return mapperFacade.toDto(user);
  }

  @Override
  public void delete(UUID userId) {
    log.info("사용자 삭제 요청 - ID: {}", userId);

    User user = userRepository.findById(userId)
        .orElseThrow(() -> {
          log.error("사용자 삭제 실패 - 존재하지 않는 ID: {}", userId);
          return UserNotFoundException.withUserId(userId);
        });

    log.debug("삭제 대상 사용자 조회 완료 - ID: {}, 사용자명: {}", user.getId(), user.getUsername());

    userRepository.delete(user);
    log.info("사용자 삭제 완료 - ID: {}, 사용자명: {}", userId, user.getUsername());
  }
}
