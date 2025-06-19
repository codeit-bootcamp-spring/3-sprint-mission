package com.sprint.mission.discodeit.service.basic;

import static com.sprint.mission.discodeit.global.constant.ErrorMessages.DUPLICATE_BOTH;
import static com.sprint.mission.discodeit.global.constant.ErrorMessages.DUPLICATE_EMAIL;
import static com.sprint.mission.discodeit.global.constant.ErrorMessages.DUPLICATE_USERNAME;
import static com.sprint.mission.discodeit.global.constant.ErrorMessages.USER_NOT_FOUND;

import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserUpdateRequest;
import com.sprint.mission.discodeit.dto.response.UserResponse;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.global.exception.DuplicateUserException;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import java.time.Instant;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class BasicUserService implements UserService {

    private final UserRepository userRepository;
    private final UserStatusRepository userStatusRepository;
    private final BinaryContentRepository binaryContentRepository;
    private final BinaryContentStorage binaryContentStorage;
    private final UserMapper userMapper;

    @Transactional
    @Override
    public UserResponse create(UserCreateRequest request,
        Optional<BinaryContentCreateRequest> optionalProfileCreateRequest) {

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

        User user = new User(request.username(), request.email(), request.password(), profile);
        userRepository.save(user);

        userStatusRepository.save(new UserStatus(user, Instant.now()));

        return userMapper.toResponse(user);
    }

    @Transactional(readOnly = true)
    @Override
    public UserResponse find(UUID userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new NoSuchElementException(USER_NOT_FOUND + userId));

        return userMapper.toResponse(user);
    }

    @Transactional(readOnly = true)
    @Override
    public List<UserResponse> findAll() {
        return userRepository.findAllWithProfileAndStatus().stream()
            .map(userMapper::toResponse)
            .collect(Collectors.toList());
    }

    @Transactional
    @Override
    public UserResponse update(UUID userId, UserUpdateRequest userUpdateRequest,
        Optional<BinaryContentCreateRequest> optionalProfileCreateRequest) {

        User user = userRepository.findById(userId)
            .orElseThrow(() -> new NoSuchElementException("User with id " + userId + " not found"));

        String newUsername = userUpdateRequest.newUsername();
        String newEmail = userUpdateRequest.newEmail();

        if (!user.getEmail().equals(newEmail) && userRepository.existsByEmail(newEmail)) {
            throw new IllegalArgumentException("User with email " + newEmail + " already exists");
        }

        if (!user.getUsername().equals(newUsername) && userRepository.existsByUsername(
            newUsername)) {
            throw new IllegalArgumentException(
                "User with username " + newUsername + " already exists");
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

        user.update(newUsername, newEmail, userUpdateRequest.newPassword(), updatedProfile);

        return userMapper.toResponse(user);
    }


    @Transactional
    @Override
    public UserResponse delete(UUID userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new NoSuchElementException(USER_NOT_FOUND + userId));

        Optional.ofNullable(user.getProfile()).ifPresent(profile -> {
            binaryContentRepository.delete(profile);
            binaryContentStorage.delete(profile.getId());
        });

        userRepository.deleteById(userId);
        userRepository.delete(user);

        return userMapper.toResponse(user);
    }

    private void validateDuplicate(String username, String email) {
        boolean usernameExists = userRepository.existsByUsername(username);
        boolean emailExists = userRepository.existsByEmail(email);

        if (usernameExists && emailExists) {
            throw new DuplicateUserException(DUPLICATE_BOTH);
        } else if (usernameExists) {
            throw new DuplicateUserException(DUPLICATE_USERNAME);
        } else if (emailExists) {
            throw new DuplicateUserException(DUPLICATE_EMAIL);
        }
    }
}
