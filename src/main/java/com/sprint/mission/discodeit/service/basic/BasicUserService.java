package com.sprint.mission.discodeit.service.basic;

import static com.sprint.mission.discodeit.global.constant.ErrorMessages.DUPLICATE_BOTH;
import static com.sprint.mission.discodeit.global.constant.ErrorMessages.DUPLICATE_EMAIL;
import static com.sprint.mission.discodeit.global.constant.ErrorMessages.DUPLICATE_USERNAME;

import com.sprint.mission.discodeit.dto.data.UserDto;
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
    public UserDto find(UUID userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new NoSuchElementException("User with id " + userId + " not found"));

        return userMapper.toDto(user);
    }

    @Transactional(readOnly = true)
    @Override
    public List<UserDto> findAll() {
        return userRepository.findAll().stream()
            .map(userMapper::toDto)
            .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    @Override
    public UserResponse update(UUID userId, UserUpdateRequest request,
        Optional<BinaryContentCreateRequest> optionalProfileCreateRequest) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new NoSuchElementException("User with id " + userId + " not found"));

        validateDuplicate(request.newUsername(), request.newEmail());

        BinaryContent newProfile = optionalProfileCreateRequest
            .map(profile -> {
                Optional.ofNullable(user.getProfile())
                    .ifPresent(old -> {
                        binaryContentRepository.delete(old);
                        binaryContentStorage.delete(old.getId());
                    });
                BinaryContent entity = new BinaryContent(
                    profile.fileName(),
                    (long) profile.bytes().length,
                    profile.contentType()
                );
                BinaryContent saved = binaryContentRepository.save(entity);
                binaryContentStorage.put(saved.getId(), profile.bytes());
                return saved;
            })
            .orElse(user.getProfile());

        user.update(request.newUsername(), request.newEmail(), request.newPassword(), newProfile);

        return userMapper.toResponse(user);
    }

    @Transactional(readOnly = true)
    @Override
    public UserResponse delete(UUID userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new NoSuchElementException("User with id " + userId + " not found"));

        Optional.ofNullable(user.getProfile())
            .ifPresent(profile -> {
                binaryContentRepository.delete(profile);
                binaryContentStorage.delete(profile.getId());
            });
        userStatusRepository.deleteByUser_Id(userId);
        userRepository.deleteById(userId);

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
