package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.dto.User.UserCreateRequest;
import com.sprint.mission.discodeit.service.dto.User.UserFindRequest;
import com.sprint.mission.discodeit.service.dto.User.UserResponse;
import com.sprint.mission.discodeit.service.dto.User.UserUpdateRequest;
import java.time.Instant;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BasicUserService implements UserService {
    private final UserRepository userRepository;
    private final UserStatusRepository userStatusRepository;
    private final BinaryContentRepository binaryContentRepository;

    @Override
    public User create(UserCreateRequest request) {
        validateUniqueUser(request);

        User user = new User(request.username(), request.email(), request.password(), request.content());

        if (request.content()) { //주강사님이 동적정적 바인딩? 불러오는거 안배웠다고 null로 표기하라고 하셨습니다.
            BinaryContent profile = new BinaryContent(
                    null,
                    null,
                    null

            );
            binaryContentRepository.save(profile);
            user.setProfileId(profile.getId());
        }

        User savedUser = userRepository.save(user);

        UserStatus status = new UserStatus(
                Instant.now(),
                savedUser.getId()
        );
        userStatusRepository.save(status);

        return savedUser;
    }

    private void validateUniqueUser(UserCreateRequest request) {
        List<User> users = userRepository.findAll();
        for (User user : users) {
            if (user.getUsername().equals(request.username())) {
                throw new IllegalArgumentException("이미 존재하는 이름 입니다.");
            }
            if (user.getEmail().equals(request.email())) {
                throw new IllegalArgumentException("이미 존재하는 이메일 입니다.");
            }
        }
    }

    @Override
    public UserResponse find(UserFindRequest request) {
        User user = userRepository.findById(request.userId())
                .orElseThrow(() -> new NoSuchElementException("해당 id를 가진 유저는 없습니다."));

        boolean isOnline = userStatusRepository.findByUserId(user.getId())
                .map(status -> status.getUpdatedAt().isAfter(Instant.now().minusSeconds(300)))
                .orElse(false);

        return new UserResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                isOnline,
                user.isProfileImage()
        );
    }

    @Override
    public List<UserResponse> findAll() {
        return userRepository.findAll().stream()
                .map(user -> {
                    boolean isOnline = userStatusRepository.findByUserId(user.getId())
                            .map(status -> status.getUpdatedAt().isAfter(Instant.now().minusSeconds(300)))
                            .orElse(false);

                    return new UserResponse(
                            user.getId(),
                            user.getUsername(),
                            user.getEmail(),
                            isOnline,
                            user.isProfileImage()
                    );
                })
                .toList();
    }

    @Override
    public User update(UserUpdateRequest request) {
        User user = userRepository.findById(request.userId())
                .orElseThrow(() -> new NoSuchElementException("해당 id를 가진 유저는 없습니다."));

        user.update(request.newUsername(), request.newEmail(), request.newPassword());

        if (request.hasProfileImage()) {
            BinaryContent profile = new BinaryContent(
                    null,
                    null,
                    null
            );
            binaryContentRepository.save(profile);
            user.setProfileId(profile.getId());
        }

        userRepository.save(user);

        userStatusRepository.findByUserId(user.getId()).ifPresent(status -> {
            status.update(Instant.now());
            userStatusRepository.save(status);
        });

        return userRepository.save(user);
    }

    @Override
    public void delete(UUID userId) {
        if (!userRepository.existsById(userId)) {
            throw new NoSuchElementException("해당 id를 가진 유저는 없습니다.");
        }
        userRepository.deleteById(userId);

        if (userStatusRepository.existsById(userId)) {
            userStatusRepository.deleteById(userId);
        }
    }
}