package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserUpdateRequest;
import com.sprint.mission.discodeit.dto.response.UserResponse;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.Role;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.user.DuplicateEmailException;
import com.sprint.mission.discodeit.exception.user.DuplicateUserException;
import com.sprint.mission.discodeit.exception.user.DuplicateUsernameException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Slf4j
public class BasicUserService implements UserService {

    private final UserRepository userRepository;
    private final BinaryContentRepository binaryContentRepository;
    private final BinaryContentStorage binaryContentStorage;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    @Override
    public UserResponse create(UserCreateRequest request,
        Optional<BinaryContentCreateRequest> optionalProfileCreateRequest) {

        log.info("[BasicUserService] Creating user. [username={}] [email={}]", request.username(),
            request.email());

        validateDuplicate(request.username(), request.email());

        BinaryContent profile = optionalProfileCreateRequest
            .map(profileReq -> {
                BinaryContent entity = new BinaryContent(
                    profileReq.fileName(),
                    (long) profileReq.bytes().length,
                    profileReq.contentType()
                );
                BinaryContent saved = binaryContentRepository.save(entity);
                binaryContentStorage.put(saved.getId(), profileReq.bytes());
                return saved;
            })
            .orElse(null);

        String encodedPassword = passwordEncoder.encode(request.password());

        User user = new User(request.username(), request.email(), encodedPassword, profile,
            Role.USER);
        userRepository.save(user);

        log.debug("[BasicUserService] User created. [id={}]", user.getId());
        return userMapper.toResponse(user);
    }

    @Transactional(readOnly = true)
    @Override
    public UserResponse find(UUID userId) {
        log.info("[BasicUserService] Finding user. [id={}]", userId);

        return userRepository.findById(userId)
            .map(userMapper::toResponse)
            .orElseThrow(() -> {
                log.warn("[BasicUserService] User not found. [id={}]", userId);
                return new UserNotFoundException(userId.toString());
            });
    }

    @Transactional(readOnly = true)
    @Override
    public List<UserResponse> findAll() {
        log.info("[BasicUserService] Finding all users.");
        List<UserResponse> result = userRepository.findAll().stream()
            .map(userMapper::toResponse)
            .collect(Collectors.toList());
        log.debug("[BasicUserService] Users found. [count={}]", result.size());
        return result;
    }

    @Transactional
    @PreAuthorize("#userId == authentication.principal.userResponse.id()")
    @Override
    public UserResponse update(UUID userId, UserUpdateRequest request,
        Optional<BinaryContentCreateRequest> optionalProfileCreateRequest) {
        log.info("[BasicUserService] Updating user. [id={}]", userId);

        User user = userRepository.findById(userId)
            .orElseThrow(() -> {
                log.warn("[BasicUserService] User not found for update. [id={}]", userId);
                return new UserNotFoundException(userId.toString());
            });

        String newUsername = request.newUsername();
        String newEmail = request.newEmail();

        if (!user.getEmail().equals(newEmail) && userRepository.existsByEmail(newEmail)) {
            log.warn("[BasicUserService] Email already exists. [email={}]", newEmail);
            throw new DuplicateEmailException(newEmail);
        }

        if (!user.getUsername().equals(newUsername) && userRepository.existsByUsername(
            newUsername)) {
            log.warn("[BasicUserService] Username already exists. [username={}]", newUsername);
            throw new DuplicateUsernameException(newUsername);
        }

        BinaryContent updatedProfile = user.getProfile();
        if (optionalProfileCreateRequest.isPresent()) {
            BinaryContentCreateRequest profileRequest = optionalProfileCreateRequest.get();
            BinaryContent binaryContent = new BinaryContent(
                profileRequest.fileName(),
                (long) profileRequest.bytes().length,
                profileRequest.contentType()
            );
            binaryContentRepository.save(binaryContent);
            binaryContentStorage.put(binaryContent.getId(), profileRequest.bytes());

            Optional.ofNullable(user.getProfile()).ifPresent(old -> {
                binaryContentRepository.delete(old);
                binaryContentStorage.delete(old.getId());
            });

            updatedProfile = binaryContent;
        }

        String encodedPassword = (request.newPassword() != null)
            ? passwordEncoder.encode(request.newPassword())
            : null;

        user.update(newUsername, newEmail, encodedPassword, updatedProfile);

        log.debug("[BasicUserService] User updated. [id={}]", userId);
        return userMapper.toResponse(user);
    }

    @Transactional
    @PreAuthorize("#userId == authentication.principal.userResponse.id()")
    @Override
    public UserResponse delete(UUID userId) {
        log.info("[BasicUserService] Deleting user. [id={}]", userId);

        User user = userRepository.findById(userId)
            .orElseThrow(() -> {
                log.warn("[BasicUserService] User not found for deletion. [id={}]", userId);
                return new UserNotFoundException(userId.toString());
            });

        Optional.ofNullable(user.getProfile()).ifPresent(profile -> {
            binaryContentRepository.delete(profile);
            binaryContentStorage.delete(profile.getId());
        });

        userRepository.deleteById(userId);
        userRepository.delete(user);

        log.debug("[BasicUserService] User deleted. [id={}]", userId);
        return userMapper.toResponse(user);
    }

    private void validateDuplicate(String username, String email) {
        boolean usernameExists = userRepository.existsByUsername(username);
        boolean emailExists = userRepository.existsByEmail(email);

        if (usernameExists && emailExists) {
            log.warn("[BasicUserService] Duplicate username and email. [username={}] [email={}]",
                username, email);
            throw new DuplicateUserException(username, email);
        } else if (usernameExists) {
            log.warn("[BasicUserService] Duplicate username. [username={}]", username);
            throw new DuplicateUsernameException(username);
        } else if (emailExists) {
            log.warn("[BasicUserService] Duplicate email. [email={}]", email);
            throw new DuplicateEmailException(email);
        }
    }
}
