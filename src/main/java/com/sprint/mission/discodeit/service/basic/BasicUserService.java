package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserUpdateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.Role;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.Common.ResourceNotFoundException;
import com.sprint.mission.discodeit.exception.User.UserAlreadyExistException;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import com.sprint.mission.discodeit.utils.SessionContext;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
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
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;
    private final SessionContext sessionContext;

    @Transactional
    @Override
    public UserDto create(UserCreateRequest userCreateRequest,
        Optional<BinaryContentCreateRequest> optionalProfileCreateRequest) {
        // validation (name, email 검증)
        if (userRepository.existsByUsernameOrEmail(userCreateRequest.username(),
            userCreateRequest.email())) {
            throw new UserAlreadyExistException();
        }

        // 프로필 이미지 id 생성(없으면 null 반환)
        BinaryContent nullableProfile = optionalProfileCreateRequest.map(
            profileRequest -> {
                BinaryContent binaryContent = new BinaryContent(profileRequest.fileName(),
                    (long) profileRequest.bytes().length,
                    profileRequest.contentType());
                binaryContentRepository.save(binaryContent);
                binaryContentStorage.put(binaryContent.getId(), profileRequest.bytes());
                return binaryContent;
            }).orElse(null);

        // 비밀번호 암호화
        String encodedPassword = passwordEncoder.encode(userCreateRequest.password());

        User user = new User(userCreateRequest.username(), userCreateRequest.email(),
            encodedPassword, nullableProfile, Role.USER);

        // DB저장
        User createdUser = this.userRepository.save(user);

        // status 정보
        boolean isOnline = sessionContext.getStatusFromSession(user.getId());
        return userMapper.toDto(createdUser, isOnline);
    }

    @Override
    public UserDto find(UUID userId) {
        User user = this.userRepository
            .findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException(("UserId = " + userId)));

        // status 정보
        boolean isOnline = sessionContext.getStatusFromSession(user.getId());
        return userMapper.toDto(user, isOnline);
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
            .map((user) -> {
                // status 정보
                boolean isOnline = sessionContext.getStatusFromSession(user.getId());
                return userMapper.toDto(user, isOnline);
            })
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
        user.update(updateRequest.newUsername(), updateRequest.newEmail(),
            updateRequest.newPassword(),
            nullableProfile);

        // status 정보
        boolean isOnline = sessionContext.getStatusFromSession(user.getId());

        return userMapper.toDto(user, isOnline);
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

    /* Admin 계정이 하나도 없을때 Admin 계정 생성 */
    @Transactional
    @Override
    public void initAdminIfNotExists() {
        boolean hasAdmin = userRepository.existsByRole(Role.ADMIN);
        log.debug("기본 ADMIN 유저 여부 체크: {}", hasAdmin ? "있음" : "없음");
        if (!hasAdmin) {
            // 비밀번호 암호화
            String encodedPassword = passwordEncoder.encode("admin");

            User user = new User("admin", "admin",
                encodedPassword, null, Role.ADMIN);

            //  DB저장
            User createdUser = userRepository.save(user);

            log.debug("기본 ADMIN 유저 생성 완료: {}, id: {}", createdUser.getUsername(),
                createdUser.getId());
        }

    }
}