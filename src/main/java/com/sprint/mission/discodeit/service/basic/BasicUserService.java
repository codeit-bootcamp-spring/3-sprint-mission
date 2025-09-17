package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserUpdateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.Role;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.event.BinaryContentCreatedEvent;
import com.sprint.mission.discodeit.event.RoleUpdatedEvent;
import com.sprint.mission.discodeit.exception.user.UserEmailAlreadyExistsException;
import com.sprint.mission.discodeit.exception.user.UserNameAlreadyExistsException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.SseService;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.UserSessionService;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Slf4j
public class BasicUserService implements UserService {

  private final UserRepository userRepository;
  private final UserMapper userMapper;
  private final BinaryContentRepository binaryContentRepository;
  private final ApplicationEventPublisher eventPublisher;
  private final PasswordEncoder passwordEncoder;
  private final UserSessionService userSessionService;
  private final CacheManager cacheManager;
  private final SseService sseService;


  @Transactional
  @Override
  public UserDto create(UserCreateRequest userCreateRequest,
      Optional<BinaryContentCreateRequest> optionalProfileCreateRequest) {
      log.info("[사용자 생성 시도] 사용자명: {}", userCreateRequest.username());

    var cache = cacheManager.getCache("users");
    if (cache != null) {
        cache.clear();
        log.info("[사용자 목록 캐시 무효화] - 새 사용자 생성으로 인함");
    }

      String username = userCreateRequest.username();
    String email = userCreateRequest.email();

    if (userRepository.existsByEmail(email)) {
      log.error("[유저 등록 실패] 해당 email은 이미 등록되어 있습니다. email : {}", email);
      throw new UserEmailAlreadyExistsException();
    }
    if (userRepository.existsByUsername(username)) {
      log.error("[유저 등록 실패] 해당 유저 이름은 이미 등록되어 있습니다. username : {}", username);
      throw new UserNameAlreadyExistsException();
    }

    BinaryContent nullableProfile = optionalProfileCreateRequest
        .map(profileRequest -> {
          String fileName = profileRequest.fileName();
          String contentType = profileRequest.contentType();
          byte[] bytes = profileRequest.bytes();
          BinaryContent binaryContent = new BinaryContent(fileName, (long) bytes.length,
              contentType);
          binaryContentRepository.save(binaryContent);
          eventPublisher.publishEvent(new BinaryContentCreatedEvent(binaryContent.getId(), bytes, fileName));
          return binaryContent;
        })
        .orElse(null);
    log.info("[유저 등록] 유저 프로필이 생성되었습니다.");

    // 비밀번호 암호화 로직
    String rawPassword = userCreateRequest.password();
    String encodedpassword = passwordEncoder.encode(rawPassword);
    log.info("[유저 등록] 비밀번호가 암호화되었습니다.");

    User user = new User(username, email, encodedpassword, nullableProfile);
    log.info("[유저 등록] 유저 ID: {}", user.getId());

    Instant now = Instant.now();

    userRepository.save(user);

      UserDto userDto = userMapper.toDto(user, false);

    try {
        sseService.broadcast("users.created", userDto);
        log.debug("SSE 사용자 생성 이벤트 전송 성공 : userId = {}, online = false",
            user.getId());
    } catch (Exception e) {
        log.error("SSE 사용자 생성 이벤트 전송 실패 : userId = {}, error = {}",
            user.getId(), e.getMessage());
    }

    return userDto;
  }

  @Transactional(readOnly = true)
  @Override
  public UserDto find(UUID userId) {
     log.info("[유저 조회 시도] 유저 ID : {}", userId);

      return userRepository.findById(userId)
          .map(user -> {
              boolean isOnline = userSessionService.isUserOnline(userId);
              return userMapper.toDto(user, isOnline);
          })
          .orElseThrow(() -> {
              log.error("[유저 조회 실패] 해당 유저를 찾을 수 없습니다. 유저 ID : {}", userId);
              return new UserNotFoundException();
          });
  }

  @Transactional(readOnly = true)
  @Override
  @Cacheable(value = "users")
  public List<UserDto> findAll() {
      log.info("[모든 유저 조회 시도]");

      return userRepository.findAllWithProfileAndStatus()
          .stream()
          .map(user -> {
              boolean isOnline = userSessionService.isUserOnline(user.getId());
              return userMapper.toDto(user, isOnline);
          })
          .toList();
  }

