package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.UserDto;
import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserUpdateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.user.UserEmailAlreadyExistException;
import com.sprint.mission.discodeit.exception.user.UserNameAlreadyExistException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class BasicUserService implements UserService {

    private final UserRepository userRepository;
    private final UserStatusRepository userStatusRepository;
    private final BinaryContentRepository binaryContentRepository;
    private final UserMapper userMapper;
    private final BinaryContentStorage binaryContentStorage;
    private final MessageRepository messageRepository;

    @Override
    @Transactional
    public UserDto createUser(UserCreateRequest userRequest,
        Optional<BinaryContentCreateRequest> profileRequest) {
        log.debug("createUser 호출 - username: {}, email: {}", userRequest.username(),
            userRequest.email());

        String username = userRequest.username();
        String email = userRequest.email();
        String password = userRequest.password();

        if (isEmailDuplicate(email)) {
            log.warn("중복 이메일로 생성 시도됨 - {}", userRequest.email());
            throw UserEmailAlreadyExistException.withEmail(email);
        }

        if (isNameDuplicate(username)) {
            log.warn("중복 사용자명으로 생성 시도됨 - {}", userRequest.email());
            throw UserNameAlreadyExistException.withUsername(username);
        }

        BinaryContent profile = profileRequest
            .filter(BinaryContentCreateRequest::isValid)
            .map(req -> {
                BinaryContent newFile = binaryContentRepository.save(
                    new BinaryContent(req.fileName(), req.size(), req.contentType()));
                binaryContentStorage.put(newFile.getId(), req.bytes());
                return newFile;
            })
            .orElse(null);

        User user = new User(username, email, password, profile);
        User newUser = userRepository.save(user);
        userStatusRepository.save(newUser.getStatus());

        return userMapper.toDto(newUser);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserDto> findAll() {
        return userRepository.findAll().stream()
            .map(userMapper::toDto)
            .toList();
    }

    @Override
    @Transactional
    public UserDto update(UUID userId, UserUpdateRequest userUpdateRequest,
        Optional<BinaryContentCreateRequest> profileRequest) {

        String newUsername = userUpdateRequest.newUsername();
        String newEmail = userUpdateRequest.newEmail();
        String newPassword = userUpdateRequest.newPassword();

        log.debug("사용자 업데이트 요청 - userId: {}, newUsername: {}, newEmail: {}", userId, newUsername,
            newEmail);
        User user = userRepository.findById(userId)
            .orElseThrow(() -> {
                log.error("업데이트 실패 - 존재하지 않는 사용자 ID: {}", userId);
                return UserNotFoundException.withId(userId);
            });

        if (userRepository.existsByEmail(newEmail)) {
            log.warn("업데이트 실패 - 중복 이메일: {}", newEmail);
            throw UserEmailAlreadyExistException.withEmail(newEmail);
        }
        if (userRepository.existsByUsername(newUsername)) {
            log.warn("업데이트 실패 - 중복 사용자 이름: {}", newUsername);
            throw UserNameAlreadyExistException.withUsername(newUsername);
        }

        BinaryContent newProfile = profileRequest
            .filter(BinaryContentCreateRequest::isValid)
            .map(req -> {
                BinaryContent newFile = binaryContentRepository.save(
                    new BinaryContent(req.fileName(), req.size(), req.contentType()));
                binaryContentStorage.put(newFile.getId(), req.bytes());
                log.info("프로필 이미지 저장 완료 - 파일 ID: {}", newFile.getId());
                return newFile;
            })
            .orElse(null);

        user.update(newUsername, newEmail, newPassword, newProfile);
        User updatedUser = userRepository.save(user);
        return userMapper.toDto(updatedUser);
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        log.debug("delete 호출 - userId: {}", id);
        User user = userRepository.findById(id)
            .orElseThrow(() -> {
                log.error("삭제 실패 - 존재하지 않는 userId: {}", id);
                return UserNotFoundException.withId(id);
            });

        messageRepository.deleteByAuthorId(id);
        userRepository.deleteById(id);
    }

    private boolean isEmailDuplicate(String email) {
        return userRepository.existsByEmail(email);
    }

    private boolean isNameDuplicate(String username) {
        return userRepository.existsByUsername(username);
    }
}