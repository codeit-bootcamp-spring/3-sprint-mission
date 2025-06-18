package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.data.BinaryContentDto;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserUpdateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.mapper.BinaryContentMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import java.io.IOException;
import java.io.InputStream;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Transactional
public class BasicUserService implements UserService {

    private final UserRepository userRepository;
    private final UserStatusRepository userStatusRepository;
    private final BinaryContentRepository binaryContentRepository;
    private final BinaryContentStorage binaryContentStorage;
    private final BinaryContentMapper binaryContentMapper;

    @Override
    public UserDto create(UserCreateRequest userCreateRequest,
        Optional<BinaryContentCreateRequest> profileCreateRequest) {

        String username = userCreateRequest.username();
        String email = userCreateRequest.email();
        String password = userCreateRequest.password();

        // 유효성 검사( Unique Key )
        validateUniqueUsernameAndEmail(username, email);

        BinaryContent profile = profileCreateRequest.map(this::saveProfile).orElse(null);

        User user = new User(username, email, password, profile);
        User createdUser = userRepository.save(user);

        Instant now = Instant.now();
        userStatusRepository.save(new UserStatus(createdUser, now));

        return toDto(createdUser);
    }

    @Override
    @Transactional(readOnly = true)
    public UserDto find(UUID userId) {
        return userRepository.findById(userId)
            .map(this::toDto)
            .orElseThrow(() -> new NoSuchElementException("User with id " + userId + " not found"));
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserDto> findAll() {
        return userRepository.findAll()
            .stream()
            .map(this::toDto)
            .toList();
    }

    @Override
    public UserDto update(UUID userId, UserUpdateRequest userUpdateRequest,
        Optional<BinaryContentCreateRequest> optionalProfileCreateRequest) {

        User user = userRepository.findById(userId)
            .orElseThrow(() -> new NoSuchElementException("User with id " + userId + " not found"));

        String newUsername = userUpdateRequest.newUsername();
        String newEmail = userUpdateRequest.newEmail();
        String newPassword = userUpdateRequest.newPassword();

        // 유효성 검사( Unique Key + Primary Key )
        validateUniqueUsernameAndEmail(newUsername, newEmail, user);

        BinaryContent newProfile = optionalProfileCreateRequest.map(request -> {

            if (request.bytes() == null) {
                return user.getProfile();
            }

            Optional.ofNullable(user.getProfile()).ifPresent(binaryContentRepository::delete);
            return saveProfile(request);
        }).orElse(null);

        user.update(newUsername, newEmail, newPassword, newProfile);

        return toDto(user);
    }


    @Override
    public void delete(UUID userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new NoSuchElementException("User with id " + userId + " not found"));

        Optional.ofNullable(user.getProfile())
            .ifPresent(binaryContentRepository::delete);
        userStatusRepository.deleteByUserId(userId);

        userRepository.deleteById(userId);
    }

    private UserDto toDto(User user) {
        Boolean online = userStatusRepository.findByUserId(user.getId())
            .map(UserStatus::isOnline)
            .orElse(null);

        BinaryContent profile = user.getProfile();
        BinaryContentDto profileDto = null;

        if (profile != null) {
            try (InputStream is = binaryContentStorage.get(profile.getId())) {
                byte[] bytes = is.readAllBytes();

                profileDto = new BinaryContentDto(
                    profile.getId(),
                    profile.getFileName(),
                    profile.getSize(),
                    profile.getContentType(),
                    bytes
                );
            } catch (IOException e) {
                throw new RuntimeException("Failed to load profile image bytes", e);
            }
        }

        return new UserDto(
            user.getId(),
            user.getUsername(),
            user.getEmail(),
            profileDto,
            online
        );
    }


    // 프로필 저장
    private BinaryContent saveProfile(BinaryContentCreateRequest profileRequest) {
        BinaryContent binaryContent = new BinaryContent(
            profileRequest.fileName(),
            (long) profileRequest.bytes().length,
            profileRequest.contentType()
        );

        BinaryContent savedBinaryContent = binaryContentRepository.save(binaryContent);
        binaryContentStorage.put(savedBinaryContent.getId(), profileRequest.bytes());
        return savedBinaryContent;
    }

    // 유효성 검사( username, email ) : create
    private void validateUniqueUsernameAndEmail(String username, String email) {
        if (userRepository.existsByUsername(username)) {
            throw new IllegalArgumentException(
                "User with username " + username + " already exists");
        }
        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("User with email " + email + " already exists");
        }
    }

    // 유효성 검사( username, email, userId ) : update
    private void validateUniqueUsernameAndEmail(String newUsername, String newEmail,
        User currentUser) {
        if (newUsername != null &&
            !newUsername.equals(currentUser.getUsername()) &&
            userRepository.existsByUsername(newUsername)) {
            throw new IllegalArgumentException(
                "User with username " + newUsername + " already exists");
        }
        if (newEmail != null &&
            !newEmail.equals(currentUser.getEmail()) &&
            userRepository.existsByEmail(newEmail)) {
            throw new IllegalArgumentException("User with email " + newEmail + " already exists");
        }
    }

}