  @Transactional
  @Override
  @CacheEvict(value = "users", allEntries = true)
  public UserDto update(UUID userId, UserUpdateRequest userUpdateRequest,
      Optional<BinaryContentCreateRequest> optionalProfileCreateRequest) {
      log.info("[유저 수정 시도]");

    User user = userRepository.findById(userId)
        .orElseThrow(() -> {
            log.error("[유저 조회 실패] 해당 유저를 찾을 수 없습니다. 유저 ID : {} ", userId);
            return new UserNotFoundException();
        });

    String newUsername = userUpdateRequest.newUsername();
    String newEmail = userUpdateRequest.newEmail();
    if (userRepository.existsByEmail(newEmail)) {
      log.error("[유저 수정 실패] 해당 e-mail은 이미 사용하고 있습니다. e-mail : {} ", newEmail);
      throw new UserEmailAlreadyExistsException();
    }
    if (userRepository.existsByUsername(newUsername)) {
      log.error("[유저 수정 실패] 해당 유저 name은 이미 사용하고 있습니다. username : {} ", newUsername);
      throw new UserNameAlreadyExistsException();
    }

    BinaryContent nullableProfile = optionalProfileCreateRequest
        .map(profileRequest -> {

          String fileName = profileRequest.fileName();
          String contentType = profileRequest.contentType();
          byte[] bytes = profileRequest.bytes();
          BinaryContent binaryContent = new BinaryContent(fileName, (long) bytes.length,
              contentType);
          binaryContentRepository.save(binaryContent);
          eventPublisher.publishEvent(new BinaryContentCreatedEvent(binaryContent.getId(), bytes, fileName));
          return binaryContent;
        })
        .orElse(null);
      log.info("[유저 수정] 유저 프로필이 수정되었습니다. 유저 ID : {}", userId);

    // 비밀번호 암호화 로직 추가
    String newPassword = userUpdateRequest.newPassword();
    String encodedNewPassword = null;
    if (newPassword != null && !newPassword.isEmpty()) {
        encodedNewPassword = passwordEncoder.encode(newPassword);
        log.info("[유저 수정] 비밀번호가 암호화되었습니다. 유저 ID : {}", userId);
    }

    user.update(newUsername, newEmail, encodedNewPassword, nullableProfile);
      log.info("[유저 수정 성공] 유저 ID : {}", userId);

      boolean isOnline = userSessionService.isUserOnline(userId);
      UserDto userDto = userMapper.toDto(user, isOnline);

      try {
          sseService.broadcast("users.updated", userDto);
          log.debug("SSE 사용자 수정 이벤트 전송 성공: userId = {}, online = {}",
              userId, isOnline);
      } catch (Exception e) {
          log.error("SSE 사용자 수정 이벤트 전송 실패: userId = {}, error = {}",
              userId, e.getMessage());
      }

      return userDto;
  }

  @Transactional
  @Override
  @CacheEvict(value = "users", allEntries = true)
  public void delete(UUID userId) {
      User user = userRepository.findById(userId)
              .orElseThrow(() -> new UserNotFoundException());
      boolean isOnline = userSessionService.isUserOnline(userId);
      UserDto userDto = userMapper.toDto(user, isOnline);

      log.info("[유저 삭제 시도] 유저 ID : {}", userId);

    if (!userRepository.existsById(userId)) {
      log.error("[유저 삭제 실패] 해당 유저를 찾을 수 없습니다. 유저 ID : {} ", userId);
      throw new UserNotFoundException();
    }

      // 사용자 삭제 시 세션도 제거
      userSessionService.markUserOffline(userId);

      userRepository.deleteById(userId);
    log.info("[유저 삭제 성공] 유저 ID: {}", userId);

      try {
          sseService.broadcast("users.deleted", userDto);
          log.debug("SSE 사용자 삭제 이벤트 전송 성공: userId = {}, online = {}",
              userId, isOnline);
      } catch (Exception e) {
          log.error("SSE 사용자 삭제 이벤트 전송 실패: userId = {}, error = {}",
              userId, e.getMessage());
      }
  }

    @Transactional
    @Override
    @CacheEvict(value = "users", allEntries = true)
    public UserDto updateRole(UUID userId, Role newRole) {
        log.info("[사용자 권한 변경 시도] userId: {}, newRole: {}", userId, newRole);

        User user = userRepository.findById(userId)
            .orElseThrow(() -> {
                log.error("[사용자 권한 변경 실패] 사용자를 찾을 수 없습니다. userId: {}", userId);
                return new UserNotFoundException();
            });

        Role oldRole = user.getRole();

        // 권한이 실제로 변경되는 경우만 처리
        if (!oldRole.equals(newRole)) {
            user.updateRole(newRole);

            // ★★★ 권한 변경 이벤트 발행 ★★★
            eventPublisher.publishEvent(new RoleUpdatedEvent(userId, oldRole, newRole));

            log.info("[사용자 권한 변경 성공] userId: {}, {} -> {}", userId, oldRole, newRole);
        } else {
            log.info("[사용자 권한 변경 스킵] 기존 권한과 동일합니다. userId: {}, role: {}", userId, newRole);
        }

        boolean isOnline = userSessionService.isUserOnline(user.getId());
        UserDto userDto = userMapper.toDto(user, isOnline);

        // SSE 브로드캐스트
        try {
            sseService.broadcast("users.updated", userDto);
            log.debug("SSE 사용자 권한 변경 이벤트 전송 성공: userId = {}, online = {}",
                userId, isOnline);
        } catch (Exception e) {
            log.error("SSE 사용자 권한 변경 이벤트 전송 실패: userId = {}, error = {}",
                userId, e.getMessage());
        }

        return userDto;
    }

    // @CachePut을 활용한 강제 캐시 갱신 ( 필요한 case에만 )
    @CachePut(value = "users")
    @Transactional(readOnly = true)
    public List<UserDto> refreshUserListCache() {
        log.info("[강제 사용자 목록 캐시 갱신] - DB에서 최신 데이터 조회");

        List<UserDto> users = userRepository.findAll()
            .stream()
            .map(userMapper::toDto)
            .toList();

        log.info("[사용자 목록 캐시 갱신 완료] 사용자 수: {}명", users.size());
        return users;
    }

    public void clearUserRelatedCaches(UUID userId) {
        // 해당 사용자의 채널 캐시 삭제
        var channelCache = cacheManager.getCache("userChannels");
        if (channelCache != null) {
            channelCache.evict(userId);
            log.info("[🗑️ 사용자 채널 캐시 삭제] 사용자 ID: {}", userId);
        }

        // 해당 사용자의 알림 캐시 삭제
        var notificationCache = cacheManager.getCache("userNotifications");
        if (notificationCache != null) {
            notificationCache.evict(userId);
            log.info("[🗑️ 사용자 알림 캐시 삭제] 사용자 ID: {}", userId);
        }
    }
}