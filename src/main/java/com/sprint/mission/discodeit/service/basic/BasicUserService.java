package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.UserDto;
import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserUpdateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
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

        String username = userRequest.username();
        String email = userRequest.email();
        String password = userRequest.password();

        if (isEmailDuplicate(email)) {
            throw new IllegalArgumentException("이미 존재하는 이메일입니다: " + email);
        }

        if (isNameDuplicate(username)) {
            throw new IllegalArgumentException("이미 존재하는 이름입니다: " + username);
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
    public Optional<UserDto> find(UUID id) {
        return userRepository.findById(id)
            .map(userMapper::toDto);
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

        User user = userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("해당 ID의 유저를 찾을 수 없습니다: " + userId));

        if (userRepository.existsByEmail(newEmail)) {
            throw new IllegalArgumentException("이미 존재하는 이름입니다: " + newEmail);
        }
        if (userRepository.existsByUsername(newUsername)) {
            throw new IllegalArgumentException(
                "이미 존재하는 이메일입니다: " + newUsername + " already exists");
        }

        BinaryContent newProfile = profileRequest
            .filter(BinaryContentCreateRequest::isValid)
            .map(req -> {
                BinaryContent newFile = binaryContentRepository.save(
                    new BinaryContent(req.fileName(), req.size(), req.contentType()));
                binaryContentStorage.put(newFile.getId(), req.bytes());
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
        User user = userRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("해당 ID의 유저를 찾을 수 없습니다: " + id));

        messageRepository.deleteByAuthorId(id);

        userRepository.deleteById(id);
       /*
       userStatusRepository.delete(id)
            User와 연관된 UserStatus는 orphanRemoval = true로 이미 설정되어 있으므로 별도 삭제 불필요.

        binaryContentRepository.delete(id)
            프로필 이미지도 연관관계에서 orphanRemoval = true를 활용.
        */
    }

    private boolean isEmailDuplicate(String email) {
        return userRepository.findAll().stream()
            .anyMatch(user -> user.getEmail().equalsIgnoreCase(email));
    }

    private boolean isNameDuplicate(String username) {
        return userRepository.findAll().stream()
            .anyMatch(user -> user.getUsername().equalsIgnoreCase(username));
    }
}