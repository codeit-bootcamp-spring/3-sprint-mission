package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.RoleUpdateRequest;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserUpdateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.user.DuplicateUserException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Valid
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class BasicUserService implements UserService {

    private final UserRepository userRepository;
    private final BinaryContentRepository binaryContentRepository;
    private final BinaryContentStorage binaryContentStorage;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public UserDto create(
        UserCreateRequest userCreateRequest,
        Optional<BinaryContentCreateRequest> optionalProfileCreateRequest
    ) {
        String username = userCreateRequest.username();
        String email = userCreateRequest.email();

        if (userRepository.existsByUsername(username) || userRepository.existsByEmail(email)) {
            log.error("사용자 생성 실패 - username={}, email={}", username, email);
            throw new DuplicateUserException(username, email);
        }

        BinaryContent nullableProfile = optionalProfileCreateRequest
            .map(profileRequest -> {
                String fileName = profileRequest.fileName();
                String contentType = profileRequest.contentType();
                byte[] bytes = profileRequest.bytes();

                BinaryContent binaryContent = new BinaryContent(fileName, (long) bytes.length, contentType);
                log.debug("binaryContent entity 생성: {}", binaryContent);
                binaryContentRepository.save(binaryContent);

                binaryContentStorage.put(binaryContent.getId(), bytes);
                return binaryContent;
            })
        .orElse(null);

        String encodedPassword = passwordEncoder.encode(userCreateRequest.password());
        log.debug("암호화된 비밀번호: {}",  encodedPassword);

        User user = new User(username, email, encodedPassword, nullableProfile);
        User createdUser = userRepository.save(user);
        log.debug("사용자 entity 생성: {}",  createdUser);

        return userMapper.toDto(createdUser);
    }

    @Override
    public UserDto find(UUID userId) {
        return userRepository.findById(userId)
            .map(userMapper::toDto)
            .orElseThrow(() -> {
            log.error("사용자 조회 실패 - userId={}", userId);
            return new UserNotFoundException(userId);
        });
    }

    @Override
    public List<UserDto> findAll() {
        return userRepository.findAll()
            .stream()
            .map(userMapper::toDto)
            .toList();
    }

    @Override
    @PreAuthorize("#userId == principal.userDto.id()")
    @Transactional
    public UserDto update(
        UUID userId,
        UserUpdateRequest userUpdateRequest,
        Optional<BinaryContentCreateRequest> optionalProfileCreateRequest
    ) {
        User user = userRepository.findById(userId)
        .orElseThrow(() -> {
            log.error("사용자 조회 실패 - userId={}", userId);
            return new UserNotFoundException(userId);
        });

        String newUsername = userUpdateRequest.newUsername();
        String newEmail = userUpdateRequest.newEmail();
        if (userRepository.existsByUsername(newUsername) || userRepository.existsByEmail(newEmail)) {
          throw new DuplicateUserException(newUsername, newEmail);
        }

        BinaryContent newProfile = optionalProfileCreateRequest
            .map(profileRequest -> {
              Optional.ofNullable(user.getProfile())
                  .ifPresent(binaryContentRepository::delete);

              BinaryContent binaryContent = new BinaryContent(
                  profileRequest.fileName(),
                  (long) profileRequest.bytes().length,
                  profileRequest.contentType()
              );
              log.debug("파일 entity 생성: {}",   binaryContent);

              BinaryContent saveBc = binaryContentRepository.save(binaryContent);
              binaryContentStorage.put(binaryContent.getId(), profileRequest.bytes());
              return saveBc;
            })
            .orElse(user.getProfile());

        user.update(
            newUsername,
            newEmail,
            userUpdateRequest.newPassword(),
            newProfile
        );

        return userMapper.toDto(user);
   }

    @Override
    @PreAuthorize("#userId == principal.userDto.id()")
    @Transactional
    public void delete(UUID userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> {
                log.error("사용자 조회 실패 - userId={}", userId);
                return new UserNotFoundException(userId);
        });

        Optional.ofNullable(user.getProfile())
            .ifPresent(binaryContentRepository::delete);

        userRepository.delete(user);
    }

    @Override
    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public UserDto updateUserRole(RoleUpdateRequest roleUpdateRequest) {
        UUID userId = roleUpdateRequest.userId();

        User user = userRepository.findById(userId)
            .orElseThrow(() -> {
                log.error("사용자 조회 실패 - userId={}", userId);
                return new UserNotFoundException(userId);
            });

        user.updateRole(roleUpdateRequest.newRole());
        log.debug("수정된 사용자 Role: {}", user.getRole());

        return userMapper.toDto(user);
    }
}