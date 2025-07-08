package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserUpdateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.exception.user.UserAlreadyExistsException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
public class BasicUserService implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final BinaryContentRepository binaryContentRepository;
    private final BinaryContentStorage binaryContentStorage;

    @Override
    public UserDto create(UserCreateRequest userCreateRequest,
        Optional<BinaryContentCreateRequest> profileCreateRequest) {
        log.debug("사용자 생성 시작 : {}", userCreateRequest);

        String username = userCreateRequest.username();
        String email = userCreateRequest.email();
        String password = userCreateRequest.password();

        // 유효성 검사( Unique Key )
        validateUniqueUsernameAndEmail(username, email);

        BinaryContent profile = profileCreateRequest
            .map(profileRequest -> {
                String fileName = profileRequest.fileName();
                String contentType = profileRequest.contentType();
                byte[] bytes = profileRequest.bytes();
                BinaryContent binaryContent = new BinaryContent(fileName, (long) bytes.length,
                    contentType);
                binaryContentRepository.save(binaryContent);
                binaryContentStorage.put(binaryContent.getId(), bytes);
                return binaryContent;
            })
            .orElse(null);

        User user = new User(username, email, password, profile);
        Instant now = Instant.now();

        UserStatus userStatus = new UserStatus(user, now);
        userRepository.save(user);

        log.info("사용자 생성 완료 : id = {}, username = {}", user.getId(), username);
        return userMapper.toDto(user);
    }

    @Override
    @Transactional(readOnly = true)
    public UserDto find(UUID userId) {
        log.debug("사용자 조회 시작 : id = {}", userId);
        UserDto userDto = userRepository.findById(userId)
            .map(userMapper::toDto)
            .orElseThrow(() -> UserNotFoundException.withId(userId));
        log.info("사용자 조회 완료 : id = {}", userId);
        return userDto;
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserDto> findAll() {
        log.debug("모든 사용자 조회 시작");
        List<UserDto> userDtos = userRepository.findAllWithProfileAndStatus()
            .stream()
            .map(userMapper::toDto)
            .toList();
        log.info("모든 사용자 조회 완료 : 총 {}명", userDtos.size());
        return userDtos;
    }

    @Override
    public UserDto update(UUID userId, UserUpdateRequest userUpdateRequest,
        Optional<BinaryContentCreateRequest> optionalProfileCreateRequest) {
        log.debug("사용자 수정 시작 : id = {}, request = {}", userId, userUpdateRequest);

        User user = userRepository.findById(userId)
            .orElseThrow(() -> new NoSuchElementException("User with id " + userId + " not found"));

        String newUsername = userUpdateRequest.newUsername();
        String newEmail = userUpdateRequest.newEmail();
        String newPassword = userUpdateRequest.newPassword();

        // 유효성 검사( Unique Key + Primary Key )
        validateUniqueUsernameAndEmail(newUsername, newEmail, user);

        BinaryContent profile = optionalProfileCreateRequest
            .map(profileRequest -> {

                String fileName = profileRequest.fileName();
                String contentType = profileRequest.contentType();
                byte[] bytes = profileRequest.bytes();
                BinaryContent binaryContent = new BinaryContent(fileName, (long) bytes.length,
                    contentType);
                binaryContentRepository.save(binaryContent);
                binaryContentStorage.put(binaryContent.getId(), bytes);
                return binaryContent;
            })
            .orElse(null);

        user.update(newUsername, newEmail, newPassword, profile);

        log.info("사용자 수정 완료 : id = {}", userId);
        return userMapper.toDto(user);
    }


    @Override
    public void delete(UUID userId) {
        log.debug("사용자 삭제 시작 : id = {}", userId);
        if (userRepository.existsById(userId)) {
            throw new NoSuchElementException("User with id " + userId + " not found");
        }

        userRepository.deleteById(userId);
        log.info("사용자 삭제 완료 : id = {}", userId);
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
            throw UserAlreadyExistsException.withUsername(username);
        }
        if (userRepository.existsByEmail(email)) {
            throw UserAlreadyExistsException.withEmail(email);
        }
    }

    // 유효성 검사( username, email, userId ) : update
    private void validateUniqueUsernameAndEmail(String newUsername, String newEmail,
        User currentUser) {
        if (newUsername != null &&
            !newUsername.equals(currentUser.getUsername()) &&
            userRepository.existsByUsername(newUsername)) {
            throw UserAlreadyExistsException.withUsername(newUsername);
        }
        if (newEmail != null &&
            !newEmail.equals(currentUser.getEmail()) &&
            userRepository.existsByEmail(newEmail)) {
            throw UserAlreadyExistsException.withEmail(newEmail);
        }
    }

}
