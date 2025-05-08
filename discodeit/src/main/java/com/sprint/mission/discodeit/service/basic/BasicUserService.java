package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.binarycontent.AddBinaryContentRequest;
import com.sprint.mission.discodeit.dto.user.CreateUserRequest;
import com.sprint.mission.discodeit.dto.user.UpdateUserRequest;
import com.sprint.mission.discodeit.dto.user.UserDTO;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.BinaryContentType;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.UserStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BasicUserService implements UserService {
    private final UserRepository userRepository;
    private final UserStatusRepository userStatusRepository;
    private final BinaryContentRepository binaryContentRepository;
    private final UserStatusService userStatusService;


    @Override
    public User create(CreateUserRequest request, Optional<AddBinaryContentRequest> addBinaryContentRequest) {
        if (isUsernameDuplicate(request.username())) {
            throw new IllegalArgumentException("!! 이미 존재하는 유저 이름입니다 !!");
        }

        if (isEmailDuplicate(request.email())) {
            throw new IllegalArgumentException("!! 이미 존재하는 email 입니다 !!");
        }

        UUID nullableProfileId = addBinaryContentRequest
                .map(profileRequest -> {
                    String fileName = profileRequest.fileName();
                    BinaryContentType contentType = profileRequest.type();
                    byte[] bytes = profileRequest.bytes();
                    BinaryContent binaryContent = new BinaryContent(fileName, contentType, (long)bytes.length, bytes);
                    return binaryContentRepository.save(binaryContent).getId();
                })
                .orElse(null);
        User user = new User(request.username(),request.email(),request.password(),nullableProfileId);
        User newUser = userRepository.save(user);

        Instant now = Instant.now();
        UserStatus userStatus = new UserStatus(newUser.getId(),now);
        userStatusRepository.save(userStatus);
        return newUser;
    }


    @Override
    public UserDTO find(UUID userId) {
            User findUser = userRepository.findById(userId)
                    .orElseThrow(() -> new NoSuchElementException("User with id " + userId + " not found"));
            return toDTO(findUser);

    }

    @Override
    public List<User> findAll() {
        return userRepository.findAll()
                .stream()
                .map(this::toDTO)
                .toList();
    }

    @Override
    public User update(UUID userId, UpdateUserRequest updateUserRequest, Optional<AddBinaryContentRequest> addBinaryContentRequest) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("User with id " + userId + " not found"));

        UUID nullableProfileId = addBinaryContentRequest
                .map(profileRequest -> {
                    Optional.ofNullable(user.getProfiledId())
                            .ifPresent(binaryContentRepository::deleteById);
                    String fileName = profileRequest.fileName();
                    BinaryContentType contentType = profileRequest.type();
                    byte[] bytes = profileRequest.bytes();
                    BinaryContent binaryContent = new BinaryContent(fileName, contentType, (long)bytes.length, bytes);
                    return binaryContentRepository.save(binaryContent).getId();
                })
                .orElse(null);

        String newPassword = updateUserRequest.newPassword();
        user.update(updateUserRequest.newUsername(), updateUserRequest.newEmail(), updateUserRequest.newPassword(),nullableProfileId);
        return userRepository.save(user);
    }

    @Override
    public void delete(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("User with id " + userId + " not found"));

        // 관련 도메인도 삭제
        Optional.ofNullable(user.getProfiledId())
                .ifPresent(binaryContentRepository::deleteById);
        userStatusRepository.deleteByUserId(userId);
        userRepository.deleteById(userId);
        System.out.println("delete user : " + userId + " success.");

    }
    // username 중복 방지
    private boolean isUsernameDuplicate(String username) {
        return userRepository
                .findAll()
                .stream()
                .anyMatch(user -> user.getUsername().equals(username));
    }

    // email 중복 방지
    private boolean isEmailDuplicate(String email) {
        return userRepository
                .findAll()
                .stream()
                .anyMatch(user -> user.getEmail().equals(email));
    }



}
